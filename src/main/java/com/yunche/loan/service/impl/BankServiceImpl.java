package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankParam;
import com.yunche.loan.domain.param.BankSaveParam;
import com.yunche.loan.domain.param.ConfLoanApplyParam;
import com.yunche.loan.domain.query.BankQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_AREA;
import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;
import static com.yunche.loan.config.constant.BankConst.QUESTION_BANK;
import static com.yunche.loan.config.constant.BankConst.QUESTION_MACHINE;
import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
@Service
public class BankServiceImpl implements BankService {

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private BankDOMapper bankDOMapper;

    @Autowired
    private BankRelaQuestionDOMapper bankRelaQuestionDOMapper;

    @Autowired
    private BankCarLicenseLocationDOMapper bankCarLicenseLocationDOMapper;

    @Autowired
    private AreaCache areaCache;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private ConfLoanApplyDOMapper confLoanApplyDOMapper;


    @Override
    @Transactional
    public void save(BankSaveParam param) {
        Preconditions.checkNotNull(param, "银行不能为空");
        Preconditions.checkNotNull(param.getBank(), "银行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(param.getBank().getName()), "银行不能为空");
        Preconditions.checkNotNull(param.getBank().getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(param.getBank().getStatus()) || INVALID_STATUS.equals(param.getBank().getStatus()), "状态非法");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(param.getBankCarLicenseLocationList()), "上牌地列表为空");

        Long bankId = param.getBank().getId();
        if (bankId != null) {
            Preconditions.checkNotNull(param.getBank().getId(), "银行不能为空");
            bindBankCarLicenseLocation(param.getBank().getId(), param.getBankCarLicenseLocationList());
            updateBank(param.getBank());
        } else {
            // 插入银行
            bankId = insertAndGetBank(param.getBank());
            bindBankCarLicenseLocation(bankId, param.getBankCarLicenseLocationList());
        }
        // 刷新缓存
        bankCache.refresh();
    }

    @Override
    public BankReturnVO detail(Long bankId) {
        Preconditions.checkNotNull(bankId, "银行ID不能为空");

        BankReturnVO bankReturnVO = new BankReturnVO();

        // 市/区ID
        List<Long> city_or_area_ids = Lists.newArrayList();
        List<BankCarLicenseLocationDO> licenseLocationDOS = bankCarLicenseLocationDOMapper.listByBankId(bankId);
        for (BankCarLicenseLocationDO bankCarLicenseLocationDO : licenseLocationDOS) {
            city_or_area_ids.add(bankCarLicenseLocationDO.getAreaId());
        }

        // 银行
        BankDO bankDO = bankDOMapper.selectByPrimaryKey(bankId);
        bankReturnVO.setInfo(bankDO);

        List<AreaRVO> result = Lists.newArrayList();

        for (Long city_or_area_id : city_or_area_ids) {

            BaseAreaDO city_or_area_DO = baseAreaDOMapper.selectByPrimaryKey(city_or_area_id, VALID_STATUS);

            if (null != city_or_area_DO) {

                Byte level = city_or_area_DO.getLevel();
                // 市
                if (LEVEL_CITY.equals(level)) {

                    AreaRVO areaRVO = new AreaRVO();

                    Long pId = city_or_area_DO.getParentAreaId();
                    areaRVO.setPId(pId);
                    areaRVO.setPName(areaCache.getAreaName(String.valueOf(pId)));

                    areaRVO.setCId(city_or_area_DO.getAreaId());
                    areaRVO.setCName(city_or_area_DO.getAreaName());

                    result.add(areaRVO);
                }
                // 区
                else if (LEVEL_AREA.equals(level)) {

                    AreaRVO areaRVO = new AreaRVO();

                    Long cId = city_or_area_DO.getParentAreaId();
                    BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(cId, VALID_STATUS);

                    areaRVO.setPId(cityDO.getParentAreaId());
                    areaRVO.setPName(areaCache.getAreaName(String.valueOf(cityDO.getParentAreaId())));

                    areaRVO.setCId(cId);
                    areaRVO.setCName(cityDO.getAreaName());

                    areaRVO.setAId(city_or_area_DO.getAreaId());
                    areaRVO.setAName(city_or_area_DO.getAreaName());

                    result.add(areaRVO);
                }
            }
        }
        bankReturnVO.setList(result);
        return bankReturnVO;
    }

