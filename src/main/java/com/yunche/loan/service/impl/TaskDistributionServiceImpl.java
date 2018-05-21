package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.TaskDistributionDO;
import com.yunche.loan.mapper.TaskDistributionDOMapper;
import com.yunche.loan.service.TaskDistributionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;

@Service
@Transactional
public class TaskDistributionServiceImpl implements TaskDistributionService {

    @Resource
    private TaskDistributionDOMapper taskDistributionDOMapper;


    //领取
    @Override
    public void get(Long taskId, String taskkey) {
        if(taskId == null || StringUtils.isBlank(taskkey)){
            throw new BizException("必须传入任务id和任务key");
        }

        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectLastTaskDistributionGroupByTaskKey(taskId,taskkey);
        if(taskDistributionDO!=null){
            Byte status = taskDistributionDO.getStatus();
            if(status.toString().equals("2")){
                throw new BizException("该任务已被领取,正在执行中");
            }
        }
        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        TaskDistributionDO V = new TaskDistributionDO();
        V.setTaskId(taskId);
        V.setSendee(employeeDO.getId());
        V.setSendeeName(employeeDO.getName());
        V.setStatus(new Byte("2"));
        V.setTaskKey(taskkey);
        taskDistributionDOMapper.insertSelective(V);
    }

    //释放
    @Override
    public void release(Long taskId, String taskkey) {
        if(taskId == null || StringUtils.isBlank(taskkey)){
            throw new BizException("必须传入任务id和任务key");
        }

        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectLastTaskDistributionGroupByTaskKey(taskId,taskkey);

        if(taskDistributionDO==null) {
            throw new BizException("该任务无法被释放");
        }

        Byte status = taskDistributionDO.getStatus();
        if(!status.toString().equals("2")){
            throw new BizException("该任务状态无法被释放");
        }

        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        if(employeeDO.getId().longValue()!=taskDistributionDO.getSendee().longValue()){
            throw new BizException("该任务只能被领取人释放");
        }

        TaskDistributionDO V = new TaskDistributionDO();
        V.setTaskId(taskId);
        V.setSendee(employeeDO.getId());
        V.setSendeeName(employeeDO.getName());
        V.setStatus(new Byte("3"));
        V.setTaskKey(taskkey);
        V.setReleaseCreate(new Timestamp(new Date().getTime()));
        taskDistributionDOMapper.insertSelective(V);
    }

    //完成
    @Override
    public void finish(Long taskId, String taskkey) {
        if(taskId == null || StringUtils.isBlank(taskkey)){
            throw new BizException("必须传入任务id和任务key");
        }
        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectLastTaskDistributionGroupByTaskKey(taskId,taskkey);

        if(taskDistributionDO==null) {
            throw new BizException("该任务无法被完成");
        }

        Byte status = taskDistributionDO.getStatus();
        if(!status.toString().equals("2")){
            throw new BizException("该任务状态无法被完成");
        }

        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        if(employeeDO.getId().longValue()!=taskDistributionDO.getSendee().longValue()){
            throw new BizException("该任务只能被领取人完成");
        }

        TaskDistributionDO V = new TaskDistributionDO();
        V.setTaskId(taskId);
        V.setSendee(employeeDO.getId());
        V.setSendeeName(employeeDO.getName());
        V.setStatus(new Byte("1"));
        V.setTaskKey(taskkey);
        V.setReleaseCreate(new Timestamp(new Date().getTime()));
        taskDistributionDOMapper.insertSelective(V);
    }

}
