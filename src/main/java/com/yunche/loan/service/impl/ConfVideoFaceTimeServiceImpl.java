package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.domain.vo.ConfVideoFaceTimeVO;
import com.yunche.loan.mapper.ConfVideoFaceTimeDOMapper;
import com.yunche.loan.service.ConfVideoFaceTimeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.yunche.loan.config.constant.VideoFaceConst.ADMIN_VIDEO_FACE_BANK_ID;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@Service
public class ConfVideoFaceTimeServiceImpl implements ConfVideoFaceTimeService {

    private static final String SEPARATOR = "-";

    @Autowired
    private ConfVideoFaceTimeDOMapper confVideoFaceTimeDOMapper;

    @Autowired
    private BankCache bankCache;


    @Override
    @Transactional
    public void save(List<ConfVideoFaceTimeVO> confVideoFaceTimeVOS) {

        Long videoFaceBankId = SessionUtils.getLoginUser().getBankId();
        Preconditions.checkNotNull(videoFaceBankId, "您无权操作[视频面签]");

        // del ALL
        deleteAllConfByBankId(videoFaceBankId);

        // save
        doSave(videoFaceBankId, confVideoFaceTimeVOS);
    }

    @Override
    public List<ConfVideoFaceTimeVO> listAll() {

        Long videoFaceBankId = SessionUtils.getLoginUser().getBankId();
        Preconditions.checkNotNull(videoFaceBankId, "您无权操作[视频面签]");

        List<ConfVideoFaceTimeDO> confVideoFaceTimeDOS = null;

        // 管理员
        if (ADMIN_VIDEO_FACE_BANK_ID.equals(videoFaceBankId)) {

            confVideoFaceTimeDOS = confVideoFaceTimeDOMapper.listAll();
        } else {

            confVideoFaceTimeDOS = confVideoFaceTimeDOMapper.listByBankId(videoFaceBankId);
        }

        if (!CollectionUtils.isEmpty(confVideoFaceTimeDOS)) {

            Map<Long, ConfVideoFaceTimeVO> bankId_Obj_map = Maps.newHashMap();
            Map<String, Object> kvMap = Maps.newHashMap();

            // DOList -> VO
            confVideoFaceTimeDOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Long bankId = e.getBankId();

                        if (bankId_Obj_map.containsKey(bankId)) {

                            doBBB(e, kvMap);

                        } else {

                            doAAA(e, kvMap, bankId_Obj_map);
                        }

                    });