    @Override
    public void saveConfLoanApply(ConfLoanApplyParam param) {
        ConfLoanApplyDO newCar = param.getNewCar();
        ConfLoanApplyDOKey confLoanApplyDOKey = new ConfLoanApplyDOKey();
        confLoanApplyDOKey.setBank(newCar.getBank());
        confLoanApplyDOKey.setCar_type(Integer.valueOf(newCar.getCar_type()));
        ConfLoanApplyDO confLoanApplyDO = confLoanApplyDOMapper.selectByPrimaryKey(confLoanApplyDOKey);
        ConfLoanApplyDO confLoanApplyDO1 = new ConfLoanApplyDO();
        BeanUtils.copyProperties(newCar,confLoanApplyDO1);
        if(confLoanApplyDO != null){
            confLoanApplyDOMapper.updateByPrimaryKeySelective(confLoanApplyDO1);
        }else{
            confLoanApplyDOMapper.insertSelective(confLoanApplyDO1);
        }
        ConfLoanApplyDO oldCar = param.getOldCar();
        ConfLoanApplyDOKey confLoanApplyDOKey1 = new ConfLoanApplyDOKey();
        confLoanApplyDOKey1.setBank(oldCar.getBank());
        confLoanApplyDOKey1.setCar_type(Integer.valueOf(oldCar.getCar_type()));
        ConfLoanApplyDO confLoanApplyDO2 = confLoanApplyDOMapper.selectByPrimaryKey(confLoanApplyDOKey1);
        ConfLoanApplyDO confLoanApplyDO3 = new ConfLoanApplyDO();
        BeanUtils.copyProperties(oldCar,confLoanApplyDO3);
        if(confLoanApplyDO2 != null){
            confLoanApplyDOMapper.updateByPrimaryKeySelective(confLoanApplyDO3);
        }else{
            confLoanApplyDOMapper.insertSelective(confLoanApplyDO3);
        }

    }

    @Override
    public List<ConfLoanApplyDO> getConfLoanApply(String bank) {
        List<ConfLoanApplyDO> list = new ArrayList<>();
        list = confLoanApplyDOMapper.selectInfoByBank(bank);
        return list;
    }

    @Override
    public ResultBean<List<String>> list() {
        List<String> allBankName = bankCache.getAllBankName();
        return ResultBean.ofSuccess(allBankName);
    }

    @Override
    public ResultBean<List<BaseVO>> listAll() {

        List<BaseVO> baseVOS = Lists.newArrayList();

        Map<String, String> idNameMap = bankCache.getIdNameMap();
        if (!CollectionUtils.isEmpty(idNameMap)) {

            List<BaseVO> baseVOList = Lists.newArrayList();
            idNameMap.forEach((k, v) -> {

                BaseVO baseVO = new BaseVO();
                baseVO.setId(Long.valueOf(k));
                baseVO.setName(v);

                baseVOList.add(baseVO);
            });

            // sort
            baseVOS = baseVOList.stream().sorted(Comparator.comparing(BaseVO::getId)).collect(Collectors.toList());
        }

        return ResultBean.ofSuccess(baseVOS);
    }

    @Override
    public List<BankDO> lists() {
        return bankDOMapper.listAll(null);
    }

