package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.config.cache.EmployeeCache;
import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.MD5Utils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.cache.EmployeeCache.*;
import static com.yunche.loan.config.constant.AreaConst.COUNTRY_AREA_ID;
import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_WB;
import static com.yunche.loan.config.constant.EmployeeConst.TYPE_ZS;
import static com.yunche.loan.service.impl.CarServiceImpl.NEW_LINE;
import static org.apache.shiro.web.mgt.CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME;
import static org.springframework.web.util.WebUtils.getCookie;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    /**
     * APP端session过期时间：180天 (单位：毫秒)
     */
    private static final long TERMINAL_SESSION_TIMEOUT = 3600 * 24 * 180 * 1000;
    /**
     * 账号初始密码
     */
    public static final String initPassword = "111111";

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmployeeCache employeeCache;

    @Autowired
    private AreaCache areaCache;

    @Autowired
    private PermissionService permissionService;


    @Override
    @Transactional
    public ResultBean<Long> create(EmployeeParam employeeParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getName()), "姓名不能为空");
        Preconditions.checkArgument(!"admin".equals(employeeParam.getName()), "账号名不能为admin");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getIdCard()), "身份证号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getMobile()), "手机号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getEmail()), "电子邮箱不能为空");
        Preconditions.checkNotNull(employeeParam.getStatus(), "员工状态不能为空");
        Preconditions.checkNotNull(employeeParam.getType(), "员工类型不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(employeeParam.getStatus()) || INVALID_STATUS.equals(employeeParam.getStatus()),
                "员工状态非法");
        Preconditions.checkArgument(TYPE_ZS.equals(employeeParam.getType()) || TYPE_WB.equals(employeeParam.getType()),
                "员工类型非法");

        // WB，必须绑定直接上级
        if (TYPE_WB.equals(employeeParam.getType())) {
            Preconditions.checkNotNull(employeeParam.getParentId(), "业务员直接上级不能为空");
        }

        // 校验唯一属性(身份证号、手机号、邮箱、钉钉)
        checkOnlyProperty(employeeParam);

        // 生成随机密码
//        String password = MD5Utils.getRandomString(10);

        // 初始密码
        String md5Password = MD5Utils.md5(initPassword);
        employeeParam.setPassword(md5Password);

        // 创建实体，并返回ID
        Long id = insertAndGetId(employeeParam);

        // 绑定用户组(角色)列表
        doBindUserGroup(id, employeeParam.getUserGroupIdList());

        // 发送账号密码到邮箱
