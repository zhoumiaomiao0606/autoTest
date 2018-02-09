package com.yunche.loan.config.common;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.cache.AuthCache;
import com.yunche.loan.dao.mapper.EmployeeDOMapper;
import com.yunche.loan.dao.mapper.EmployeeRelaUserGroupDOMapper;
import com.yunche.loan.dao.mapper.UserGroupRelaAreaAuthDOMapper;
import com.yunche.loan.domain.dataObj.*;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yunche.loan.config.constant.AuthConst.*;
import static com.yunche.loan.config.constant.BaseConst.*;

/**
 * 自定义Realm，进行认证和授权操作
 *
 * @author liuzhe
 * @date 2018/2/6
 */
public class BizShiroRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizingRealm.class);

    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;
    @Autowired
    private UserGroupRelaAreaAuthDOMapper userGroupRelaAreaAuthDOMapper;
    @Autowired
    private AuthCache authCache;


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
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        String password = String.valueOf(usernamePasswordToken.getPassword());

        // 通过username从数据库中查找 User对象
        // 根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        EmployeeDO employeeDO = employeeDOMapper.getByUsername(username, VALID_STATUS);
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

        // 自己做密码校验   通过：给当前输入明文密码   不通过：给数据库加密密码
        boolean verify = MD5Utils.verify(password, employeeDO.getPassword());
        String verifyPassword = verify ? password : employeeDO.getPassword();

        // shiro认证
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                // 签名对象，认证通过后，可以在程序的任意位置获取当前放入的对象
                employeeDO,
                // 常规：数据库中查询出的密码 （验证原理：比对token中的密码[当前输入的明文密码]    But：此处，整合了自定义加密验证，故反向处理）
                verifyPassword,
                // 当前realm的类名
                this.getClass().getName());

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

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        Object user = principals.getPrimaryPrincipal();
        EmployeeDO employeeDO = new EmployeeDO();
        BeanUtils.copyProperties(user, employeeDO);

        // 获取用户的角色列表
        List<Long> userGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(employeeDO.getId());
        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return info;
        }

        // 获取角色列表所绑定的所有权限列表
        List<Long> hasBindAuthIdList = userGroupRelaAreaAuthDOMapper.getHasBindAuthIdListByUserGroupIdList(userGroupIdList);
        if (!CollectionUtils.isEmpty(hasBindAuthIdList)) {

            // get all Auth
            Map<Long, AuthDO> authMap = authCache.getAuth();
            if (!CollectionUtils.isEmpty(authMap)) {

                // get all auth entity
                Map<Long, MenuDO> menuMap = authCache.getMenu();
                Map<Long, PageDO> pageMap = authCache.getPage();
                Map<Long, OperationDO> operationMap = authCache.getOperation();

                hasBindAuthIdList.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(authId -> {

                            AuthDO authDO = authMap.get(authId);
                            if (null != authDO) {

                                Byte type = authDO.getType();

                                // menu
                                if (MENU.equals(type)) {
                                    MenuDO menuDO = menuMap.get(authDO.getSourceId());
                                    if (null != menuDO) {
                                        info.addStringPermission(menuDO.getUri());
                                    }
                                }
                                // page
                                else if (PAGE.equals(type)) {
                                    PageDO pageDO = pageMap.get(authDO.getSourceId());
                                    if (null != pageDO) {
                                        info.addStringPermission(pageDO.getUri());
                                    }
                                }
                                // operation
                                else if (OPERATION.equals(type)) {
                                    OperationDO operationDO = operationMap.get(authDO.getSourceId());
                                    if (null != operationDO) {
                                        info.addStringPermission(operationDO.getUri());
                                    }
                                }

                            }

                        });
            }
        }

        return info;
    }
}
