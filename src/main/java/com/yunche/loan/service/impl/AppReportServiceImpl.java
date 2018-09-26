package com.yunche.loan.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.query.AppBusDetailQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.mapper.ZhonganInfoDOMapper;
import com.yunche.loan.service.AppReportService;
import com.yunche.loan.service.EmployeeService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;


@Transactional
@Service
public class AppReportServiceImpl implements AppReportService {
    @Autowired
    private ZhonganInfoDOMapper zhonganInfoDOMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskSchedulingDOMapper taskSchedulingDOMapper;


    @Override
    public ResultBean<List<AppBusinessDetailReportVO>> businessDetail(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppBusinessDetailReportVO> list = zhonganInfoDOMapper.appBusinessDetailReport(query);
        PageInfo<AppBusinessDetailReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        for(AppBusinessDetailReportVO appBusinessDetailReportVO:list){
            Long orderId = appBusinessDetailReportVO.getOrderId();
            if(orderId !=null){
                int num = zhonganInfoDOMapper.countAppBusDetail(orderId);
                if(num > 0){
                    appBusinessDetailReportVO.setTelephoneVerify("资料增补");
                }
            }
        }
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<AppMakeMoneyDetailReportVO>> makeMoneyDetail(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppMakeMoneyDetailReportVO> list = zhonganInfoDOMapper.appMakeMoneyDetailReport(query);
        PageInfo<AppMakeMoneyDetailReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public AppTableInfoVO getTableHead(String type) {
        AppTableInfoVO appTableInfoVO = new AppTableInfoVO();
        List<String> list = new ArrayList<>();
        if("1".equals(type)){
            list.add("姓名");
            list.add("车型");
            list.add("执行利率");
            list.add("贷款本金");
            list.add("分期本金");
            list.add("电审状态");
            list.add("垫款状态");
            list.add("申请时间");
        }else if("2".equals(type)){
            list.add("姓名");
            list.add("执行利率");
            list.add("贷款本金");
            list.add("分期本金");
            list.add("垫款状态");
            list.add("垫款金额");
            list.add("申请时间");
        }
        appTableInfoVO.setTableHead(list);
        return appTableInfoVO;
    }
}