            return Lists.newArrayList(bankId_Obj_map.values());
        }

        return Collections.EMPTY_LIST;
    }

    private void doBBB(ConfVideoFaceTimeDO confVideoFaceTimeDO, Map<String, Object> kvMap) {

        Long bankId = confVideoFaceTimeDO.getBankId();
        BigDecimal startLoanAmount = confVideoFaceTimeDO.getStartLoanAmount();
        BigDecimal endLoanAmount = confVideoFaceTimeDO.getEndLoanAmount();
        Byte type = confVideoFaceTimeDO.getType();
        Integer maxWaitTime = confVideoFaceTimeDO.getMaxWaitTime();
        String startTime = confVideoFaceTimeDO.getStartTime();
        String endTime = confVideoFaceTimeDO.getEndTime();

        String key_bankId = "" + bankId;
        String key_detail = key_bankId + SEPARATOR + startLoanAmount + SEPARATOR + endLoanAmount;
        String key_type = key_detail + SEPARATOR + type + SEPARATOR + maxWaitTime;
        String key_time = key_type + SEPARATOR + startTime + SEPARATOR + endTime;


        if (!kvMap.containsKey(key_detail)) {

            ConfVideoFaceTimeVO.Detail detailVO = new ConfVideoFaceTimeVO.Detail();
            detailVO.setStartLoanAmount(startLoanAmount);
            detailVO.setEndLoanAmount(endLoanAmount);
            kvMap.put(key_detail, detailVO);

            ConfVideoFaceTimeVO confVideoFaceTimeVO = (ConfVideoFaceTimeVO) kvMap.get(key_bankId);
            confVideoFaceTimeVO.getDetailList().add(detailVO);
        }

        if (!kvMap.containsKey(key_type)) {

            ConfVideoFaceTimeVO.Type typeVO = new ConfVideoFaceTimeVO.Type();
            typeVO.setType(type);
            typeVO.setMaxWaitTime(maxWaitTime);
            kvMap.put(key_type, typeVO);

            ConfVideoFaceTimeVO.Detail detailVO = (ConfVideoFaceTimeVO.Detail) kvMap.get(key_detail);
            detailVO.getTypeList().add(typeVO);
        }

        if (!kvMap.containsKey(key_time)) {

            ConfVideoFaceTimeVO.Time timeVO = new ConfVideoFaceTimeVO.Time();
            timeVO.setStartTime(startTime);
            timeVO.setEndTime(endTime);
            kvMap.put(key_time, timeVO);

            ConfVideoFaceTimeVO.Type typeVO = (ConfVideoFaceTimeVO.Type) kvMap.get(key_type);
            typeVO.getTimeList().add(timeVO);
        }

    }

    private void doAAA(ConfVideoFaceTimeDO e, Map<String, Object> kvMap, Map<Long, ConfVideoFaceTimeVO> bankId_Obj_map) {

        Long bankId = e.getBankId();
        BigDecimal startLoanAmount = e.getStartLoanAmount();
        BigDecimal endLoanAmount = e.getEndLoanAmount();
        Byte type = e.getType();
        Integer maxWaitTime = e.getMaxWaitTime();
        String startTime = e.getStartTime();
        String endTime = e.getEndTime();

        String key_bankId = "" + bankId;
        String key_detail = key_bankId + SEPARATOR + startLoanAmount + SEPARATOR + endLoanAmount;
        String key_type = key_detail + SEPARATOR + type + SEPARATOR + maxWaitTime;
        String key_time = key_type + SEPARATOR + startTime + SEPARATOR + endTime;


        ConfVideoFaceTimeVO confVideoFaceTimeVO = new ConfVideoFaceTimeVO();

        // bankId
        confVideoFaceTimeVO.setBankId(bankId);
        kvMap.put(key_bankId, confVideoFaceTimeVO);


        // detail
        ConfVideoFaceTimeVO.Detail detailVO = new ConfVideoFaceTimeVO.Detail();
        detailVO.setStartLoanAmount(startLoanAmount);
        detailVO.setEndLoanAmount(endLoanAmount);
        confVideoFaceTimeVO.setDetailList(Lists.newArrayList(detailVO));
        kvMap.put(key_detail, detailVO);


        // type
        ConfVideoFaceTimeVO.Type typeVO = new ConfVideoFaceTimeVO.Type();
        typeVO.setType(type);
        typeVO.setMaxWaitTime(maxWaitTime);
        detailVO.setTypeList(Lists.newArrayList(typeVO));
        kvMap.put(key_type, typeVO);


        // time
        ConfVideoFaceTimeVO.Time timeVO = new ConfVideoFaceTimeVO.Time();
        timeVO.setStartTime(startTime);
        timeVO.setEndTime(endTime);
        typeVO.setTimeList(Lists.newArrayList(timeVO));
        kvMap.put(key_time, timeVO);

        bankId_Obj_map.put(bankId, confVideoFaceTimeVO);
    }

    /**
     * del All
     *
     * @param videoFaceBankId
     */
    private void deleteAllConfByBankId(Long videoFaceBankId) {

        // 管理员
        if (ADMIN_VIDEO_FACE_BANK_ID.equals(videoFaceBankId)) {

            confVideoFaceTimeDOMapper.deleteAll();
        } else {

            confVideoFaceTimeDOMapper.deleteAllByBankId(videoFaceBankId);
        }
    }

    /**
     * 保存
     *
     * @param videoFaceBankId
     * @param confVideoFaceTimeVOS
     */
    private void doSave(Long videoFaceBankId, List<ConfVideoFaceTimeVO> confVideoFaceTimeVOS) {

        if (!CollectionUtils.isEmpty(confVideoFaceTimeVOS)) {

            // insert
            confVideoFaceTimeVOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Long bankId = e.getBankId();

                        Preconditions.checkNotNull(bankId, "bankId不能为空");
                        if (!ADMIN_VIDEO_FACE_BANK_ID.equals(videoFaceBankId)) {
                            Preconditions.checkArgument(videoFaceBankId.equals(bankId),
                                    "您无权操作当前银行：" + bankCache.getNameById(bankId));
                        }

                        List<ConfVideoFaceTimeVO.Detail> detailList = e.getDetailList();
                        if (!CollectionUtils.isEmpty(detailList)) {

                            doInsert_detail(detailList, bankId);
                        }
                    });
        }

    }

    /**
     * -
     *
     * @param detailList
     * @param bankId
     */
    private void doInsert_detail(List<ConfVideoFaceTimeVO.Detail> detailList, Long bankId) {

        detailList.stream()
                .filter(Objects::nonNull)
                .forEach(d -> {

                    BigDecimal startLoanAmount = d.getStartLoanAmount();
                    BigDecimal endLoanAmount = d.getEndLoanAmount();

                    Preconditions.checkNotNull(startLoanAmount, "startLoanAmount不能为空");
                    Preconditions.checkNotNull(endLoanAmount, "endLoanAmount不能为空");
                    // -1：+∞
                    if (!(endLoanAmount.doubleValue() == -1)) {
                        Preconditions.checkArgument(startLoanAmount.doubleValue() < endLoanAmount.doubleValue(),
                                "startLoanAmount必须小于endLoanAmount，startLoanAmount : " + startLoanAmount + " ，endLoanAmount : " + endLoanAmount);
                    }

                    List<ConfVideoFaceTimeVO.Type> typeList = d.getTypeList();
                    if (!CollectionUtils.isEmpty(typeList)) {

                        doInsert_type(typeList, bankId, startLoanAmount, endLoanAmount);
                    }
                });
    }

    /**
     * -
     *
     * @param typeList
     * @param bankId
     * @param startLoanAmount
     * @param endLoanAmount
     */
    private void doInsert_type(List<ConfVideoFaceTimeVO.Type> typeList, Long bankId, BigDecimal startLoanAmount, BigDecimal endLoanAmount) {

        typeList.stream()
                .filter(Objects::nonNull)
                .forEach(t -> {

                    Preconditions.checkNotNull(t.getType(), "type不能为空");

                    List<ConfVideoFaceTimeVO.Time> timeList = t.getTimeList();
                    if (!CollectionUtils.isEmpty(timeList)) {

                        doInsert_time(timeList, bankId, startLoanAmount, endLoanAmount, t.getType());
                    }
                });
    }

    /**
     * -
     *
     * @param timeList
     * @param bankId
     * @param startLoanAmount
     * @param endLoanAmount
     * @param type
     */
    private void doInsert_time(List<ConfVideoFaceTimeVO.Time> timeList, Long bankId, BigDecimal startLoanAmount,
                               BigDecimal endLoanAmount, Byte type) {

        timeList.stream()
                .filter(Objects::nonNull)
                .forEach(time -> {

                    String startTime = time.getStartTime();
                    String endTime = time.getEndTime();
                    Preconditions.checkArgument(StringUtils.isNotBlank(startTime), "startTime不能为空");
                    Preconditions.checkArgument(StringUtils.isNotBlank(endTime), "endTime不能为空");
                    Preconditions.checkArgument(startTime.compareTo(endTime) < 0,
                            "startTime必须小于endTime，startTime : " + startTime + " ，endTime : " + endTime);

                    ConfVideoFaceTimeDO confVideoFaceTimeDO = new ConfVideoFaceTimeDO();
                    // 银行
                    confVideoFaceTimeDO.setBankId(bankId);
                    // 金额区间
                    confVideoFaceTimeDO.setStartLoanAmount(startLoanAmount);
                    confVideoFaceTimeDO.setEndLoanAmount(endLoanAmount);
                    // 时间/日期类型
                    confVideoFaceTimeDO.setType(type);
                    // 时间/日期
                    confVideoFaceTimeDO.setStartTime(startTime);
                    confVideoFaceTimeDO.setEndTime(endTime);

                    int count = confVideoFaceTimeDOMapper.insertSelective(confVideoFaceTimeDO);
                    Preconditions.checkArgument(count > 0, "插入失败");
                });
    }
}
