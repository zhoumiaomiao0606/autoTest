package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.vo.LoginUserExtInfo;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.mapper.PartnerRelaEmployeeDOMapper;
import com.yunche.loan.service.YuncheCloudExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author liuzhe
 * @date 2018/12/4
 */
@Service
public class YuncheCloudExtServiceImpl implements YuncheCloudExtService {

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;


    @Override
    public LoginUserExtInfo getLoginUserExtInfoByLoginUserId(Long loginUserId) {
        Assert.notNull(loginUserId, "loginUserId不能为空");

        LoginUserExtInfo loginUserExtInfo = new LoginUserExtInfo();

        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loginUserId, null);
        loginUserExtInfo.setLoginUserId(employeeDO.getId());
        loginUserExtInfo.setLoginUserName(employeeDO.getName());

        PartnerDO partnerDO = partnerRelaEmployeeDOMapper.getPartnerByEmployeeId(loginUserId);
        loginUserExtInfo.setPartnerId(partnerDO.getId());
        loginUserExtInfo.setPartnerName(partnerDO.getName());

        return loginUserExtInfo;
    }
}
