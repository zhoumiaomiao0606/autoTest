package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankCarLicenseLocationDO;
import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import com.yunche.loan.domain.param.BankParam;
import com.yunche.loan.domain.query.BankQuery;
import com.yunche.loan.domain.vo.BankVO;
import com.yunche.loan.mapper.BankCarLicenseLocationDOMapper;
import com.yunche.loan.mapper.BankDOMapper;
import com.yunche.loan.mapper.BankRelaQuestionDOMapper;
import com.yunche.loan.service.BankService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private BankDOMapper bankDOMapper;

    @Autowired
    private BankRelaQuestionDOMapper bankRelaQuestionDOMapper;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private BankCarLicenseLocationDOMapper bankCarLicenseLocationDOMapper;


    @Override
    public ResultBean<List<String>> listAll() {
        List<String> allBankName = bankCache.getAllBankName();
        return ResultBean.ofSuccess(allBankName);
    }

    @Override
    public List<BankDO> lists() {
        return bankDOMapper.listAll(new Byte("0"));
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
        Preconditions.checkArgument(CollectionUtil.isNotEmpty(bankParam.getBankCarLicenseLocationList()), "上牌地列表为空");
        // 插入银行
        Long bankId = insertAndGetBank(bankParam.getBank());

        // 绑定问卷
        bindQuestion(bankId, bankParam.getBankQuestionList());
        bindQuestion(bankId, bankParam.getMachineQuestionList());
        bindBankCarLicenseLocation(bankId,bankParam.getBankCarLicenseLocationList());

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
        bindBankCarLicenseLocation(bankId,bankParam.getBankCarLicenseLocationList());


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
        List<BankCarLicenseLocationDO> bankCarLicenseLocationList = bankCarLicenseLocationDOMapper.listByBankId(id);
        List<Long> longs = Lists.newArrayList();
        for(BankCarLicenseLocationDO v:bankCarLicenseLocationList){
            longs.add(v.getAreaId());
        }
        bankVO.setBankQuestionList(bankQuestionList);
        bankVO.setMachineQuestionList(machineQuestionList);
        bankVO.setBankCarLicenseLocationList(longs);
        return ResultBean.ofSuccess(bankVO);
    }

    @Override
    public ResultBean<List<Long>> areaListByBankName(String bankName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(bankName),"银行名称不能为空");
        Long bankId = bankDOMapper.selectIdByName(bankName);
        List<BankCarLicenseLocationDO> list = bankCarLicenseLocationDOMapper.listByBankId(bankId);
        List<Long> longs = Lists.newArrayList();
        for(BankCarLicenseLocationDO v:list){
            longs.add(v.getAreaId());
        }
        return ResultBean.ofSuccess(longs);
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

    private void bindBankCarLicenseLocation(Long bankId, List<Long> bankCarLicenseLocationList){
        bankCarLicenseLocationDOMapper.deleteByBankId(bankId);
        for(Long areaId:bankCarLicenseLocationList){
            BankCarLicenseLocationDO save = new BankCarLicenseLocationDO();
            save.setBankId(bankId);
            save.setAreaId(areaId);
            bankCarLicenseLocationDOMapper.insertSelective(save);
        }
    }
}