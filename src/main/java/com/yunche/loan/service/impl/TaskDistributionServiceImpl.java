package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.TaskDistributionDO;
import com.yunche.loan.domain.vo.TaskDisVO;
import com.yunche.loan.mapper.TaskDistributionDOMapper;
import com.yunche.loan.service.TaskDistributionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskDistributionServiceImpl implements TaskDistributionService {

    @Resource
    private TaskDistributionDOMapper taskDistributionDOMapper;


    //领取
    @Override
    public void get(Long taskId, String taskKey) {
        if(taskId == null || StringUtils.isBlank(taskKey)){
            throw new BizException("必须传入任务id和任务key");
        }
        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(taskId,taskKey);

        if(taskDistributionDO!=null){
            Byte status = taskDistributionDO.getStatus();
            if(status.toString().equals("2")){
                throw new BizException("该任务已被领取,正在执行中");
            }else if(status.toString().equals("1")) {
                throw new BizException("该任务已被完成");
            }else{
                throw new BizException("该任务状态异常");
            }
        }
        EmployeeDO employeeDO = SessionUtils.getLoginUser();
        TaskDistributionDO V = new TaskDistributionDO();
        V.setTaskId(taskId);
        V.setTaskKey(taskKey);
        V.setSendee(employeeDO.getId());
        V.setSendeeName(employeeDO.getName());
        V.setStatus(new Byte("2"));
        V.setGetCreate(new Timestamp(new Date().getTime()));
        taskDistributionDOMapper.insertSelective(V);
    }

    //释放
    @Override
    public void release(Long taskId, String taskKey) {
        if(taskId == null || StringUtils.isBlank(taskKey)){
            throw new BizException("必须传入任务id和任务key");
        }

        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(taskId,taskKey);


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

        taskDistributionDOMapper.deleteByPrimaryKey(taskId,taskKey);
    }

    //打回完成
    @Override
    public void rejectFinish(Long taskId,Long orderId,List<String> taskKeys) {
        //遍历可能性
        for(String taskKey:taskKeys){
            TaskDistributionDO taskDistributionDOByTaskId = taskDistributionDOMapper.selectByPrimaryKey(taskId,taskKey);
            if(taskDistributionDOByTaskId!=null){
                TaskDistributionDO V = new TaskDistributionDO();
                V.setTaskKey(taskDistributionDOByTaskId.getTaskKey());
                V.setTaskId(taskDistributionDOByTaskId.getTaskId());
                V.setStatus(new Byte("2"));
                taskDistributionDOMapper.updateByPrimaryKeySelective(V);
            }


            TaskDistributionDO taskDistributionDOByOrderId = taskDistributionDOMapper.selectByPrimaryKey(orderId,taskKey);
            if(taskDistributionDOByOrderId!=null){
                //open
                TaskDistributionDO V = new TaskDistributionDO();
                V.setTaskKey(taskDistributionDOByOrderId.getTaskKey());
                V.setTaskId(taskDistributionDOByOrderId.getTaskId());
                V.setStatus(new Byte("2"));
                taskDistributionDOMapper.updateByPrimaryKeySelective(V);
            }
        }
    }

    //完成
    @Override
    public void finish(Long taskId, String taskKey) {
        if(taskId == null || StringUtils.isBlank(taskKey)){
            throw new BizException("必须传入任务id和任务key");
        }
        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(taskId,taskKey);

        if(taskDistributionDO==null) {
            throw new BizException("该任务状态无法被完成");
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
        V.setTaskKey(taskKey);
        V.setSendee(employeeDO.getId());
        V.setSendeeName(employeeDO.getName());
        V.setStatus(new Byte("1"));
        V.setFinishCreate(new Timestamp(new Date().getTime()));
        taskDistributionDOMapper.updateByPrimaryKeySelective(V);
    }

    @Override
    public TaskDisVO query(Long taskId, String taskKey) {
        if (taskId == null || StringUtils.isBlank(taskKey)) {
            throw new BizException("必须传入任务id和任务key");
        }
        TaskDisVO taskDisVO = new TaskDisVO();

        TaskDistributionDO taskDistributionDO = taskDistributionDOMapper.selectByPrimaryKey(taskId,taskKey);
        if (taskDistributionDO == null) {
            taskDisVO.setStatus("1");
            return taskDisVO;
        }else {
            String status = taskDistributionDO.getStatus().toString();
            long rId = taskDistributionDO.getSendee().longValue();//领取人id
            long nId = SessionUtils.getLoginUser().getId().longValue();//当前登陆用户id
            if ("2".equals(status)) {
                if (rId == nId) {
                    taskDisVO.setStatus("2");                    //自己领取
                } else {
                    taskDisVO.setStatus("3");                    //别人领取
                }
            } else if ("1".equals(status)){
                taskDisVO.setStatus("4");
            }else {
                throw new BizException("该任务状态异常");
            }
            taskDisVO.setSendee(taskDistributionDO.getSendee() == null?null:taskDistributionDO.getSendee().toString());
            taskDisVO.setSendeeName(taskDistributionDO.getSendeeName());
            return taskDisVO;
        }
    }
}
