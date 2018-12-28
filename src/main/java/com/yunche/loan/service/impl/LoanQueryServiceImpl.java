package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.DictService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanQueryServiceImpl implements LoanQueryService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanProcessDOMapper loanProcessDOMapper;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private DictService dictService;


    @Override
    public boolean selectCheckOrderInBankInterfaceSerial(Long orderId, String transCode) {
        return loanQueryDOMapper.selectCheckOrderInBankInterfaceSerial(orderId, transCode);
    }

    @Override
    public UniversalCustomerDetailVO universalCustomerDetail(Long customerId) {

        return loanQueryDOMapper.selectUniversalCustomerDetail(loanQueryDOMapper.selectOrderIdbyPrincipalCustId(customerId), customerId);
    }

    @Override
    public String selectTelephoneVerifyLevel() {
        return loanQueryDOMapper.selectTelephoneVerifyLevel(SessionUtils.getLoginUser().getId());
    }

    @Override
    public Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId, String transCode) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (loanOrderDO == null) {
            throw new BizException("此订单不存在");
        }

        List<BankInterFaceSerialOrderStatusVO> list = loanQueryDOMapper.selectBankInterFaceSerialOrderStatusByOrderId(orderId, transCode);
        if (CollectionUtils.isEmpty(list)) {
            return 1;
        }

        for (BankInterFaceSerialOrderStatusVO V : list) {
            //如果有一个为退回，就会被打回去 打回的单子是可以操作的
            if ("3".equals(V.getStatus())) {
                return 1;
            }
        }

        for (BankInterFaceSerialOrderStatusVO V : list) {
            if (!"200".equals(V.getApi_status())) {
                return 3;
            }

            if ((!"0".equals(V.getStatus()) && !"1".equals(V.getStatus()) && !"2".equals(V.getStatus()) && !"3".equals(V.getStatus()))) {
                //有一个推送失败
                return 3;
            }
        }

        for (BankInterFaceSerialOrderStatusVO V : list) {

            if ("2".equals(V.getStatus()) && "200".equals(V.getApi_status())) {
                //有一个是处理中 且其他不等于处理失败
                return 2;
            }
        }


        for (BankInterFaceSerialOrderStatusVO V : list) {

            if (!("0".equals(V.getStatus()) || "1".equals(V.getStatus()) && "200".equals(V.getApi_status()))) {
                //有一个是处理中 且其他不等于处理失败
                return 4;
            }
        }


        return 1;
    }

    @Override
    public Integer selectBankOpenCardStatusByOrderId(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (loanOrderDO == null) {
            throw new BizException("此订单不存在");
        }
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Byte t = loanProcessDO.getTelephoneVerify();
        if (t == null) {
            t = new Byte("0");
        }
        String x = t.toString();

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), new Byte("0"));
        String y = loanCustomerDO.getOpenCardOrder();
        if (StringUtils.isBlank(y)) {
            throw new BizException("缺少开卡顺序");
        }

        //如果
        if (y.equals("0")) {
            if (x.equals("1")) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 1;
        }
    }

    @Override
    public void checkBankInterFaceSerialStatus(Long customerId, String transCode) {
        if (IDict.K_TRANS_CODE.MULTIMEDIAUPLOAD.equals(transCode)) {
            return;
        }
        if (customerId == null) {
            throw new BizException("客户id不存在");
        }
        if (StringUtils.isBlank(transCode)) {
            throw new BizException("transCode 不存在");
        }
        UniversalBankInterfaceSerialVO result = loanQueryDOMapper.selectUniversalLatestBankInterfaceSerial(customerId, transCode);
        if (result != null) {
            if (IDict.K_JJSTS.PROCESS.equals(result.getStatus())) {
                throw new BizException("请耐心等待上一次操作的结果通知");
            }
        }
    }


    @Override
    public String selectLastBankInterfaceSerialNoteByTransCode(Long customerId, String transCode) {
        if (customerId == null) {
            throw new BizException("客户id不存在");
        }
        if (StringUtils.isBlank(transCode)) {
            throw new BizException("transCode 不存在");
        }

        return loanQueryDOMapper.selectLastBankInterfaceSerialNoteByTransCode(customerId, transCode);
    }


    @Override
    public BankInterfaceSerialReturnVO selectLastBankInterfaceSerialByTransCode(Long customerId, String transCode) {
        if (customerId == null) {
            throw new BizException("客户id不存在");
        }
        if (StringUtils.isBlank(transCode)) {
            throw new BizException("transCode 不存在");
        }

        return loanQueryDOMapper.selectLastBankInterfaceSerialByTransCode(customerId, transCode);
    }

    @Override
    public UniversalInfoSupplementVO selectUniversalInfoSupplementDetail(Long infoSupplementId) {
        Preconditions.checkNotNull(infoSupplementId, "增补单ID不能为空");

        // getAll
        List<UniversalInfoSupplementVO> universalInfoSupplementVOList = loanQueryDOMapper.selectUniversalInfoSupplement(infoSupplementId);

        // check
        Preconditions.checkArgument(!CollectionUtils.isEmpty(universalInfoSupplementVOList), "增补单不存在");

        // group by
        List<UniversalInfoSupplementVO> infoSupplementVOList = groupByInfoSupplementId(universalInfoSupplementVOList);

        return infoSupplementVOList.get(0);
    }

    @Override
    public List<UniversalInfoSupplementVO> selectUniversalInfoSupplementHistory(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        // getAll
        List<UniversalInfoSupplementVO> universalInfoSupplementVOList = loanQueryDOMapper.selectUniversalCollectionInfoSupplement(orderId);

        // group by
        List<UniversalInfoSupplementVO> infoSupplementVOList = groupByInfoSupplementId(universalInfoSupplementVOList);

        // sort
        List<UniversalInfoSupplementVO> sortList = sortByEndTime(infoSupplementVOList);

        return sortList;
    }

    /**
     * 当前客户的 文件列表  （包含：正常上传 & 增补上传，且已根据upload_type作了聚合）
     *
     * @param customerId
     * @return
     */
    @Override
    public List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId) {

        List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(customerId);

        // type聚合  ==>  正常上传 & 增补上传
        List<UniversalCustomerFileVO> files_ = convert(files);

        return files_;
    }

    @Override
    public String selectVideoFacePath(Long orderId) {

        String path = loanQueryDOMapper.selectVideoFacePath(orderId);
        if (StringUtils.isNotBlank(path)) {
            path = path.replace("https://yunche-videosign.oss-cn-hangzhou.aliyuncs.com/", "");
            path = path.replace("http://yunche-videosign.oss-cn-hangzhou.aliyuncs.com/", "");
            path.trim();
        }

        return path;
    }

    /**
     * 根据文件type -> 作聚合  (原始files 包含了所有的upload_type，所以会有重复type的情况)
     *
     * @param files
     * @return
     */
    private List<UniversalCustomerFileVO> convert(List<UniversalCustomerFileVO> files) {

        if (CollectionUtils.isEmpty(files)) {
            return Collections.EMPTY_LIST;
        }

        Map<String, UniversalCustomerFileVO> typeFilesMap = Maps.newHashMap();

        files.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    String type = e.getType();

                    if (typeFilesMap.containsKey(type)) {

                        UniversalCustomerFileVO universalCustomerFileVO = typeFilesMap.get(type);

                        List<String> addurls = universalCustomerFileVO.getAddurls();

                        addurls.addAll(e.getUrls());

                    } else {
                        typeFilesMap.put(type, e);
                    }

                });


        List<UniversalCustomerFileVO> universalCustomerFileVOList = Lists.newArrayList(typeFilesMap.values());
        return universalCustomerFileVOList;
    }

    private List<UniversalInfoSupplementVO> groupByInfoSupplementId(List<UniversalInfoSupplementVO> infoSupplementVOList) {

        if (CollectionUtils.isEmpty(infoSupplementVOList)) {
            return Collections.EMPTY_LIST;
        }

        Map<Long, UniversalInfoSupplementVO> idDetailMap = Maps.newHashMap();

        infoSupplementVOList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long infoSupplementId = e.getSupplementOrderId();

                    if (idDetailMap.containsKey(infoSupplementId)) {

                        UniversalInfoSupplementVO infoSupplementVO = idDetailMap.get(infoSupplementId);

                        List<FileVO2> files = infoSupplementVO.getFiles();

                        // file
                        FileVO2 fileVO = new FileVO2();
                        fileVO.setFileId(e.getFileId());
                        fileVO.setType(e.getFileType());
                        fileVO.setName(e.getFileTypeText());
                        fileVO.setUrls(e.getFilePath());

                        // add files
                        files.add(fileVO);

                    } else {

                        UniversalInfoSupplementVO infoSupplementVO = new UniversalInfoSupplementVO();

                        BeanUtils.copyProperties(e, infoSupplementVO);

                        // files
                        FileVO2 fileVO = new FileVO2();
                        fileVO.setFileId(e.getFileId());
                        fileVO.setType(e.getFileType());
                        fileVO.setName(e.getFileTypeText());
                        fileVO.setUrls(e.getFilePath());

                        infoSupplementVO.setFiles(Lists.newArrayList(fileVO));

                        idDetailMap.put(infoSupplementId, infoSupplementVO);
                    }

                });

        // val
        Collection<UniversalInfoSupplementVO> values = idDetailMap.values();
        return Lists.newArrayList(values);
    }

    /**
     * sort
     *
     * @param infoSupplementVOList
     */
    private List<UniversalInfoSupplementVO> sortByEndTime(List<UniversalInfoSupplementVO> infoSupplementVOList) {

        if (!CollectionUtils.isEmpty(infoSupplementVOList)) {

            List<UniversalInfoSupplementVO> sortList = infoSupplementVOList.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(UniversalInfoSupplementVO::getStartTime).reversed())
                    .collect(Collectors.toList());

            return sortList;
        }

        return infoSupplementVOList;
    }

    /**
     * 增补类型文本值      -用存储函数
     *
     * @param supplementType
     * @return
     */
    @Deprecated
    public String getInfoSupplementTypeText(Byte supplementType) {

        Map<String, String> kvMap = dictService.getKVMap("infoSupplementType");

        return getInfoSupplementTypeText(supplementType, kvMap);
    }

    /**
     * 增补类型文本值      -用存储函数
     *
     * @param supplementType
     * @param kvMap
     * @return
     */
    @Deprecated
    public String getInfoSupplementTypeText(Byte supplementType, Map<String, String> kvMap) {

        if (CollectionUtils.isEmpty(kvMap)) {
            kvMap = dictService.getKVMap("infoSupplementType");
        }

        String supplementTypeText = kvMap.get(String.valueOf(supplementType));

        return supplementTypeText;
    }
}
