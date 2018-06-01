package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.MD5Utils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.PartnerQuery;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.PartnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_WB;
import static com.yunche.loan.service.impl.CarServiceImpl.NEW_LINE;
import static com.yunche.loan.service.impl.EmployeeServiceImpl.initPassword;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private BizModelDOMapper bizModelDOMapper;

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private BizModelRelaAreaPartnersDOMapper bizModelRelaAreaPartnersDOMapper;

    @Autowired
    private BizModelRelaFinancialProdDOMapper bizModelRelaFinancialProdDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private PartnerBankAccountDOMapper partnerBankAccountDOMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PartnerRelaAreaDOMapper partnerRelaAreaDOMapper;

    @Autowired
    private EmployeeService employeeService;


    @Override
    @Transactional
    public ResultBean<Long> create(PartnerParam partnerParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getName()), "团队名称不能为空");
        Preconditions.checkNotNull(partnerParam.getDepartmentId(), "对应负责部门不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderName()), "团队负责人不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderMobile()), "负责人手机不能为空");
        Preconditions.checkNotNull(partnerParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(partnerParam.getStatus()) || INVALID_STATUS.equals(partnerParam.getStatus()),
                "状态非法");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(partnerParam.getBankAccountList()), "财务合作信息不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderEmail()), "邮箱不能为空");

        // 给合伙人创建一个账号
        Long leaderAccountId = createPartnerLeaderAccount(partnerParam);

        // 设置为leader
        partnerParam.setLeaderId(leaderAccountId);

        // 创建实体，并返回ID
        Long partnerId = insertAndGetId(partnerParam);

        // 绑定leader账号
        bindEmployee(partnerId, Lists.newArrayList(leaderAccountId));

        // 绑定财务合作信息
        bindPartnerBankAccount(partnerParam.getBankAccountList(), partnerId);

        // 绑定业务产品列表
        bindBizModel(partnerId, partnerParam.getAreaId(), partnerParam.getBizModelIdList());

        return ResultBean.ofSuccess(partnerId, "创建成功");
    }

    /**
     * 给合伙人创建一个账号，并设置为leader
     *
     * @param partnerParam
     * @return 返回账号ID
     */
    private Long createPartnerLeaderAccount(PartnerParam partnerParam) {
        EmployeeDO employeeDO = new EmployeeDO();

        employeeDO.setName(partnerParam.getLeaderName());
        employeeDO.setIdCard(partnerParam.getLeaderIdCard());
        employeeDO.setEmail(partnerParam.getLeaderEmail());
        employeeDO.setMobile(partnerParam.getLeaderMobile());
        employeeDO.setDingDing(partnerParam.getLeaderDingDing());

        // 初始密码
        String md5Password = MD5Utils.md5(initPassword);
        employeeDO.setPassword(md5Password);

        employeeDO.setType(TYPE_WB);
        employeeDO.setStatus(VALID_STATUS);
        employeeDO.setGmtCreate(new Date());
        employeeDO.setGmtModify(new Date());

        int count = employeeDOMapper.insertSelective(employeeDO);
        Preconditions.checkArgument(count > 0, "创建合伙人账号失败");

        return employeeDO.getId();
    }

    /**
     * 绑定财务合作信息
     *
     * @param partnerBankAccountDOList
     * @param partnerId
     */
    private void bindPartnerBankAccount(List<PartnerBankAccountDO> partnerBankAccountDOList, Long partnerId) {

        List<PartnerBankAccountDO> partnerBankAccountDOS = partnerBankAccountDOList.stream()
                .filter(Objects::nonNull)
                .map(e -> {

                    PartnerBankAccountDO partnerBankAccountDO = new PartnerBankAccountDO();
                    BeanUtils.copyProperties(e, partnerBankAccountDO);
                    partnerBankAccountDO.setPartnerId(partnerId);

                    return partnerBankAccountDO;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(partnerBankAccountDOS)) {
            int count = partnerBankAccountDOMapper.batchInsert(partnerBankAccountDOS);
            Preconditions.checkArgument(count > 0, "保存财务合作信息失败");
        }
    }

    /**
     * 给合伙人团队负责人(老板)  创建一个账号
     * <p>
     * 以负责人手机号创建账号
     *
     * @param mobile
     * @param name
     */
    private void createAccountIfNotExist(String mobile, String name) {

        // 是否已创建账号
        EmployeeQuery query = new EmployeeQuery();
        query.setMobile(mobile);
        List<EmployeeDO> employeeDOS = employeeDOMapper.query(query);

        // 无 -> 新建
        if (CollectionUtils.isEmpty(employeeDOS)) {

            // 随机生成密码
            String password = MD5Utils.getRandomString(10);
            // MD5加密
            String md5Password = MD5Utils.md5(password);

            EmployeeDO employeeDO = new EmployeeDO();
            employeeDO.setType(TYPE_WB);
            employeeDO.setName(name);
            employeeDO.setMobile(mobile);
            employeeDO.setPassword(md5Password);
            employeeDO.setGmtModify(new Date());
            employeeDO.setGmtModify(new Date());

            int count = employeeDOMapper.insertSelective(employeeDO);
            Preconditions.checkArgument(count > 0, "创建团队负责人账号失败");

            // sentAccount
//            sentAccountAndPassword();
        }
    }

    /**
     * 发送账号密码到邮箱
     *
     * @param email
     * @param password
     */
    private void sentAccountAndPassword(String email, String password) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("主题：注册账号密码");
        message.setText("账号：" + email + NEW_LINE + "密码：" + password);

        mailSender.send(message);
    }

    @Override
    @Transactional
    public ResultBean<Void> update(PartnerParam partnerParam) {
        Preconditions.checkNotNull(partnerParam.getId(), "id不能为空");
        Preconditions.checkNotNull(partnerParam.getLeaderId(), "团队负责人不能为空");

        // 编辑leader
        updateLeader(partnerParam.getId(), partnerParam.getLeaderId());

        PartnerDO partnerDO = new PartnerDO();
        BeanUtils.copyProperties(partnerParam, partnerDO);

        partnerParam.setGmtModify(new Date());
        int count = partnerDOMapper.updateByPrimaryKeySelective(partnerDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 先清空
        int delCount = partnerBankAccountDOMapper.deleteByPartnerId(partnerParam.getId());
        // 再绑定
        bindPartnerBankAccount(partnerParam.getBankAccountList(), partnerParam.getId());

        // 编辑绑定业务产品的限制区域
        updateRelaBizModelArea(partnerParam.getId(), partnerParam.getAreaId());

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    /**
     * 更新Z的Parent
     * <p>
     * X  ->  Z的 旧上级          -
     * Y  ->  Z的 新上级          - newLeaderId
     * Z  ->  被更新parent者      - oldLeader
     *
     * @param partnerId
     * @param newLeaderId_Y
     */
    private void updateLeader(Long partnerId, Long newLeaderId_Y) {

        // 根据合伙人ID  拿到Z
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(partnerId, null);
        Preconditions.checkNotNull(partnerDO, "合伙人不存在");

        // Z
        Long oldLeaderId_Z = partnerDO.getLeaderId();
        // leaderID无变化，则不编辑
        if (newLeaderId_Y.equals(oldLeaderId_Z)) {
            return;
        }

        EmployeeDO employeeDO_Z = new EmployeeDO();
        employeeDO_Z.setId(oldLeaderId_Z);
        employeeDO_Z.setParentId(newLeaderId_Y);
        ResultBean<Void> updateResultBean = employeeService.update(employeeDO_Z);
        Preconditions.checkArgument(updateResultBean.getSuccess(), updateResultBean.getMsg());
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = partnerDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        // 清空账户信息
        partnerBankAccountDOMapper.deleteByPartnerId(id);

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<PartnerVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(partnerDO, "id有误，数据不存在");

        PartnerVO partnerVO = new PartnerVO();
        BeanUtils.copyProperties(partnerDO, partnerVO);

        // fillMsg
        fillMsg(partnerDO, partnerVO);
        List<Long> areaIdList = partnerRelaAreaDOMapper.getAreaIdListByPartnerId(id);
        if(!CollectionUtils.isEmpty(areaIdList)){
            List<BaseAreaDO> hasApplyLicensePlateArea = baseAreaDOMapper.selectByIdList(areaIdList, VALID_STATUS);
            partnerVO.setHasApplyLicensePlateArea(hasApplyLicensePlateArea);
        }
        return ResultBean.ofSuccess(partnerVO);
    }


    @Override
    public ResultBean<List<PartnerVO>> batchGetById(List<Long> idList) {
        Preconditions.checkNotNull(idList, "id不能为空");

        List<PartnerDO> partnerDOList = partnerDOMapper.batchSelectByPrimaryKey(idList, VALID_STATUS);
        Preconditions.checkNotNull(partnerDOList, "id有误，数据不存在");

        List<PartnerVO> partnerVOList = Lists.newArrayList();
        for (PartnerDO partnerDO : partnerDOList) {

            PartnerVO partnerVO = new PartnerVO();
            BeanUtils.copyProperties(partnerDO, partnerVO);

            fillMsg(partnerDO, partnerVO);

            partnerVOList.add(partnerVO);
        }

        return ResultBean.ofSuccess(partnerVOList);
    }

    @Override
    public ResultBean<List<PartnerVO>> query(PartnerQuery query) {
        // 根据areaId填充所有子级areaId(含自身)
        getAndSetCascadeChildAreaIdList(query);

        int totalNum = partnerDOMapper.count(query);
        if (totalNum > 0) {

            List<PartnerDO> partnerDOS = partnerDOMapper.query(query);
            if (!CollectionUtils.isEmpty(partnerDOS)) {

                List<PartnerVO> partnerVOS = partnerDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            PartnerVO partnerVO = new PartnerVO();
                            BeanUtils.copyProperties(e, partnerVO);

                            // fillMsg
                            fillMsg(e, partnerVO);

                            return partnerVO;
                        })
                        .sorted(Comparator.comparing(PartnerVO::getId))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(partnerVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<BizModelVO>> listBizModel(BizModelQuery query) {
        // 根据areaId填充所有父级areaId(含自身)
        getAndSetCascadeSuperAreaIdList(query);

        // 获取所有符合条件的ID
        List<Long> bizModelIdList = bizModelRelaAreaPartnersDOMapper.getBizModelIdListByCondition(query);

        // 截取分页ID
        List<Long> pagingBizModelIdList = pagingBizModelIdList(bizModelIdList, query.getStartRow(), query.getPageSize());

        if (!CollectionUtils.isEmpty(pagingBizModelIdList)) {

            List<BizModelDO> bizModelDOS = bizModelDOMapper.getByIdList(pagingBizModelIdList);
            if (!CollectionUtils.isEmpty(bizModelDOS)) {

                List<BizModelVO> bizModelVOList = bizModelDOS.stream()
                        .filter(Objects::nonNull)
                        .map(bizModelDO -> {

                            BizModelVO bizModelVO = new BizModelVO();
                            BeanUtils.copyProperties(bizModelDO, bizModelVO);

                            return bizModelVO;
                        })
                        .sorted(Comparator.comparing(BizModelVO::getBizId))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(bizModelVOList, bizModelIdList.size(), query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, bizModelIdList.size(), query.getPageIndex(), query.getPageSize());
    }

    @Override
    @Transactional
    public ResultBean<Void> bindBizModel(Long id, String bizModelIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(bizModelIds), "业务产品ID不能为空");

        // convert
        List<Long> bizModelIdList = Arrays.asList(bizModelIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .distinct()
                .collect(Collectors.toList());

        // getAreaId
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        // bind
        bindBizModel(id, partnerDO.getAreaId(), bizModelIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> unbindBizModel(Long id, String bizModelIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(bizModelIds), "业务产品ID不能为空");

        // getAreaId
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(partnerDO, "id有误,合伙人不存!");
        Preconditions.checkNotNull(partnerDO.getAreaId(), "合伙人业务区域为空，请先设置业务区域");

        Arrays.asList(bizModelIds.split(",")).stream()
                .distinct()
                .forEach(bizModelId -> {
                    BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                    bizModelRelaAreaPartnersDO.setGroupId(id);
                    bizModelRelaAreaPartnersDO.setAreaId(partnerDO.getAreaId());
                    bizModelRelaAreaPartnersDO.setBizId(Long.valueOf(bizModelId));
                    int count = bizModelRelaAreaPartnersDOMapper.deleteByPrimaryKey(bizModelRelaAreaPartnersDO);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    @Override
    public ResultBean<List<EmployeeVO>> listEmployee(RelaQuery query) {
        Preconditions.checkNotNull(query.getId(), "合伙人ID不能为空");

        int totalNum = employeeDOMapper.countListEmployeeByPartnerId(query);
        if (totalNum > 0) {

            List<EmployeeDO> employeeDOS = employeeDOMapper.listEmployeeByPartnerId(query);
            if (!CollectionUtils.isEmpty(employeeDOS)) {

                List<EmployeeVO> employeeVOList = employeeDOS.stream()
                        .filter(Objects::nonNull)
                        .map(employeeDO -> {

                            EmployeeVO employeeVO = new EmployeeVO();
                            BeanUtils.copyProperties(employeeDO, employeeVO);

                            // 填充直接上级信息
                            fillParent(employeeDO.getParentId(), employeeVO);

                            return employeeVO;
                        })
                        .sorted(Comparator.comparing(EmployeeVO::getGmtModify))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(employeeVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    @Transactional
    public ResultBean<Void> bindEmployee(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID不能为空");

        // convert
        List<Long> employeeIdList = Arrays.asList(employeeIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .distinct()
                .collect(Collectors.toList());

        // bind
        bindEmployee(id, employeeIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> unbindEmployee(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID不能为空");

        Arrays.asList(employeeIds.split(",")).stream()
                .distinct()
                .forEach(employeeId -> {
                    PartnerRelaEmployeeDOKey partnerRelaEmployeeDOKey = new PartnerRelaEmployeeDOKey();
                    partnerRelaEmployeeDOKey.setPartnerId(id);
                    partnerRelaEmployeeDOKey.setEmployeeId(Long.valueOf(employeeId));
                    int count = partnerRelaEmployeeDOMapper.deleteByPrimaryKey(partnerRelaEmployeeDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    @Override
    public ResultBean<PartnerAccountVO> listAccount(Long employeeId) {
        Preconditions.checkNotNull(employeeId, "员工ID不能为空");

        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(employeeId);
        Preconditions.checkNotNull(partnerId, "员工无所属合伙人");

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(partnerId, null);
        Preconditions.checkNotNull(partnerDO, "合伙人不存在");

        PartnerAccountVO partnerAccountVO = new PartnerAccountVO();
        partnerAccountVO.setPartnerId(partnerDO.getId());
        partnerAccountVO.setPartnerName(partnerDO.getName());
        partnerAccountVO.setPayMonth(partnerDO.getPayMonth());

        // 账户信息
        List<PartnerBankAccountDO> partnerBankAccountDOList = partnerBankAccountDOMapper.listByPartnerId(partnerId);
        if (!CollectionUtils.isEmpty(partnerBankAccountDOList)) {
            List<PartnerAccountVO.AccountInfo> accountInfoList = partnerBankAccountDOList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        PartnerAccountVO.AccountInfo accountInfo = new PartnerAccountVO.AccountInfo();
                        BeanUtils.copyProperties(e, accountInfo);

                        return accountInfo;
                    })
                    .collect(Collectors.toList());

            partnerAccountVO.setAccountInfoList(accountInfoList);
        }

        return ResultBean.ofSuccess(partnerAccountVO);
    }

    @Override
    public ResultBean<Set<String>> listBank(Long employeeId) {
        Preconditions.checkNotNull(employeeId, "员工ID不能为空");

        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(employeeId);
        if (null == partnerId) {
            return ResultBean.ofSuccess(Collections.EMPTY_SET);
        }

        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDO.setGroupId(partnerId);
        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOList = bizModelRelaAreaPartnersDOMapper.listQuery(bizModelRelaAreaPartnersDO);

        Set<String> bankSet = Sets.newHashSet();

        if (!CollectionUtils.isEmpty(bizModelRelaAreaPartnersDOList)) {

            bizModelRelaAreaPartnersDOList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Long bizId = e.getBizId();
                        if (null != bizId) {

                            List<BizModelRelaFinancialProdDO> bizModelRelaFinancialProdDOList = bizModelRelaFinancialProdDOMapper.queryById(bizId);

                            if (!CollectionUtils.isEmpty(bizModelRelaFinancialProdDOList)) {

                                bizModelRelaFinancialProdDOList.stream()
                                        .filter(Objects::nonNull)
                                        .forEach(f -> {

                                            Long prodId = f.getProdId();
                                            if (null != prodId) {
                                                FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(prodId);
                                                if (null != financialProductDO) {
                                                    bankSet.add(financialProductDO.getBankName());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

        bankSet.removeAll(Collections.singleton(null));

        return ResultBean.ofSuccess(bankSet);
    }

    /**
     * 更新合伙人上牌地
     *
     * @param partnerParam
     * @return
     */
    @Override
    public ResultBean updatePartnerArea(PartnerParam partnerParam) {
        Preconditions.checkNotNull(partnerParam.getId(), "合伙人编号不能为空");
        partnerRelaAreaDOMapper.deleteAllByPartnerId(partnerParam.getId());
        partnerParam.getAreaIdList().stream().distinct().filter(Objects::nonNull).forEach(areaId -> {
            PartnerRelaAreaDO partnerRelaAreaDO = new PartnerRelaAreaDO();
            partnerRelaAreaDO.setPartnerId(partnerParam.getId());
            partnerRelaAreaDO.setAreaId(areaId);
            partnerRelaAreaDO.setGmtCreate(new Date());
            partnerRelaAreaDOMapper.insert(partnerRelaAreaDO);
        });
        return ResultBean.ofSuccess(null, "合伙人上牌地区域保存成功");
    }


    /**
     * 创建实体，并返回主键ID
     *
     * @param partnerParam
     * @return
     */

    private Long insertAndGetId(PartnerParam partnerParam) {
        List<String> nameList = partnerDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(partnerParam.getName()), "团队名称已存在");

        PartnerDO partnerDO = new PartnerDO();
        BeanUtils.copyProperties(partnerParam, partnerDO);
        partnerDO.setGmtCreate(new Date());
        partnerDO.setGmtModify(new Date());

        int count = partnerDOMapper.insertSelective(partnerDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return partnerDO.getId();
    }

    /**
     * 编辑绑定业务产品的限制区域
     *
     * @param id
     * @param areaId
     */
    private void updateRelaBizModelArea(Long id, Long areaId) {
        if (null == areaId) {
            return;
        }
        // check
        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDO.setGroupId(id);
        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOS = bizModelRelaAreaPartnersDOMapper.listQuery(bizModelRelaAreaPartnersDO);
        if (CollectionUtils.isEmpty(bizModelRelaAreaPartnersDOS)) {
            return;
        }

        // update
        bizModelRelaAreaPartnersDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    if (areaId.equals(e.getAreaId())) {
                        return;
                    } else {
                        // delete
                        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDOKey = new BizModelRelaAreaPartnersDO();
                        bizModelRelaAreaPartnersDOKey.setBizId(e.getBizId());
                        bizModelRelaAreaPartnersDOKey.setAreaId(e.getAreaId());
                        bizModelRelaAreaPartnersDOKey.setGroupId(e.getGroupId());
                        int delCount = bizModelRelaAreaPartnersDOMapper.deleteByPrimaryKey(bizModelRelaAreaPartnersDOKey);
                        Preconditions.checkArgument(delCount > 0, "编辑业务区域失败");

                        // insert
                        e.setAreaId(areaId);
                        e.setGmtModify(new Date());
                        int insertCount = bizModelRelaAreaPartnersDOMapper.insert(e);
                        Preconditions.checkArgument(insertCount > 0, "编辑业务区域失败");
                    }
                });
    }

    /**
     * fillMsg
     *
     * @param partnerDO
     * @param partnerVO
     */
    private void fillMsg(PartnerDO partnerDO, PartnerVO partnerVO) {
        fillDepartment(partnerDO.getDepartmentId(), partnerVO);
        fillArea(partnerDO.getAreaId(), partnerVO);
        fillEmployeeNum(partnerVO);
        // 财务合作信息
        fillBankAccount(partnerDO.getId(), partnerVO);
        // 团队负责人
        fillLeader(partnerVO.getLeaderId(), partnerVO);
    }

    private void fillLeader(Long leaderId, PartnerVO partnerVO) {

        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(leaderId, null);

        if (null != employeeDO) {
            partnerVO.setLeaderName(employeeDO.getName());
            partnerVO.setLeaderMobile(employeeDO.getMobile());
            partnerVO.setLeaderIdCard(employeeDO.getIdCard());
            partnerVO.setLeaderEmail(employeeDO.getEmail());
            partnerVO.setLeaderDingDing(employeeDO.getDingDing());
        }
    }

    /**
     * 财务合作信息
     *
     * @param partnerId
     * @param partnerVO
     */
    private void fillBankAccount(Long partnerId, PartnerVO partnerVO) {
        List<PartnerBankAccountDO> partnerBankAccountDOList = partnerBankAccountDOMapper.listByPartnerId(partnerId);
        if (!CollectionUtils.isEmpty(partnerBankAccountDOList)) {
            partnerVO.setBankAccountList(partnerBankAccountDOList);
        }
    }

    /**
     * 填充部门信息
     *
     * @param departmentId
     * @param partnerVO
     */
    private void fillDepartment(Long departmentId, PartnerVO partnerVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);
            // 递归填充所有上层父级部门
            fillSuperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), partnerVO);

            // 填充部门负责人
            fillDepartmentLeader(departmentDO.getLeaderId(), partnerVO);
        }
    }

    /**
     * 递归填充所有上层父级部门
     * <p>
     * 前端需求：仅需要层级ID即可
     *
     * @param parentId
     * @param superDepartmentList
     * @param partnerVO
     */
    private void fillSuperDepartment(Long parentId, List<BaseVO> superDepartmentList, PartnerVO partnerVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                superDepartmentList.add(parentDepartment);
                fillSuperDepartment(departmentDO.getParentId(), superDepartmentList, partnerVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superDepartmentList);
            partnerVO.setDepartment(superDepartmentList);
        }
    }

    /**
     * 填充部门负责人
     *
     * @param leaderId
     * @param partnerVO
     */
    private void fillDepartmentLeader(Long leaderId, PartnerVO partnerVO) {
        if (null == leaderId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(leaderId, VALID_STATUS);
        if (null != employeeDO) {
            BaseVO parentEmployee = new BaseVO();
            BeanUtils.copyProperties(employeeDO, parentEmployee);
            // 递归填充父级部门负责人
            fillSuperDepartmentLeader(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), partnerVO);
        }
    }

    /**
     * 递归填充父级部门负责人
     *
     * @param parentId
     * @param superLeaderList
     * @param partnerVO
     */
    private void fillSuperDepartmentLeader(Long parentId, List<BaseVO> superLeaderList, PartnerVO partnerVO) {
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                superLeaderList.add(parentEmployee);
                fillSuperDepartmentLeader(employeeDO.getParentId(), superLeaderList, partnerVO);
            }
        } else {
            Collections.reverse(superLeaderList);
            partnerVO.setDepartmentLeader(superLeaderList);
        }
    }

    /**
     * 填充区域(城市)信息
     *
     * @param areaId
     * @param partnerVO
     */
    private void fillArea(Long areaId, PartnerVO partnerVO) {
        if (null == areaId) {
            return;
        }
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO parentArea = new BaseVO();
            parentArea.setId(baseAreaDO.getAreaId());
            parentArea.setName(baseAreaDO.getAreaName());
            // 递归填充所有上层父级区域
            fillSupperArea(baseAreaDO.getParentAreaId(), Lists.newArrayList(parentArea), partnerVO);
        }
    }

    /**
     * 递归填充所有上层父级区域
     *
     * @param parentId
     * @param supperAreaList
     * @param partnerVO
     */
    private void fillSupperArea(Long parentId, List<BaseVO> supperAreaList, PartnerVO partnerVO) {
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                BaseVO parentArea = new BaseVO();
                parentArea.setId(baseAreaDO.getAreaId());
                parentArea.setName(baseAreaDO.getAreaName());
                supperAreaList.add(parentArea);
                fillSupperArea(baseAreaDO.getParentAreaId(), supperAreaList, partnerVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperAreaList);
            partnerVO.setArea(supperAreaList);
        }
    }

    /**
     * 填充合伙人绑定的员工总数
     *
     * @param partnerVO
     */
    private void fillEmployeeNum(PartnerVO partnerVO) {
        RelaQuery query = new RelaQuery();
        query.setId(partnerVO.getId());
        int employeeNum = employeeDOMapper.countListEmployeeByPartnerId(query);
        partnerVO.setEmployeeNum(employeeNum);
    }

    /**
     * 绑定业务产品列表
     *
     * @param partnerId      合伙人ID
     * @param areaId         业务限制区域ID
     * @param bizModelIdList 业务产品ID列表
     */
    private void bindBizModel(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        if (CollectionUtils.isEmpty(bizModelIdList)) {
            return;
        }
        Preconditions.checkNotNull(areaId, "业务限制区域ID不能为空");

        // 去重
        distinctBizModelIdList(partnerId, areaId, bizModelIdList);

        // 执行绑定
        execBindBizModel(partnerId, areaId, bizModelIdList);
    }

    /**
     * 业务产品ID去重
     *
     * @param partnerId
     * @param areaId
     * @param bizModelIdList
     */
    private void distinctBizModelIdList(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDO.setGroupId(partnerId);
        bizModelRelaAreaPartnersDO.setAreaId(areaId);
        List<BizModelRelaAreaPartnersDO> existBizModelRelaAreaPartnersDOS = bizModelRelaAreaPartnersDOMapper.listQuery(bizModelRelaAreaPartnersDO);
        if (!CollectionUtils.isEmpty(existBizModelRelaAreaPartnersDOS)) {
            List<Long> existBizModelIdList = existBizModelRelaAreaPartnersDOS.stream()
                    .filter(e -> null != e && null != e.getBizId())
                    .map(e -> {
                        return e.getBizId();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> repeatTmp = Lists.newArrayList();
            bizModelIdList.stream()
                    .forEach(e -> {
                        if (existBizModelIdList.contains(e)) {
                            repeatTmp.add(e);
                        }
                    });

            bizModelIdList.removeAll(repeatTmp);
        }
    }

    /**
     * 执行绑定
     *
     * @param partnerId
     * @param areaId
     * @param bizModelIdList
     */
    private void execBindBizModel(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        bizModelIdList.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(bizModelId -> {

                    BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                    bizModelRelaAreaPartnersDO.setGroupId(partnerId);
                    bizModelRelaAreaPartnersDO.setBizId(bizModelId);
                    bizModelRelaAreaPartnersDO.setAreaId(areaId);
                    bizModelRelaAreaPartnersDO.setGmtCreate(new Date());
                    bizModelRelaAreaPartnersDO.setGmtModify(new Date());
                    int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
                    Preconditions.checkArgument(count > 0, "关联业务产品失败");
                });
    }

    /**
     * 合伙人绑定员工列表
     *
     * @param partnerId
     * @param employeeIdList
     */
    private void bindEmployee(Long partnerId, List<Long> employeeIdList) {
        // 去重
        List<Long> existEmployeeIdList = partnerRelaEmployeeDOMapper.getEmployeeIdListByPartnerId(partnerId);
        if (!CollectionUtils.isEmpty(existEmployeeIdList)) {
            employeeIdList = employeeIdList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        if (!existEmployeeIdList.contains(e)) {
                            return e;
                        }
                        return null;
                    })
                    .distinct()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 执行绑定
        if (!CollectionUtils.isEmpty(employeeIdList)) {

            List<PartnerRelaEmployeeDO> partnerRelaEmployeeDOS = employeeIdList.stream()
                    .map(employeeId -> {

                        PartnerRelaEmployeeDO partnerRelaEmployeeDO = new PartnerRelaEmployeeDO();
                        partnerRelaEmployeeDO.setPartnerId(partnerId);
                        partnerRelaEmployeeDO.setEmployeeId(employeeId);
                        partnerRelaEmployeeDO.setGmtCreate(new Date());
                        partnerRelaEmployeeDO.setGmtModify(new Date());

                        return partnerRelaEmployeeDO;
                    })
                    .collect(Collectors.toList());

            int count = partnerRelaEmployeeDOMapper.batchInsert(partnerRelaEmployeeDOS);
            Preconditions.checkArgument(count == partnerRelaEmployeeDOS.size(), "关联失败");
        }

        // TODO 执行绑定授权（合伙人）
    }

    /**
     * 根据areaId填充所有子级areaId(含自身)
     *
     * @param query
     */
    private void getAndSetCascadeChildAreaIdList(PartnerQuery query) {
        // getAllCascadeAreaIdList
        List<Long> allChildAreaIdList = getAllChildAreaIdList(query.getAreaId());
        allChildAreaIdList.removeAll(Collections.singleton(null));
        // set
        query.setCascadeChildAreaIdList(allChildAreaIdList);
    }

    /**
     * 获取所有子级areaId(含自身)
     *
     * @param areaId
     * @return
     */
    private List<Long> getAllChildAreaIdList(Long areaId) {
        List<Long> childAreaIdList = Lists.newArrayList(areaId);

//        List<BaseAreaDO> childBaseAreaDOList = baseAreaDOMapper.getByParentAreaId(areaId, VALID_STATUS);
//        if (!CollectionUtils.isEmpty(childBaseAreaDOList)) {
//
//            childBaseAreaDOList.stream()
//                    .filter(Objects::nonNull)
//                    .map(e -> {
//
//                        Long parentAreaId = e.getAreaId();
//                        childAreaIdList.add(parentAreaId);
//
//                        getAndSetChildAreaIdList(parentAreaId, childAreaIdList);
//                    })
//        }

        return childAreaIdList;
    }

    private void getAndSetChildAreaIdList(Long parentAreaId, List<Long> childAreaIdList) {
//        List<BaseAreaDO> childBaseAreaDOList = baseAreaDOMapper.getByParentAreaId(areaId, VALID_STATUS);
//        if (!CollectionUtils.isEmpty(childBaseAreaDOList)) {
//
//            childBaseAreaDOList.stream()
//
//        }

    }

    /**
     * 根据areaId填充所有父级areaId(含自身)
     *
     * @param query
     */
    private void getAndSetCascadeSuperAreaIdList(BizModelQuery query) {
        // getAllCascadeAreaIdList
        List<Long> allSuperAreaIdList = getAllSuperAreaIdList(query.getAreaId());
        allSuperAreaIdList.removeAll(Collections.singleton(null));
        // set
        query.setCascadeAreaIdList(allSuperAreaIdList);
    }

    /**
     * 获取所有父级areaId(含自身)
     *
     * @param areaId
     * @return
     */
    private List<Long> getAllSuperAreaIdList(Long areaId) {
        List<Long> superAreaIdList = Lists.newArrayList(areaId);

        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {

            BaseAreaDO parentBaseAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId(), VALID_STATUS);
            if (null != parentBaseAreaDO) {
                superAreaIdList.add(parentBaseAreaDO.getParentAreaId());
                getAndSetSuperAreaId(parentBaseAreaDO.getParentAreaId(), superAreaIdList);
            }
        }

        return superAreaIdList;
    }

    /**
     * 递归获取父级AreaId
     *
     * @param parentAreaId
     * @param superAreaIdList
     */
    private void getAndSetSuperAreaId(Long parentAreaId, List<Long> superAreaIdList) {
        if (null != parentAreaId) {
            BaseAreaDO parentBaseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, VALID_STATUS);
            if (null != parentBaseAreaDO) {
                superAreaIdList.add(parentBaseAreaDO.getParentAreaId());
                getAndSetSuperAreaId(parentBaseAreaDO.getParentAreaId(), superAreaIdList);
            }
        }
    }

    /**
     * 截取分页ID
     *
     * @param bizModelIdList
     * @param startRow
     * @param pageSize
     * @return
     */
    private List<Long> pagingBizModelIdList(List<Long> bizModelIdList, Integer startRow, Integer pageSize) {

        int fromIndex = 0;
        int toIndex = 0;
        int totalNum = bizModelIdList.size();

        if (startRow > totalNum) {
            return null;
        } else {
            fromIndex = startRow;
        }

        if (pageSize + startRow > totalNum) {
            toIndex = totalNum;
        } else {
            toIndex = pageSize + startRow;
        }

        // ID大小排序
        List<Long> pageIdList = bizModelIdList.stream()
                .sorted()
                .collect(Collectors.toList());

        // pageID截取
        List<Long> pagingPageIdList = pageIdList.subList(fromIndex, toIndex);

        return pagingPageIdList;
    }

    /**
     * 填充员工直接主管信息
     *
     * @param parentId
     * @param employeeVO
     */
    private void fillParent(Long parentId, EmployeeVO employeeVO) {
        if (null == parentId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
        if (null != employeeDO) {
            employeeVO.setParentName(employeeDO.getName());
            BaseVO parentEmployee = new BaseVO();
            BeanUtils.copyProperties(employeeDO, parentEmployee);
            // 递归填充所有上层父级leader
            fillSupperEmployee(employeeDO.getParentId(), Lists.newArrayList(parentEmployee), employeeVO, 10);
        }
    }

    private void fillSupperEmployee(Long parentId, List<BaseVO> supperEmployeeList, EmployeeVO employeeVO, Integer limit) {
        limit--;
        if (limit < 0) {
            return;
        }
        if (null != parentId) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != employeeDO) {
                BaseVO parentEmployee = new BaseVO();
                BeanUtils.copyProperties(employeeDO, parentEmployee);
                supperEmployeeList.add(parentEmployee);
                fillSupperEmployee(employeeDO.getParentId(), supperEmployeeList, employeeVO, limit);
            }
        } else {
            // null时为最顶级
            Collections.reverse(supperEmployeeList);
            employeeVO.setParent(supperEmployeeList);
        }
    }
}
