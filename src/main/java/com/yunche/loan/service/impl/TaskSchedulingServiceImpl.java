package com.yunche.loan.service.impl;

import com.yunche.loan.config.common.ProcessBind;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.TaskSchedulingService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    @Resource
    private TaskService taskService;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Resource
    private EmployeeDOMapper employeeDOMapper;

    @Resource
    private PartnerDOMapper partnerDOMapper;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private CarModelDOMapper carModelDOMapper;

    @Resource
    private CarDetailDOMapper carDetailDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private CarBrandDOMapper carBrandDOMapper;

    @Override
    public List<ScheduleTaskVO> scheduleTaskList() {
        List<ScheduleTaskVO> result = new ArrayList<ScheduleTaskVO>();
        List<Task> list = taskService.createTaskQuery().taskCandidateGroup("合伙人").list();//根据角色去查看可领取的任务
        for(Task task:list){
            LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(task.getProcessInstanceId());
            if(loanOrderDO!=null){
                //只有存在的单子,才会返回
                ScheduleTaskVO vo = new ScheduleTaskVO();
                vo.setTaskKey(task.getTaskDefinitionKey());
                vo.setCreateScheduleDate(castDate(task.getCreateTime()));
                vo.setOrderId(loanOrderDO.getId() == null?null:String.valueOf(loanOrderDO.getId()));
                LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
                if(loanBaseInfoDO != null){
                    PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(),new Byte("0"));
                    if(partnerDO != null){
                        vo.setPartner(partnerDO.getName());
                    }
                    EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(),new Byte("0"));

                    if(employeeDO != null){
                        vo.setSalesman(employeeDO.getName());
                    }
                    vo.setBank(loanBaseInfoDO.getBank());
                }

                LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
                if(loanCustomerDO != null){
                    vo.setName(loanCustomerDO.getName());
                    vo.setIdCard(loanCustomerDO.getIdCard());
                    vo.setMobile(loanCustomerDO.getMobile());
                }

                LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
                if(loanCarInfoDO != null){
                    vo.setCarType(loanCarInfoDO.getCarType() == null?null:loanCarInfoDO.getCarType().toString());
                    CarDetailDO carDetailDO  = carDetailDOMapper.selectByPrimaryKey(loanCarInfoDO.getCarDetailId(),new Byte("0"));
                    if(carDetailDO != null){
                        CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carDetailDO.getModelId(),new Byte("0"));
                        if(carModelDO != null){
                            CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carModelDO.getBrandId(),new Byte("0"));
                            if(carBrandDO != null){
                                vo.setCarName(carBrandDO.getName() + " " + carModelDO.getName() + " " + carDetailDO.getName());
                            }
                        }
                    }
                }
                LoanFinancialPlanDO loanFinancialPlanDO  = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                if(loanFinancialPlanDO != null){
                    vo.setDownPaymentRatio(loanFinancialPlanDO.getDownPaymentRatio() == null?null:loanFinancialPlanDO.getDownPaymentRatio().toString());
                    vo.setLoanTime(loanFinancialPlanDO.getLoanTime() == null?null:loanFinancialPlanDO.getLoanTime().toString());
                    vo.setLoanAmount(loanFinancialPlanDO.getLoanAmount() == null?null:loanFinancialPlanDO.getLoanAmount().toString());
                }

                for (LoanProcessEnum e : LoanProcessEnum.values()) {
                    if(e.getCode().equals(task.getTaskDefinitionKey())){
                        vo.setTaskStatusExplanation("待办"+e.getName());
                        break;
                    }
                }

                vo.setUsertaskCreditApplyVerifyStatus(null);
                vo.setUsertaskTelephoneVerifyStatus(null);
                vo.setUsertaskVisitVerifyStatus(null);
                vo.setUsertaskInfoSupplementStatus(null);
                result.add(vo);
            }


        }

        return result;
    }

    private <T>String bind(T t,ProcessBind e){
        if(t == null){
            return null;
        }
       return e.process(t);
    }

    private String castDate(Date value){
        if(value == null){
            return  null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(value);
    }
}
