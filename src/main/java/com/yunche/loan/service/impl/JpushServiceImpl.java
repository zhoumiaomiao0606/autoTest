package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.Jpush;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.FlowOperationMsgDO;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.mapper.FlowOperationMsgDOMapper;
import com.yunche.loan.service.JpushService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@Service
@Transactional
public class JpushServiceImpl implements JpushService {

    @Resource
    private FlowOperationMsgDOMapper flowOperationMsgDOMapper;

    @Resource
    private EmployeeDOMapper employeeDOMapper;


    @Override
    public void push(FlowOperationMsgDO DO) {
        flowOperationMsgDOMapper.insertSelective(DO);
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(DO.getEmployeeId(), VALID_STATUS);
        if (employeeDO != null) {
            if (StringUtils.isNotBlank(employeeDO.getMachineId())) {
                Jpush.sendToRegistrationId(employeeDO.getMachineId(), DO.getPrompt(), DO.getProcessKey());
            }
        }
    }

    @Override
    public ResultBean list(Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize, true);
        List<FlowOperationMsgDO> list = flowOperationMsgDOMapper.selectByEmployeeId(SessionUtils.getLoginUser().getId());
        // 取分页信息
        PageInfo<FlowOperationMsgDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public void read(Long id) {
        FlowOperationMsgDO DO = new FlowOperationMsgDO();
        DO.setId(id);
        DO.setReadStatus(new Byte("1"));
        flowOperationMsgDOMapper.updateByPrimaryKeySelective(DO);
    }
}