    @Override
    public ResultBean<List<BankDO>> query(BankQuery query) {

        int totalNum = bankDOMapper.count(query);
        if (totalNum > 0) {

            List<BankDO> bankList = bankDOMapper.query(query);
            if (!CollectionUtils.isEmpty(bankList)) {

                return ResultBean.ofSuccess(bankList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    @Transactional
    public ResultBean<Long> create(BankParam bankParam) {
        Preconditions.checkNotNull(bankParam, "银行不能为空");
        Preconditions.checkNotNull(bankParam.getBank(), "银行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(bankParam.getBank().getName()), "银行不能为空");
        Preconditions.checkNotNull(bankParam.getBank().getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(bankParam.getBank().getStatus()) || INVALID_STATUS.equals(bankParam.getBank().getStatus()), "状态非法");
        // 插入银行
        Long bankId = insertAndGetBank(bankParam.getBank());
        // 绑定问卷
        bindQuestion(bankId, bankParam.getBankQuestionList());
        bindQuestion(bankId, bankParam.getMachineQuestionList());
        // 刷新缓存
        bankCache.refresh();
        return ResultBean.ofSuccess(bankId, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(BankParam bankParam) {
        Preconditions.checkNotNull(bankParam, "ID不能为空");
        Preconditions.checkNotNull(bankParam.getBank().getId(), "银行ID不能为空");
        // 更新银行
        updateBank(bankParam.getBank());
        // 更新问卷
        Long bankId = bankParam.getBank().getId();
        updateQuestion(bankId, bankParam.getBankQuestionList());
        updateQuestion(bankId, bankParam.getMachineQuestionList());
        // 刷新缓存
        bankCache.refresh();
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "ID不能为空");

        int count = bankDOMapper.deleteByPrimaryKey(id);
        bankCarLicenseLocationDOMapper.deleteByBankId(id);
        // 刷新缓存
        bankCache.refresh();

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<BankVO> getById(Long id) {
        Preconditions.checkNotNull(id, "银行ID不能为空");

        BankVO bankVO = new BankVO();

        // 银行
        BankDO bankDO = bankDOMapper.selectByPrimaryKey(id);
        bankVO.setBank(bankDO);

        // 问卷
        List<BankRelaQuestionDO> bankQuestionList = bankRelaQuestionDOMapper.listByBankIdAndType(id, QUESTION_BANK);
        List<BankRelaQuestionDO> machineQuestionList = bankRelaQuestionDOMapper.listByBankIdAndType(id, QUESTION_MACHINE);
        bankVO.setBankQuestionList(bankQuestionList);
        bankVO.setMachineQuestionList(machineQuestionList);
        return ResultBean.ofSuccess(bankVO);
    }

    private List<CascadeAreaVO> areaList(Long bankId) {
        Preconditions.checkArgument(bankId != null, "银行名称不能为空");
        List<BankCarLicenseLocationDO> list = bankCarLicenseLocationDOMapper.listByBankId(bankId);
        List<Long> bank_areaids = Lists.newArrayList();
        for (BankCarLicenseLocationDO v : list) {
            bank_areaids.add(v.getAreaId());
        }

        List<CascadeAreaVO> ableAreaList = Lists.newArrayList();

        //根据管辖区域id获取管辖区域信息
        List<BaseAreaDO> baseBindArea = bank_areaids.parallelStream().map(e -> {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e, VALID_STATUS);
            return baseAreaDO;
        }).distinct().collect(Collectors.toList());

        List<CascadeAreaVO.City> baseAreaDOS = Lists.newArrayList();

        HashMap<Long, Set<Long>> areaMap = Maps.newHashMap();

        baseBindArea.stream().forEach(hasArea -> {
            switch (hasArea.getLevel()) {

                case 0:
                    ableAreaList.addAll(areaCache.get());
                    break;
                case 1:
                    List<Long> citys = baseAreaDOMapper.selectCityIdByProvenceId(hasArea.getAreaId());
                    if (areaMap.keySet().contains(hasArea.getAreaId())) {
                        Set<Long> aaa = areaMap.get(hasArea.getAreaId());
                        aaa.addAll(citys);
                        areaMap.put(hasArea.getAreaId(), aaa);
                    } else {
                        Set<Long> tmp = Sets.newHashSet();
                        tmp.addAll(citys);
                        areaMap.put(hasArea.getAreaId(), tmp);
                    }
                    break;
                case 2:
                    if (areaMap.keySet().contains(hasArea.getParentAreaId())) {
                        Set<Long> aaa = areaMap.get(hasArea.getParentAreaId());
                        aaa.add(hasArea.getAreaId());
                    } else {
                        Set<Long> tmp = Sets.newHashSet();
                        tmp.add(hasArea.getAreaId());
                        areaMap.put(hasArea.getParentAreaId(), tmp);
                    }
                    break;

            }
        });

        // 根据银行管辖区域获取管辖区域的省市
        List<CascadeAreaVO> cascadeAreaVOS = fillInfo(ableAreaList, areaMap);

        return cascadeAreaVOS;
    }

    @Override
    public List<CascadeAreaVO> areaListByBankName(String bankName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(bankName), "银行名称不能为空");

        Long bankId = bankCache.getIdByName(bankName);

        List<BankCarLicenseLocationDO> bankCarLicenseLocationDOS = bankCarLicenseLocationDOMapper.listByBankId(bankId);
        Set<Long> bank_areaids = null;
        if (!CollectionUtils.isEmpty(bankCarLicenseLocationDOS)) {

            bank_areaids = bankCarLicenseLocationDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .map(BankCarLicenseLocationDO::getAreaId)
                    .collect(Collectors.toSet());
        }

        // 根据管辖区域id获取管辖区域信息
        List<BaseAreaDO> baseBindArea = null;
        if (!CollectionUtils.isEmpty(bank_areaids)) {
            baseBindArea = bank_areaids.parallelStream().map(e -> {
                BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e, VALID_STATUS);
                return baseAreaDO;
            }).distinct().collect(Collectors.toList());
        }

        List<CascadeAreaVO> ableAreaList = Lists.newArrayList();
        Map<Long, Set<Long>> areaMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(baseBindArea)) {

            baseBindArea.stream()
                    .forEach(hasArea -> {

                        switch (hasArea.getLevel()) {

                            case 0:
                                ableAreaList.addAll(areaCache.get());
                                break;

                            case 1:
                                Long provinceId = hasArea.getAreaId();
                                List<Long> citys = baseAreaDOMapper.selectCityIdByProvenceId(provinceId);

                                if (areaMap.containsKey(provinceId)) {
                                    Set<Long> aaa = areaMap.get(provinceId);
                                    aaa.addAll(citys);
                                    areaMap.put(provinceId, aaa);
                                } else {
                                    Set<Long> tmp = Sets.newHashSet(citys);
                                    areaMap.put(provinceId, tmp);
                                }
                                break;

                            case 2:
                                Long cityId_ = hasArea.getAreaId();
                                Long provinceId_ = hasArea.getParentAreaId();
                                if (areaMap.containsKey(provinceId_)) {
                                    Set<Long> aaa = areaMap.get(provinceId_);
                                    aaa.add(cityId_);
                                } else {
                                    Set<Long> tmp = Sets.newHashSet(cityId_);
                                    areaMap.put(provinceId_, tmp);
                                }
                                break;

                            case 3:
                                Long areaId__ = hasArea.getAreaId();
                                Long cityId__ = hasArea.getParentAreaId();
                                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(cityId__, null);
                                Long provinceId__ = cityDO.getParentAreaId();

                                if (areaMap.containsKey(provinceId__)) {
                                    Set<Long> aaa = areaMap.get(provinceId__);
                                    aaa.add(areaId__);
                                } else {
                                    Set<Long> tmp = Sets.newHashSet(areaId__);
                                    areaMap.put(provinceId__, tmp);
                                }
                                break;
                        }
                    });
        }

        //根据银行管辖区域获取管辖区域的省市
        List<CascadeAreaVO> cascadeAreaVOS = fillInfo(ableAreaList, areaMap);

        return cascadeAreaVOS;
    }


    /**
     * 填充信息
     *
     * @param areaMap
     */
    private List<CascadeAreaVO> fillInfo(List<CascadeAreaVO> ableAreaList, Map<Long, Set<Long>> areaMap) {

        if (CollectionUtils.isEmpty(areaMap)) {
            return ableAreaList;
        }

        areaMap.keySet().parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    CascadeAreaVO cascadeAreaVO = new CascadeAreaVO();

                    // 省
                    BaseAreaDO prov = baseAreaDOMapper.selectByPrimaryKey(e, VALID_STATUS);
                    cascadeAreaVO.setId(prov.getAreaId());
                    cascadeAreaVO.setName(prov.getAreaName());

                    // 市/区
                    Set<Long> city_or_area_ids = areaMap.get(e);
                    if (!CollectionUtils.isEmpty(city_or_area_ids)) {

                        List<CascadeAreaVO.City> city_or_area_DOS = city_or_area_ids.stream()
                                .filter(Objects::nonNull)
                                .map(city_or_area_id -> {

                                    BaseAreaDO city_or_area_DO = baseAreaDOMapper.selectByPrimaryKey(city_or_area_id, null);

                                    CascadeAreaVO.City city = new CascadeAreaVO.City();
                                    city.setId(city_or_area_DO.getAreaId());
                                    city.setName(city_or_area_DO.getAreaName());
                                    city.setLevel(city_or_area_DO.getLevel());
                                    return city;
                                })
                                .collect(Collectors.toList());

                        // 市/区
                        cascadeAreaVO.setCityList(city_or_area_DOS);
                        // 省
                        ableAreaList.add(cascadeAreaVO);
                    }

                });

        return ableAreaList;
    }


    /**
     * 创建银行
     *
     * @param bankDO
     * @return
     */
    private Long insertAndGetBank(BankDO bankDO) {
        // date
        bankDO.setGmtCreate(new Date());
        bankDO.setGmtModify(new Date());

        int count = bankDOMapper.insertSelective(bankDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return bankDO.getId();
    }

    /**
     * 绑定问卷
     *
     * @param bankId
     * @param questionList
     */
    private void bindQuestion(Long bankId, List<BankRelaQuestionDO> questionList) {

        if (!CollectionUtils.isEmpty(questionList)) {

            questionList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        e.setBankId(bankId);
                        e.setGmtCreate(new Date());
                        e.setGmtModify(new Date());

                        int count = bankRelaQuestionDOMapper.insert(e);
                        Preconditions.checkArgument(count > 0, "插入失败");
                    });
        }
    }

    /**
     * 更新银行
     *
     * @param bankDO
     */
    private void updateBank(BankDO bankDO) {
        bankDO.setGmtModify(new Date());

        int count = bankDOMapper.updateByPrimaryKeySelective(bankDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
    }

    /**
     * 更新问卷
     *
     * @param bankId
     * @param questionList
     */
    private void updateQuestion(Long bankId, List<BankRelaQuestionDO> questionList) {
        // deleteAll-Old
        bankRelaQuestionDOMapper.deleteAllByBankId(bankId);

        // bindNow
        bindQuestion(bankId, questionList);
    }

    private void bindBankCarLicenseLocation(Long bankId, Set<Long> bankCarLicenseLocationList) {
        // deleteAll-Old
        bankCarLicenseLocationDOMapper.deleteByBankId(bankId);

        // bindNow
        for (Long areaId : bankCarLicenseLocationList) {
            BankCarLicenseLocationDO save = new BankCarLicenseLocationDO();
            save.setBankId(bankId);
            save.setAreaId(areaId);
            bankCarLicenseLocationDOMapper.insertSelective(save);
        }
    }
}