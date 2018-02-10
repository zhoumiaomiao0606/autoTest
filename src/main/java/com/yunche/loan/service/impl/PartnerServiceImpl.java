package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.MD5Utils;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.queryObj.PartnerQuery;
import com.yunche.loan.domain.queryObj.RelaQuery;
import com.yunche.loan.domain.viewObj.BaseVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.PartnerVO;
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

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
@Transactional
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
    private JavaMailSender mailSender;


    @Override
    public ResultBean<Long> create(PartnerParam partnerParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getName()), "团队名称不能为空");
        Preconditions.checkNotNull(partnerParam.getDepartmentId(), "对应负责部门不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderName()), "团队负责人不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderMobile()), "负责人手机不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getOpenBank()), "开户行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getAccountName()), "开户名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getBankAccount()), "银行账号不能为空");
        Preconditions.checkNotNull(partnerParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(partnerParam.getStatus()) || INVALID_STATUS.equals(partnerParam.getStatus()),
                "状态非法");

        // 给合伙人创建一个账号
        createAccountIfNotExist(partnerParam.getLeaderMobile(), partnerParam.getName());

        // 创建实体，并返回ID
        Long id = insertAndGetId(partnerParam);

        // 绑定业务产品列表
        bindBizModel(id, partnerParam.getAreaId(), partnerParam.getBizModelIdList());

        return ResultBean.ofSuccess(id, "创建成功");
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
    public ResultBean<Void> update(PartnerDO partnerDO) {
        Preconditions.checkNotNull(partnerDO.getId(), "id不能为空");

        partnerDO.setGmtModify(new Date());
        int count = partnerDOMapper.updateByPrimaryKeySelective(partnerDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 编辑绑定业务产品的限制区域
        updateRelaBizModelArea(partnerDO.getId(), partnerDO.getAreaId());

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = partnerDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
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

                List<PartnerVO> partnerVOS = partnerDOS.parallelStream()
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

                List<BizModelVO> bizModelVOList = bizModelDOS.parallelStream()
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
                    int count = bizModelRelaAreaPartnersDOMapper.delete(bizModelRelaAreaPartnersDO);
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

                List<EmployeeVO> employeeVOList = employeeDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(employeeDO -> {

                            EmployeeVO employeeVO = new EmployeeVO();
                            BeanUtils.copyProperties(employeeDO, employeeVO);

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
        bizModelRelaAreaPartnersDOS.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    if (areaId.equals(e.getAreaId())) {
                        return;
                    } else {
                        // delete
                        int delCount = bizModelRelaAreaPartnersDOMapper.delete(e);
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
            List<Long> existBizModelIdList = existBizModelRelaAreaPartnersDOS.parallelStream()
                    .filter(e -> null != e && null != e.getBizId())
                    .map(e -> {
                        return e.getBizId();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> repeatTmp = Lists.newArrayList();
            bizModelIdList.parallelStream()
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
        bizModelIdList.parallelStream()
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
            employeeIdList = employeeIdList.parallelStream()
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

            List<PartnerRelaEmployeeDO> partnerRelaEmployeeDOS = employeeIdList.parallelStream()
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
//            childBaseAreaDOList.parallelStream()
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
//            childBaseAreaDOList.parallelStream()
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
        List<Long> pageIdList = bizModelIdList.parallelStream()
                .sorted()
                .collect(Collectors.toList());

        // pageID截取
        List<Long> pagingPageIdList = pageIdList.subList(fromIndex, toIndex);

        return pagingPageIdList;
    }
}
