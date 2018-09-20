package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.domain.vo.ConfVideoFaceTimeVO;
import com.yunche.loan.mapper.ConfVideoFaceTimeDOMapper;
import com.yunche.loan.service.ConfVideoFaceTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@Service
public class ConfVideoFaceTimeServiceImpl implements ConfVideoFaceTimeService {

    private static final String SEPARATOR = "-";

    @Autowired
    private ConfVideoFaceTimeDOMapper confVideoFaceTimeDOMapper;


    @Override
    @Transactional
    public void save(List<ConfVideoFaceTimeVO> confVideoFaceTimeVOS) {

        // del ALL
        confVideoFaceTimeDOMapper.deleteAll();

        // insert
        if (!CollectionUtils.isEmpty(confVideoFaceTimeVOS)) {

            doSave(confVideoFaceTimeVOS);
        }
    }

    @Override
    public List<ConfVideoFaceTimeVO> listAll() {

        List<ConfVideoFaceTimeDO> confVideoFaceTimeDOS = confVideoFaceTimeDOMapper.listAll();

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

            List<ConfVideoFaceTimeVO> confVideoFaceTimeVOList = Lists.newArrayList(bankId_Obj_map.values());
            return confVideoFaceTimeVOList;
        }

        return Collections.EMPTY_LIST;
    }

    private void doBBB(ConfVideoFaceTimeDO e, Map<String, Object> kvMap) {

        Long bankId = e.getBankId();
        BigDecimal startLoanAmount = e.getStartLoanAmount();
        BigDecimal endLoanAmount = e.getEndLoanAmount();
        Byte type = e.getType();
        String startTime = e.getStartTime();
        String endTime = e.getEndTime();

        String key_bankId = "" + bankId;
        String key_detail = key_bankId + SEPARATOR + startLoanAmount + SEPARATOR + endLoanAmount;
        String key_type = key_detail + SEPARATOR + type;
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
        String startTime = e.getStartTime();
        String endTime = e.getEndTime();

        String key_bankId = "" + bankId;
        String key_detail = key_bankId + SEPARATOR + startLoanAmount + SEPARATOR + endLoanAmount;
        String key_type = key_detail + SEPARATOR + type;
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
     * 保存
     *
     * @param confVideoFaceTimeVOS
     */
    private void doSave(List<ConfVideoFaceTimeVO> confVideoFaceTimeVOS) {

        // insert
        confVideoFaceTimeVOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Preconditions.checkNotNull(e.getBankId(), "bankId不能为空");

                    List<ConfVideoFaceTimeVO.Detail> detailList = e.getDetailList();
                    if (!CollectionUtils.isEmpty(detailList)) {

                        doInsert_detail(detailList, e.getBankId());
                    }
                });
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
                    Preconditions.checkArgument(startLoanAmount.doubleValue() <= endLoanAmount.doubleValue(),
                            "startLoanAmount不能大于endLoanAmount");

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
                    Preconditions.checkNotNull(startTime, "startTime不能为空");
                    Preconditions.checkNotNull(endTime, "endTime不能为空");
                    Preconditions.checkArgument(startTime.compareTo(endTime) < 0, "startTime不能大于endTime");

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
