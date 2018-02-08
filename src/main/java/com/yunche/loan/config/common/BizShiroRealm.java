package com.yunche.loan.config.common;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.dao.mapper.EmployeeDOMapper;
import com.yunche.loan.dao.mapper.EmployeeRelaUserGroupDOMapper;
import com.yunche.loan.dao.mapper.UserGroupRelaAreaAuthDOMapper;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import static com.yunche.loan.config.constant.BaseConst.*;

/**
 * 自定义Realm，进行认证和授权操作
 *
 * @author liuzhe
 * @date 2018/2/6
 */
public class BizShiroRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizingRealm.class);

    /**
     * redis-session过期时间：30min
     */
    private static final long SESSION_TIME_OUT = 1800L;

    @Value("${salt}")
    private String salt;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


//    /**
//     * 认证方法
//     *
//     * @param token
//     * @return
//     * @throws AuthenticationException
//     */
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
//
//        UsernamePasswordToken myToken = (UsernamePasswordToken) token;
//        String username = myToken.getUsername();
//        char[] password = myToken.getPassword();
//        // 根据用户名查询数据库中的密码，将密码交给安全管理器，由安全管理器对象负责比较数据库中的密码和页面传递的密码是否一致
//        EmployeeQuery query = new EmployeeQuery();
//        query.setEmail(username);
//        List<EmployeeDO> employeeDOS = employeeDOMapper.query(query);
//        if (CollectionUtils.isEmpty(employeeDOS)) {
//            return null;
//        }
//        // 参数一：签名对象，认证通过后，可以在程序的任意位置获取当前放入的对象
//        // 参数二：数据库中查询出的密码
//        // 参数三：当前realm的类名
//        EmployeeDO employeeDO = employeeDOS.get(0);
////        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(employeeDO, employeeDO.getPassword(), "",this.getClass().getName());
//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(employeeDO, employeeDO.getPassword(), this.getClass().getName());
//        return info;
//    }
//
//    /**
//     * 授权方法
//     *
//     * @param principals
//     * @return
//     */
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//
//        // 获取当前执行用户:
//        Subject currentUser = SecurityUtils.getSubject();
//
////        currentUser.execute();
//
//        //授权信息对象
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        //根据当前登录用户查询数据库，获得其对应的权限
//        EmployeeDO employeeDO = (EmployeeDO) principals.getPrimaryPrincipal();
//        if (employeeDO.getEmail().equals("admin")) {
//            //超级管理员，查询所有权限
//
//        } else {
//            //普通用户，根据用户查询对应的权限
//
//            // 获取用户所在用户组列表
//            List<Long> userGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(employeeDO.getId());
//
//            // 根据用户组获取所有权限ID
//            List<Long> hasBindAuthIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthIdListByUserGroupIdList(userGroupIdList);
//
//            hasBindAuthIdList.parallelStream()
//                    .forEach(e -> {
////                        info.addStringPermission(e);
//                    });
//        }
//        return info;
//    }


    /**
     * 认证方法
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        logger.debug("doGetAuthenticationInfo   >>>  认证");

        // 获取用户的输入的账号
        String username = (String) token.getPrincipal();

        // 通过username从数据库中查找 User对象，如果找到，没找到.
        // 根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        EmployeeDO employeeDO = getUser(username);
        logger.debug("employeeDO >>> ", JSON.toJSONString(employeeDO));

        if (null == employeeDO) {
            return null;
        }
        // 账户冻结
        if (INVALID_STATUS.equals(employeeDO.getStatus())) {
            throw new LockedAccountException("账号已停用");
        }
        // 账号已删除
        if (DEL_STATUS.equals(employeeDO.getStatus())) {
            throw new UnknownAccountException("账号不存在");
        }
        if (!VALID_STATUS.equals(employeeDO.getStatus())) {
            throw new UnknownAccountException("账号状态异常");
        }

        // shiro认证
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                // 用户
                employeeDO,
                // 密码
                employeeDO.getPassword(),
                // realm name
                this.getClass().getName()
        );

        // 认证通过，返回认证信息
        return info;
    }


    /**
     * 授权方法
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.debug("doGetAuthorizationInfo   >>>  授权");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        EmployeeDO employeeDO = (EmployeeDO) principals.getPrimaryPrincipal();

        // 获取用户的角色列表

        // 获取用户的权限实体列表
//        getAuthEntityList();
//
//        for (SysRole role : employeeDO.getRoleList()) {
//            authorizationInfo.addRole(role.getRole());
//            for (SysPermission p : role.getPermissions()) {
//                authorizationInfo.addStringPermission(p.getPermission());
//            }
//        }
        return authorizationInfo;
    }


    /**
     * 根据username获取用户
     *
     * @param username
     * @return
     */
    private EmployeeDO getUser(String username) {
        // 先走缓存
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(username);
        String sessionId = boundValueOps.get();
        if (StringUtils.isNotBlank(sessionId)) {
            boundValueOps.rename(sessionId);
            String userJson = boundValueOps.get();
            if (StringUtils.isNotBlank(userJson)) {
                return JSON.parseObject(userJson, EmployeeDO.class);
            }
        }

        // 缓存过期，走DB
        EmployeeDO employeeDO = employeeDOMapper.getByUsername(username, VALID_STATUS);
        return employeeDO;
    }

    /**
     * salt = username + salt
     *
     * @param username
     * @param salt
     * @return
     */
    public String getCredentialsSalt(String username, String salt) {
        String credentialsSalt = username + salt;
        return credentialsSalt;
    }
}