//        executorService.execute(() -> {
//            sentAccountAndPassword(employeeParam.getEmail(), password);
//        });

        // 刷新缓存
        employeeCache.refresh();

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(EmployeeDO employeeDO) {
        Preconditions.checkNotNull(employeeDO.getId(), "id不能为空");
        Preconditions.checkArgument(!employeeDO.getId().equals(employeeDO.getParentId()), "直接主管不能为自己");

        // 更新Parent
        updateParent(employeeDO.getId(), employeeDO.getParentId());

        // 禁止通过update更新密码
        employeeDO.setPassword(null);
        employeeDO.setGmtModify(new Date());
        int count = employeeDOMapper.updateByPrimaryKeySelective(employeeDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 刷新缓存
        employeeCache.refresh();

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    /**
     * 更新Z的Parent
     * <p>
     * X  ->  Z的 旧上级
     * Y  ->  Z的 新上级
     * Z  ->  被更新parent者
     * sss
     *
     * @param id_Z          Z
     * @param newParentId_Y Y
     */
    private void updateParent(Long id_Z, Long newParentId_Y) {
        if (newParentId_Y == null) {
            return;
        }

        BaseVO employee_Z = employeeCache.getById(id_Z);
        Preconditions.checkNotNull(employee_Z, "账号不存在");

        // 旧上级 X
        Long oldParentId_X = employee_Z.getParentId();

        // 新上级 Y     ->     newParentId_Y

        // Y的新上级设置为X
        if (null == oldParentId_X) {
            employeeDOMapper.setParentIdIsNull(newParentId_Y);
        } else {
            EmployeeDO employeeDO_Y = new EmployeeDO();
            employeeDO_Y.setId(newParentId_Y);
            employeeDO_Y.setParentId(oldParentId_X);

            employeeDO_Y.setGmtModify(new Date());
            int count = employeeDOMapper.updateByPrimaryKeySelective(employeeDO_Y);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }
    }

    @Override
    @Transactional
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        List<Long> childList = employeeDOMapper.listChildByParentId(id);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(childList), "请先解绑该账号下的所有子账号");

        int count = employeeDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        // 刷新缓存
        employeeCache.refresh();

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<EmployeeVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(employeeDO, "id有误，数据不存在");

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employeeDO, employeeVO);

        // 填充直接上级信息
        fillParent(employeeDO.getParentId(), employeeVO);
        // 填充所属部门信息
        fillDepartment(employeeDO.getDepartmentId(), employeeVO);
        // 填充所属合伙人信息
        fillPartner(employeeVO);

        return ResultBean.ofSuccess(employeeVO);
    }

    /**
     * 返回业务员所属合伙人团队
     *
     * @param employeeVO
     */
    private void fillPartner(EmployeeVO employeeVO) {
        if (TYPE_WB.equals(employeeVO.getType())) {
            Long employeeId = employeeVO.getId();
            Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(employeeId);
            employeeVO.setPartnerId(partnerId);
        }
    }

    @Override
    public ResultBean<List<EmployeeVO>> query(EmployeeQuery query) {
        int totalNum = employeeDOMapper.count(query);
        if (totalNum > 0) {

            List<EmployeeDO> employeeDOS = employeeDOMapper.query(query);
            if (!CollectionUtils.isEmpty(employeeDOS)) {

                List<EmployeeVO> employeeVOS = employeeDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            EmployeeVO employeeVO = new EmployeeVO();
                            BeanUtils.copyProperties(e, employeeVO);

                            // 填充直接上级信息
                            fillParent(e.getParentId(), employeeVO);
                            // 填充所属部门信息
                            fillDepartment(e.getDepartmentId(), employeeVO);

                            return employeeVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(employeeVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<CascadeVO>> listAll() {
        // 走缓存
        List<CascadeVO> cascadeVOS = employeeCache.get(TYPE_ZS);
        return ResultBean.ofSuccess(cascadeVOS);
    }

    @Override
    public ResultBean<List<String>> listTitle() {
        List<String> listTitle = employeeDOMapper.listTitle();
        listTitle.removeAll(Collections.singleton(null));
        return ResultBean.ofSuccess(listTitle);
    }

    @Override
    public ResultBean<List<UserGroupVO>> listUserGroup(RelaQuery query) {
        Preconditions.checkNotNull(query.getId(), "员工ID不能为空");
        // 填充所有子级区域
        getAndFillChildAreaList(query);

        int totalNum = userGroupDOMapper.countListUserGroupByEmployeeIdAndAreaList(query);
        if (totalNum > 0) {

            List<UserGroupDO> userGroupDOS = userGroupDOMapper.listUserGroupByEmployeeIdAndAreaList(query);
            if (!CollectionUtils.isEmpty(userGroupDOS)) {

                List<UserGroupVO> userGroupVOList = userGroupDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(userGroupDO -> {

                            UserGroupVO userGroupVO = new UserGroupVO();
                            BeanUtils.copyProperties(userGroupDO, userGroupVO);

                            fillDepartment(userGroupDO.getDepartmentId(), userGroupVO);
                            fillArea(userGroupVO);

                            return userGroupVO;
                        })
                        .sorted(Comparator.comparing(UserGroupVO::getId))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(userGroupVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    /**
     * 填充所有子级区域（含自身）
     *
     * @param query
     */
    private void getAndFillChildAreaList(RelaQuery query) {
        Long areaId = query.getAreaId();
        if (null == areaId) {
            return;
        }

        // 全国
        if (COUNTRY_AREA_ID.equals(areaId)) {
            return;
        }

        List<Long> allChildAreaIdList = areaCache.getAllChildAreaIdList(areaId);
        allChildAreaIdList.add(areaId);
        query.setAreaIdList(allChildAreaIdList);
    }

    @Override
    public ResultBean<Void> bindUserGroup(Long id, String userGroupIds) {
        Preconditions.checkNotNull(id, "员工ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupIds), "用户组ID不能为空");

        // convert
        List<Long> userGroupIdList = Arrays.asList(userGroupIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .collect(Collectors.toList());

        // do
        doBindUserGroup(id, userGroupIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindUserGroup(Long id, String userGroupIds) {
        Preconditions.checkNotNull(id, "员工ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupIds), "用户组ID不能为空");

        Arrays.asList(userGroupIds.split(",")).stream()
                .distinct()
                .forEach(userGroupId -> {
                    EmployeeRelaUserGroupDOKey employeeRelaUserGroupDOKey = new EmployeeRelaUserGroupDOKey();
                    employeeRelaUserGroupDOKey.setEmployeeId(id);
                    employeeRelaUserGroupDOKey.setUserGroupId(Long.valueOf(userGroupId));
                    int count = employeeRelaUserGroupDOMapper.deleteByPrimaryKey(employeeRelaUserGroupDOKey);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> resetPassword(Long id) {
        Preconditions.checkNotNull(id, "员工ID不能为空");

        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(employeeDO, "账号不存在或已停用");

        // 生成随机密码
        String password = MD5Utils.getRandomString(10);

        // MD5加密
        String md5Password = MD5Utils.md5(password);
        EmployeeDO updateEmployeeDO = new EmployeeDO();
        updateEmployeeDO.setId(id);
        updateEmployeeDO.setPassword(md5Password);
        updateEmployeeDO.setGmtModify(new Date());

        int count = employeeDOMapper.updateByPrimaryKeySelective(updateEmployeeDO);
        Preconditions.checkArgument(count > 0, "重置密码失败");

        // 发送账号密码到邮箱
        sentAccountAndPassword(employeeDO.getEmail(), password);

        return ResultBean.ofSuccess(null, "重置密码成功");
    }

    @Override
    public ResultBean<LoginVO> login(HttpServletRequest request, HttpServletResponse response, EmployeeParam employeeParam) {
        Preconditions.checkArgument(null != employeeParam && StringUtils.isNotBlank(employeeParam.getUsername()), "登陆账号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getPassword()), "密码不能为空");

        String origin = request.getHeader("origin");
        response.setHeader("Access-Control-Allow-Origin", origin);

        // 使用shiro提供的方式进行身份认证
        Subject subject = SecurityUtils.getSubject();
        String username = employeeParam.getUsername();
        String password = employeeParam.getPassword();
        String machineId = employeeParam.getMachineId();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            // 更新rememberMeCookie
            rememberMeCookie(token, request, employeeParam.getRememberMe(), employeeParam.getIsTerminal());

            // 调用安全管理器，安全管理器调用Realm
            subject.login(token);

        } catch (UnknownAccountException e) {
            //用户名不存在
            return ResultBean.ofError("用户名或密码错误");
        } catch (IncorrectCredentialsException e) {
            // 密码错误
            return ResultBean.ofError("用户名或密码错误");
        } catch (DisabledAccountException e) {
            // 账户冻结
            return ResultBean.ofError("账号已停用");
        }

        // 如果是移动端登录，更新会话有效期为1年
        Boolean isTerminal = employeeParam.getIsTerminal();
        if (isTerminal) {
            // APP端Session过期时间
            SecurityUtils.getSubject().getSession().setTimeout(TERMINAL_SESSION_TIMEOUT);

            // APP端更新MachineId
            EmployeeDO emp = (EmployeeDO) subject.getPrincipal();
            EmployeeDO emp_ = new EmployeeDO();
            emp_.setId(emp.getId());
            emp_.setMachineId(machineId);
            emp_.setGmtModify(new Date());
            employeeDOMapper.updateByPrimaryKeySelective(emp_);
        }

        // 返回data
        LoginVO data = setAndGetLoginVO(subject);

        return ResultBean.ofSuccess(data, "登录成功");
    }

    /**
     * LoginVO
     *
     * @param subject
     * @return
     */
    private LoginVO setAndGetLoginVO(Subject subject) {
        LoginVO data = new LoginVO();
        EmployeeDO user = (EmployeeDO) subject.getPrincipal();
        BeanUtils.copyProperties(user, data);
        data.setPassword(null);
        data.setMachineId(null);

        data.setUserId(user.getId());
        data.setUsername(user.getName());

        // 角色
        Set<String> userGroupNameSet = permissionService.getUserGroupNameSet();
        data.setUserGroupSet(userGroupNameSet);

        return data;
    }

    /**
     * 更新rememberMeCookie
     *
     * @param token
     * @param request
     * @param rememberMe
     * @param isTerminal
     */
    private void rememberMeCookie(UsernamePasswordToken token, HttpServletRequest request, Integer rememberMe, Boolean isTerminal) {

        // APP
        if (isTerminal) {
            // RememberMe
            token.setRememberMe(true);

            Cookie rememberCookie = getCookie(request, DEFAULT_REMEMBER_ME_COOKIE_NAME);
            if (null != rememberCookie) {
                rememberCookie.setHttpOnly(true);
                rememberCookie.setMaxAge((int) (TERMINAL_SESSION_TIMEOUT / 1000));
            }
        } else {
            // WEB
            if (null != rememberMe && rememberMe > 0) {
                // RememberMe
                token.setRememberMe(true);

                Cookie rememberCookie = getCookie(request, DEFAULT_REMEMBER_ME_COOKIE_NAME);
                if (null != rememberCookie) {
                    rememberCookie.setHttpOnly(true);
                    rememberCookie.setMaxAge(3600 * 24 * rememberMe);
                }
            }
        }
    }

    @Override
    @Transactional
    public ResultBean<Void> logout() {

        try {
            // APP清空machineId
            Long userId = SessionUtils.getLoginUser().getId();
            employeeDOMapper.setMachineIdForNull(userId);
        } catch (Exception ex) {
            logger.error("getLoginUser error", ex);
        }

        // 清空shiro会话
        SecurityUtils.getSubject().logout();
        return ResultBean.ofSuccess(null, "登出成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> editPassword(EmployeeParam employeeParam) {
        Preconditions.checkArgument(null != employeeParam && StringUtils.isNotBlank(employeeParam.getOldPassword()), "原密码不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeParam.getNewPassword()), "新密码不能为空");
        Preconditions.checkArgument(!employeeParam.getOldPassword().equals(employeeParam.getNewPassword()), "新旧密码不能相同");

        // 从session中获取User
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        if (null == loginUser) {
            return ResultBean.ofError(BaseExceptionEnum.NOT_LOGIN);
        }
        Preconditions.checkArgument(MD5Utils.verify(employeeParam.getOldPassword(), loginUser.getPassword()), "原密码有误");

        EmployeeDO updateEmployee = new EmployeeDO();
        updateEmployee.setId(loginUser.getId());
        updateEmployee.setPassword(MD5Utils.md5(employeeParam.getNewPassword()));
        updateEmployee.setGmtModify(new Date());
        int count = employeeDOMapper.updateByPrimaryKeySelective(updateEmployee);
        Preconditions.checkArgument(count > 0, "密码修改失败");

        // 登出
        logout();

        return ResultBean.ofSuccess(null, "密码修改成功，请重新登录！");
    }

    @Override
    public Set<String> getSelfAndCascadeChildIdList(Long parentId) {
        Preconditions.checkNotNull(parentId, "parentId不能为空");

        Set<String> cascadeChildIdList = employeeCache.getCascadeChildIdList(parentId);

        Set<String> selfAndCascadeChildIdList = Sets.newHashSet(String.valueOf(parentId));
        selfAndCascadeChildIdList.addAll(cascadeChildIdList);
        selfAndCascadeChildIdList.removeAll(Collections.singleton(null));

        return selfAndCascadeChildIdList;
    }

    /**
     * 校验唯一属性 (身份证号、手机号、邮箱、钉钉)
     *
     * @param employeeParam
     */

    private void checkOnlyProperty(EmployeeParam employeeParam) {

        List<String> idCardList = employeeCache.getAllOnlyProperty(ID_CARD);
        List<String> mobileList = employeeCache.getAllOnlyProperty(MOBILE);
        List<String> emailList = employeeCache.getAllOnlyProperty(EMAIL);
        List<String> dingDingList = employeeCache.getAllOnlyProperty(DINGDING);

        if (!CollectionUtils.isEmpty(idCardList)) {
            Preconditions.checkArgument(!idCardList.contains(employeeParam.getIdCard()), "该身份证号已被注册");
        }
        if (!CollectionUtils.isEmpty(mobileList)) {
            Preconditions.checkArgument(!mobileList.contains(employeeParam.getMobile()), "该手机号已被注册");
        }
        if (!CollectionUtils.isEmpty(emailList)) {
            Preconditions.checkArgument(!emailList.contains(employeeParam.getEmail()), "该邮箱已被注册");
        }
        if (!CollectionUtils.isEmpty(dingDingList)) {
            Preconditions.checkArgument(!dingDingList.contains(employeeParam.getDingDing()), "该钉钉号已被注册");
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


    /**
     * 插入实体，并返回主键ID
     *
     * @param employeeParam
     * @return
     */
    private Long insertAndGetId(EmployeeParam employeeParam) {
        EmployeeDO employeeDO = new EmployeeDO();
        BeanUtils.copyProperties(employeeParam, employeeDO);

        // level
        Long parentId = employeeParam.getParentId();
        if (null != parentId) {
            EmployeeDO parentEmployeeDO = employeeDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            Preconditions.checkNotNull(parentEmployeeDO, "直接主管不存在");
            Integer parentLevel = parentEmployeeDO.getLevel();
            Integer level = parentLevel == null ? null : parentLevel + 1;
            employeeDO.setLevel(level);
        } else {
            employeeDO.setLevel(1);
        }

        // date
        employeeDO.setGmtCreate(new Date());
        employeeDO.setGmtModify(new Date());

        int count = employeeDOMapper.insertSelective(employeeDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return employeeDO.getId();
    }

    /**
     * 填充员工部门信息
     *
     * @param departmentId
     * @param employeeVO
     */
    private void fillDepartment(Long departmentId, EmployeeVO employeeVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);

            // 预加载
//            List<DepartmentDO> all = departmentDOMapper.getAll(VALID_STATUS);

            // 递归填充所有上层父级部门
            fillSuperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), employeeVO);
        }
    }

    private void fillSuperDepartment(Long parentId, List<BaseVO> superDepartmentList, EmployeeVO employeeVO) {
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                superDepartmentList.add(parentDepartment);
                fillSuperDepartment(departmentDO.getParentId(), superDepartmentList, employeeVO);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superDepartmentList);
            employeeVO.setDepartment(superDepartmentList);
        }
    }

    /**
     * 补充用户组区域信息
     *
     * @param userGroupVO
     */
    private void fillArea(UserGroupVO userGroupVO) {
        List<Long> areaIds = userGroupRelaAreaAuthDOMapper.getAreaIdListByUserGroupId(userGroupVO.getId());
        if (CollectionUtils.isEmpty(areaIds)) {
            return;
        }
        Long areaId = areaIds.get(0);
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO parentArea = new BaseVO();
            parentArea.setId(baseAreaDO.getAreaId());
            parentArea.setName(baseAreaDO.getAreaName());
            // 递归填充所有上层父级部门
            fillSuperArea(baseAreaDO.getParentAreaId(), Lists.newArrayList(parentArea), userGroupVO, 10);
        }
    }

    /**
     * 递归填充所有上层父级部门
     *
     * @param parentId
     * @param superAreaList
     * @param userGroupVO
     * @param limit
     */
    private void fillSuperArea(Long parentId, List<BaseVO> superAreaList, UserGroupVO userGroupVO, Integer limit) {
        limit--;
        if (limit < 0) {
            return;
        }
        if (null != parentId) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != baseAreaDO) {
                BaseVO parentArea = new BaseVO();
                parentArea.setId(baseAreaDO.getAreaId());
                parentArea.setName(baseAreaDO.getAreaName());
                superAreaList.add(parentArea);
                fillSuperArea(baseAreaDO.getParentAreaId(), superAreaList, userGroupVO, limit);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superAreaList);
            userGroupVO.setArea(superAreaList);
        }
    }

    /**
     * 填充用户组部门信息
     *
     * @param departmentId
     * @param userGroupVO
     */
    private void fillDepartment(Long departmentId, UserGroupVO userGroupVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO parentDepartment = new BaseVO();
            BeanUtils.copyProperties(departmentDO, parentDepartment);
            // 递归填充所有上层父级部门
            fillSuperDepartment(departmentDO.getParentId(), Lists.newArrayList(parentDepartment), userGroupVO, 10);
        }
    }

    /**
     * 递归填充所有上层父级部门ID
     * <p>
     * 前端需求：仅需要层级ID即可
     *
     * @param parentId
     * @param superDepartmentList
     * @param userGroupVO
     * @param limit
     */
    private void fillSuperDepartment(Long parentId, List<BaseVO> superDepartmentList, UserGroupVO userGroupVO, Integer limit) {
        limit--;
        if (limit < 0) {
            return;
        }
        if (null != parentId) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(parentId, VALID_STATUS);
            if (null != departmentDO) {
                BaseVO parentDepartment = new BaseVO();
                BeanUtils.copyProperties(departmentDO, parentDepartment);
                superDepartmentList.add(parentDepartment);
                fillSuperDepartment(departmentDO.getParentId(), superDepartmentList, userGroupVO, limit);
            }
        } else {
            // null时为最顶级
            Collections.reverse(superDepartmentList);
            userGroupVO.setDepartment(superDepartmentList);
        }
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

    /**
     * 递归填充所有上层父级leader
     *
     * @param parentId
     * @param supperEmployeeList
     * @param employeeVO
     * @param limit
     */
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


    /**
     * 绑定用户组(角色)列表
     *
     * @param employeeId
     * @param userGroupIdList
     */
    private void doBindUserGroup(Long employeeId, List<Long> userGroupIdList) {
        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return;
        }

        // 去重
        List<Long> existUserGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(employeeId);
        if (!CollectionUtils.isEmpty(existUserGroupIdList)) {

            userGroupIdList = userGroupIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .map(e -> {
                        if (!existUserGroupIdList.contains(e)) {
                            return e;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // 绑定
        if (!CollectionUtils.isEmpty(userGroupIdList)) {
            List<EmployeeRelaUserGroupDO> employeeRelaUserGroupDOS = userGroupIdList.parallelStream()
                    .map(userGroupId -> {
                        EmployeeRelaUserGroupDO employeeRelaUserGroupDO = new EmployeeRelaUserGroupDO();
                        employeeRelaUserGroupDO.setEmployeeId(employeeId);
                        employeeRelaUserGroupDO.setUserGroupId(userGroupId);
                        employeeRelaUserGroupDO.setGmtCreate(new Date());
                        employeeRelaUserGroupDO.setGmtModify(new Date());

                        return employeeRelaUserGroupDO;
                    })
                    .collect(Collectors.toList());

            int count = employeeRelaUserGroupDOMapper.batchInsert(employeeRelaUserGroupDOS);
            Preconditions.checkArgument(count == employeeRelaUserGroupDOS.size(), "关联失败");
        }
    }
}
