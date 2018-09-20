package com.yunche.loan.service.impl;

import com.yunche.loan.config.cache.TokenCache;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.InstallGpsDO;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.config.util.CarLoanHttpUtil;
import com.yunche.loan.config.util.OpenApiUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.GpsUpdateParam;
import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.AuxiliaryService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@Service
public class AuxiliaryServiceImpl implements AuxiliaryService {

    private static final Logger logger = LoggerFactory.getLogger(AuxiliaryServiceImpl.class);


    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private InstallGpsDOMapper installGpsDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private TokenCache tokenCache;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Resource
    private PartnerDOMapper partnerDOMapper;


    @Override
    @Transactional
    public void commit(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }
        Long foundationId = loanOrderDO.getLoanCarInfoId();
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(foundationId);
        if (loanCarInfoDO == null) {
            throw new BizException("此车辆贷款信息不存在");
        }
        loanCarInfoDO.setCarKey(new Byte("1"));
        loanCarInfoDOMapper.updateByPrimaryKey(loanCarInfoDO);
    }
    @Override
    public String getGpsAddress(String gpsCode){
        String result ="";
        try{
            String accToken = getAccToken();
            result = OpenApiUtil.getGpsAddress(accToken,gpsCode);
            while("1004".equals(result)){
                result = OpenApiUtil.getGpsAddress(getAccToken(),gpsCode);
            }
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new BizException(e.getMessage());
        }
        return result;
    }


    @Override
    @Transactional
    public void install(InstallUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }
        PartnerDO partnerDO = partnerDOMapper.queryLeaderNameById(Long.valueOf(param.getOrder_id()));
        for (GpsUpdateParam obj : param.getGps_list()) {
            int i = installGpsDOMapper.selectBygpsNumber(obj.getGps_number());
            if (i > 0) {
                throw new BizException("该GPS：" + obj.getGps_number() + "已存在");
            } else {
                if ("JIMI".equals(param.getGpsCompany())) {
                    try {
                        String accToken = getAccToken();
                        boolean falg = false;
                        List<Map<String, Object>> list = OpenApiUtil.getGpsDetailInfo(accToken, obj.getGps_number());
                        if (list.size() > 0) {
                            while ("1004".equals((String) list.get(0).get("code"))) {
                                list = OpenApiUtil.getGpsDetailInfo(getAccToken(), obj.getGps_number());
                            }
                        }
                        for (Map<String, Object> map1 : list) {
                            if ((map1.get("activationTime") != null || !"".equals(map1.get("activationTime")))
                                    && (map1.get("vehicleName") == null || "".equals(map1.get("vehicleName")))
                                    && (map1.get("driverName") == null || "".equals(map1.get("driverName")))) {
                                String account = (String)map1.get("account");
                                if(account != null && !"".equals(account)){
                                    if(partnerDO.getGpsAccount().trim().equals(account.trim())){
                                        falg = true;
                                    } else {
                                        throw new BizException("第三方该gps所属人与本地合伙人不符");
                                    }
                                } else {
                                    throw new BizException("第三方该gps不属于该合伙人");
                                }

                            } else {
                                throw new BizException("第三方该gps信息以绑定用户");
                            }
                        }
                        if (falg) {
                            String result = OpenApiUtil.updateGpsInfo(accToken, obj.getGps_number(), param.getVehicleName(), param.getDriverName());
                            while ("1004".equals(result)) {
                                result = OpenApiUtil.updateGpsInfo(getAccToken(), obj.getGps_number(), param.getVehicleName(), param.getDriverName());
                            }
                        } else {
                            throw new BizException("第三方该gps信息不存在");
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new BizException(e.getMessage());
                    }
                } else if ("CARLOAN".equals(param.getGpsCompany())) {
                    try {
                        boolean flag = CarLoanHttpUtil.getGpsStatus(obj.getGps_number());
                        if (flag) {
                            PartnerCusInfoVO partnerCusInfoVO = installGpsDOMapper.selectPartnerAndCusByOrderId(Long.valueOf(param.getOrder_id()));
                            boolean cusFlag = CarLoanHttpUtil.modifyCustomer(String.valueOf(partnerCusInfoVO.getPId()),String.valueOf(partnerCusInfoVO.getCusId()),param.getDriverName(),param.getVehicleName().replaceAll(" ",""));
                            if(cusFlag){
                                boolean gpsFlag = CarLoanHttpUtil.bindGps(obj.getGps_number(),String.valueOf(partnerCusInfoVO.getCusId()));
                                if(!gpsFlag){
                                    throw new BizException("该GPS:" + obj.getGps_number() + "绑定失败");
                                }
                            }else{
                                throw new BizException("该客户:" + param.getDriverName() + "无法登记");
                            }
                        } else {
                            throw new BizException("该GPS:" + obj.getGps_number() + "无法登记");
                        }
                    } catch (Exception e) {
                        logger.error("车贷管家系统通讯异常", e);
                        throw new BizException(e.getMessage());
                    }
                }else if("All".equals(param.getGpsCompany())){
                    try {
                        boolean flag = CarLoanHttpUtil.getGpsStatus(obj.getGps_number());
                        if (flag) {
                            PartnerCusInfoVO partnerCusInfoVO = installGpsDOMapper.selectPartnerAndCusByOrderId(Long.valueOf(param.getOrder_id()));
                            boolean cusFlag = CarLoanHttpUtil.modifyCustomer(String.valueOf(partnerCusInfoVO.getPId()),String.valueOf(partnerCusInfoVO.getCusId()),param.getDriverName(),param.getVehicleName().replaceAll(" ",""));
                            if(cusFlag){
                                boolean gpsFlag = CarLoanHttpUtil.bindGps(obj.getGps_number(),String.valueOf(partnerCusInfoVO.getCusId()));
                                if(!gpsFlag){
                                    throw new BizException("该GPS:" + obj.getGps_number() + "绑定失败");
                                }
                                param.setGpsCompany("CARLOAN");
                            }else{
                                throw new BizException("该客户:" + param.getDriverName() + "无法登记");
                            }
                        } else {
                            try {
                                String accToken = getAccToken();
                                boolean falg = false;
                                List<Map<String, Object>> list = OpenApiUtil.getGpsDetailInfo(accToken, obj.getGps_number());
                                if (list.size() > 0) {
                                    while ("1004".equals((String) list.get(0).get("code"))) {
                                        list = OpenApiUtil.getGpsDetailInfo(getAccToken(), obj.getGps_number());
                                    }
                                }
                                for (Map<String, Object> map1 : list) {
                                    if ((map1.get("activationTime") != null || !"".equals(map1.get("activationTime")))
                                            && (map1.get("vehicleName") == null || "".equals(map1.get("vehicleName")))
                                            && (map1.get("driverName") == null || "".equals(map1.get("driverName")))) {
                                        String account = (String)map1.get("account");
                                        if(account != null && !"".equals(account)){
                                            if(partnerDO.getGpsAccount().trim().equals(account.trim())){
                                                falg = true;
                                            } else {
                                                throw new BizException("第三方该gps所属人与本地合伙人不符");
                                            }
                                        } else {
                                            throw new BizException("第三方该gps不属于该合伙人");
                                        }

                                    } else {
                                        throw new BizException("第三方该gps信息以绑定用户");
                                    }
                                }
                                if (falg) {
                                    String result = OpenApiUtil.updateGpsInfo(accToken, obj.getGps_number(), param.getVehicleName(), param.getDriverName());
                                    while ("1004".equals(result)) {
                                        result = OpenApiUtil.updateGpsInfo(getAccToken(), obj.getGps_number(), param.getVehicleName(), param.getDriverName());
                                    }
                                } else {
                                    throw new BizException("第三方该gps信息不存在");
                                }
                                param.setGpsCompany("JIMI");
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                throw new BizException(e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("GPS系统通讯异常", e);
                        throw new BizException(e.getMessage());
                    }
                }
                InstallGpsDO T = BeanPlasticityUtills.copy(InstallGpsDO.class, obj);
                T.setOrder_id(Long.valueOf(param.getOrder_id()));
                T.setGps_company(param.getGpsCompany());
                installGpsDOMapper.insertSelective(T);
            }
        }
    }


    @Override
    @Transactional
    public List<GpsVO> query(Long orderId) {
        List<GpsVO> list = new ArrayList<GpsVO>();
        List<GpsVO> result = loanQueryDOMapper.selectGpsByOrderId(orderId);
        if (result == null) {
            return list;
        }
        return result;
    }

    @Override
    @Transactional
    public GpsDetailTotalVO detail(Long orderId) {
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        GpsDetailTotalVO gpsDetailTotal = new GpsDetailTotalVO();
        gpsDetailTotal.setCustomers(customers);

        GpsDetailVO gpsDetail = new GpsDetailVO();
        gpsDetail = loanQueryDOMapper.selectGpsDetailByOrderId(orderId);
        if (gpsDetail.getLicensePlateNymber() == null) {
            gpsDetail.setLicensePlateNymber("");
        }
        gpsDetailTotal.setGpsDetail(gpsDetail);


        //gps信息
        List<GpsVO> result = new ArrayList<GpsVO>();
        result.addAll(loanQueryDOMapper.selectGpsByOrderId(orderId));
        for (int i = result.size(); i < gpsDetail.getGpsNum(); i++) {
            GpsVO g = new GpsVO();
            g.setGps_number("");
            g.setOrder_id("");
            result.add(g);
        }
        gpsDetailTotal.setGpsNum(result);
        //征信
        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }
        gpsDetailTotal.setCredits(credits);

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);
        //贷款信息
        gpsDetailTotal.setInfo(universalInfoVO);
        return gpsDetailTotal;
    }

    @Override
    public GpsDetailTotalVO appDetail(Long orderId) {
        GpsDetailTotalVO gpsDetailTotal = new GpsDetailTotalVO();
        GpsDetailVO gpsDetail = new GpsDetailVO();
        gpsDetail = loanQueryDOMapper.selectGpsDetailByOrderId(orderId);
        if (gpsDetail.getLicensePlateNymber() == null) {
            gpsDetail.setLicensePlateNymber("");
        }
        gpsDetailTotal.setGpsDetail(gpsDetail);
        //gps信息
        List<GpsVO> result = new ArrayList<GpsVO>();
        result.addAll(loanQueryDOMapper.selectGpsByOrderId(orderId));
        for (int i = result.size(); i < gpsDetail.getGpsNum(); i++) {
            GpsVO g = new GpsVO();
            g.setGps_number("");
            g.setOrder_id("");
            result.add(g);
        }
        gpsDetailTotal.setGpsNum(result);
        return gpsDetailTotal;
    }

    private String getAccToken() throws Exception {
        String[] tokens = tokenCache.getToken();

        /*String[] tokens = new String[3];
        tokens[0]="";
        tokens[1]="";
        tokens[2]="";*/
        String accToken = "";

        if ("".equals(tokens[0])) {
            String[] tokenStr = OpenApiUtil.getToken();
            if ("1006".equals(tokenStr[0])) {
                while (true) {
                    String repToken = tokenCache.getToken()[0];
                    if (!"".equals(repToken)) {
                        accToken = repToken;
                        break;
                    }
                }
            } else {
                accToken = tokenStr[0];
                tokenCache.insertToken(tokenStr[0], tokenStr[1]);
            }
        } else {
            accToken = tokens[0];
        }
        return accToken;
    }

    @Override
    public List<GpsJimiInfoVO> queryOther(String partnerName) {
        List<GpsJimiInfoVO> list = new ArrayList<GpsJimiInfoVO>();
        return list;
    }

    @Override
    public List<GpsJimiInfoVO> queryJimi(String partnerName) {
        List<Map<String, Object>> result = null;
        try {
            String accToken = getAccToken();
            PartnerDO partnerDO = partnerDOMapper.queryByPartnerName(partnerName);
            if (null != partnerDO) {
                String leaderName = partnerDO.getLeaderName().trim();
                String target = "";
                List<Map<String, Object>> list = OpenApiUtil.getChildTarget(accToken);
                if (list.size() > 0) {
                    while ("1004".equals((String) list.get(0).get("code"))) {
                        list = OpenApiUtil.getChildTarget(getAccToken());
                    }
                }
                if (list.size() > 0) {
                    for (Map<String, Object> m : list) {
                        String name = (String) m.get("name");
                        if (leaderName.trim().equals(name.trim())) {
                            target = (String) m.get("account");
                            break;
                        }
                    }
                }
                if (!"".equals(target)) {
                    result = OpenApiUtil.getGpsInfo(accToken, target);
                    while ("1004".equals((String) list.get(0).get("code"))) {
                        result = OpenApiUtil.getGpsInfo(getAccToken(), target);
                    }
                } else {
                    logger.info("团队领导人和GPS系统匹配失败");
                }
            } else {
                logger.info("获取团队领导人失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BizException(e.getMessage());
        }
        List<GpsJimiInfoVO> list = new ArrayList<GpsJimiInfoVO>();
        if (result.size() > 0) {
            result.stream().forEach(e -> {
                GpsJimiInfoVO gpsJimiInfoVO = new GpsJimiInfoVO();
                gpsJimiInfoVO.setGpsId((String) e.get("imei"));
                //1为激活 0未激活
                gpsJimiInfoVO.setActivationState(e.get("activationTime") == null ? "0" : "1");
                gpsJimiInfoVO.setDriverName((String) e.get("driverName"));
                gpsJimiInfoVO.setVehicleName((String) e.get("vehicleName"));
                list.add(gpsJimiInfoVO);
            });
        }
        return list;
    }
}
