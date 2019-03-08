package com.yunche.loan.estage.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.EstageMaterialsCodeEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.estage.DefaultEstageProcessor;
import com.yunche.loan.estage.EstageServiceEnum;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: e分期征信申请服务
 * author: yu.hb
 * Date: 2019-03-07
 */
@Component
public class EstageCreditApplyProcessor extends DefaultEstageProcessor {
    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Override
    public JSONObject assemblyReq(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        List<LoanCustomerDO> customerDOs = loanCustomerDOMapper.selectAllcustAndFiles(loanOrderDO.getLoanCustomerId());
        if (CollectionUtils.isEmpty(customerDOs)) {
            throw new BizException("借款人信息不能为空");
        }
        // 主贷人信息
        LoanCustomerDO masterCustomer = customerDOs.stream().filter(customerDO -> BaseConst.MASTER_CUST.equals(customerDO.getCustType())).collect(Collectors.toList()).get(0);
        // 关联人信息
        List<LoanCustomerDO> spouseInfos = customerDOs.stream().filter(customerDO -> {
            return BaseConst.COMM_CUST.equals(customerDO.getCustType()) || BaseConst.ASSURE_CUST.equals(customerDO.getCustType());
        }).collect(Collectors.toList());

        JSONObject req = new JSONObject();
        req.put("carType","0");//业务品种 0：新车，1：二手车，2：新能源车
        req.put("downloadMode","1");
        req.put("intentionPrice","50000"); // 意向价格
        req.put("lender",customerInfo(masterCustomer));
        req.put("spouse",spouseInfos(spouseInfos));
        return req;
    }

    private JSONArray spouseInfos(List<LoanCustomerDO> spouseInfos) {
        JSONArray spouseArray = new JSONArray();
        if (CollectionUtils.isEmpty(spouseInfos)) {
            return spouseArray;
        }

        spouseInfos.forEach(customerDO -> {
            JSONObject customerJson = customerInfo(customerDO);
            spouseArray.add(customerJson);
        });
        return spouseArray;
    }

    private JSONObject customerInfo(LoanCustomerDO customerDOS) {
        String identityValidity = customerDOS.getIdentityValidity();
        if (StringUtils.isEmpty(identityValidity)) {
            throw new BizException("证件有效期不能为空");
        }
        String[] dates = StringUtils.split(identityValidity, "-");

        JSONObject customer = new JSONObject();
        customer.put("idCard",customerDOS.getIdCard());
        customer.put("issueAuthority",customerDOS.getIssuingDepartment()); //身份证签发机关
        customer.put("phoneNum",customerDOS.getMobile());
        customer.put("userName",customerDOS.getName());

        customer.put("startDate",dates[0]); //证件有限期起始日
        customer.put("endDate","长期".equals(dates[1]) ? "9999.12.30" : dates[1]); //证件有限期截止日
        customer.put("familyAddress",customerDOS.getAddress());
        customer.put("pics",getPics(customerDOS.getLoanFileDOS()));

        if (!BaseConst.MASTER_CUST.equals(customerDOS.getCustType())) { // 非主贷人额外字段
            customer.put("relationShip",converRelation(customerDOS.getCustRelation()));
        }
        return customer;
    }

    private JSONArray getPics(List<LoanFileDO> loanFileDOS) {
        JSONArray pics = new JSONArray();
        if (CollectionUtils.isEmpty(loanFileDOS)) {
            return pics;
        }

        loanFileDOS.forEach(loanFileDO -> {
            JSONArray fileArray = JSONArray.parseArray(loanFileDO.getPath());
            String address = BaseConst.OSS_PREFIX.concat("/").concat(fileArray.getString(0));

            JSONObject pic = new JSONObject();
            pic.put("picFileName", StringUtils.substring(address,address.lastIndexOf("/") + 1));
            pic.put("picAddress", address);
            pic.put("picId",loanFileDO.getId());
            pic.put("picCode", EstageMaterialsCodeEnum.getMatrCode(loanFileDO.getType()));
            pics.add(pic);
        });
        return pics;
    }

    private Integer converRelation(Byte custRelation) {
        Integer relation = Integer.valueOf(custRelation);
        if (relation == 6) {
            return 8;
        } else if (relation == 7) {
            return 6;
        } else if (relation == 8) {
            return 9;
        }
        return relation;
    }

    @Override
    public String buildBusiCode() {
        return EstageServiceEnum.CREDIT_APPLY.getBusiCode();
    }
}
