package com.yunche.loan.service.impl;

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
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class JpushServiceImpl implements JpushService {

    @Resource
    private FlowOperationMsgDOMapper flowOperationMsgDOMapper;

    @Resource
    private EmployeeDOMapper employeeDOMapper;

    @Override
    public void push(Long employeeId,Long orderId,String title,String prompt,String msg,String processKey,Byte type) {
        FlowOperationMsgDO DO = new FlowOperationMsgDO();
        DO.setEmployeeId(employeeId);
        DO.setOrderId(orderId);
        DO.setTitle(title);
        DO.setPrompt(prompt);
        DO.setMsg(msg);
        DO.setSender(SessionUtils.getLoginUser().getName());
        DO.setProcessKey(processKey);
        DO.setSendDate(new Timestamp(new Date().getTime()));
        DO.setReadStatus(new Byte("0"));
        DO.setType(type);
        flowOperationMsgDOMapper.insertSelective(DO);
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(employeeId,new Byte("0"));
        if(employeeDO!=null){
            if(!StringUtils.isBlank(employeeDO.getMachineId())){
                Jpush.sendToRegistrationId(employeeDO.getMachineId(),title,processKey);
            }
        }
    }

    @Override
    public ResultBean list(Integer pageIndex, Integer pageSize) {
        List<FlowOperationMsgDO> list =  flowOperationMsgDOMapper.selectByEmployeeId(SessionUtils.getLoginUser().getId());
        // 取分页信息
        PageInfo<FlowOperationMsgDO> pageInfo = new PageInfo<FlowOperationMsgDO>(list);
        return ResultBean.ofSuccess(list,new Long(pageInfo.getTotal()).intValue(),pageInfo.getPageNum(),pageInfo.getPageSize());
    }

    @Override
    public void read(Long id) {
        FlowOperationMsgDO DO = new FlowOperationMsgDO();
        DO.setId(id);
        DO.setReadStatus(new Byte("1"));
        flowOperationMsgDOMapper.updateByPrimaryKeySelective(DO);
    }
}
