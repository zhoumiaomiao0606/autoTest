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
       /* for(AppBusinessDetailReportVO appBusinessDetailReportVO:list){
            Long orderId = appBusinessDetailReportVO.getOrderId();
            if(orderId !=null){
                int num = zhonganInfoDOMapper.countAppBusDetail(orderId);
                if(num > 0){
                    appBusinessDetailReportVO.setTelephoneVerify("资料增补");
                }
            }
        }*/
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
    public ResultBean<List<AppBussinessRankReportVO>> businessRank(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppBussinessRankReportVO> list = zhonganInfoDOMapper.appBussinessRankReport(query);
        PageInfo<AppBussinessRankReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<AppNoMortgageCusReportVO>> noMortgageCus(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppNoMortgageCusReportVO> list = zhonganInfoDOMapper.appNoMortgageCusReport(query);
        PageInfo<AppNoMortgageCusReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean<List<AppMortgageAndDataOverdueReportVO>> mortgageAndDataOverdue(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppMortgageAndDataOverdueReportVO> list = zhonganInfoDOMapper.appMortgageAndDataOverdueReport1(query);
        List<AppMortgageAndDataOverdueReportVO> list1 = zhonganInfoDOMapper.appMortgageAndDataOverdueReport2(query);
        for(AppMortgageAndDataOverdueReportVO appMortgageAndDataOverdueReportVO : list){
            for(AppMortgageAndDataOverdueReportVO appMortgageAndDataOverdueReportVO1:list1){
                if(appMortgageAndDataOverdueReportVO.getId().equals(appMortgageAndDataOverdueReportVO1.getId())){
                    appMortgageAndDataOverdueReportVO.setOverdueSum(appMortgageAndDataOverdueReportVO1.getOverdueSum());
                    appMortgageAndDataOverdueReportVO.setOverdueAmountSum(appMortgageAndDataOverdueReportVO1.getOverdueAmountSum());
                }
            }
        }
        PageInfo<AppMortgageAndDataOverdueReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean<List<AppCardsTimeCheckReportVO>> cardsTimeCheck(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppCardsTimeCheckReportVO> list = zhonganInfoDOMapper.appCardsTimeCheckReport(query);
        PageInfo<AppCardsTimeCheckReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean<List<AppDataTimeCheckReportVO>> dataTimeCheck(AppBusDetailQuery query) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        query.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        query.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<AppDataTimeCheckReportVO> list = zhonganInfoDOMapper.appDataTimeCheckReport(query);
        PageInfo<AppDataTimeCheckReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();
        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    //1-时间，2-业务大区，3-合伙人，4-统计区间，5-车辆类型，6-贷款区间，7逾期区间，8垫款状态
    @Override
    public AppTableInfoVO getTableHead(String type) {
        AppTableInfoVO appTableInfoVO = new AppTableInfoVO();
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        //业务明细表
        if("1".equals(type)){
            list.add("姓名");
            list.add("车型");
            list.add("贷款本金");
            list.add("执行利率");
            list.add("按揭期限");
            list.add("分期本金");
            list.add("业务员");
            list1.add("1");
            list1.add("3");
        }else if("2".equals(type)){//垫款明细表
            list.add("姓名");
            list.add("执行利率");
            list.add("贷款本金");
            list.add("分期本金");
            list.add("垫款状态");
            list.add("垫款金额");
            list.add("申请时间");
            list1.add("1");
            list1.add("3");
            list1.add("8");
        }else if ("3".equals(type)){//业务量排行
            list.add("业务员");
            list.add("业务单数");
            list.add("贷款总额");
            list.add("单笔贷款额");
            list1.add("1");
            list1.add("3");
        }else if ("4".equals(type)){//未抵押客户
            list.add("客户");
            list.add("车型");
            list.add("业务员");
            list.add("贷款本金");
            list.add("垫款时间");
            list1.add("1");
            list1.add("3");
        }else if ("5".equals(type)){//抵押和资料超期数
            list.add("业务员");
            list.add("未抵押单数");
            list.add("未抵押贷款总额");
            list.add("资料超期单数");
            list.add("资料超期贷款总额");
            list1.add("1");
            list1.add("3");
        }else if ("6".equals(type)){//牌证时效考核
            list.add("业务员");
            list.add("客户");
            list.add("车型");
            list.add("车牌");
            list.add("垫款时间");
            list.add("上牌时间");
            list.add("间隔天数");
            list1.add("1");
            list1.add("3");
        }else if ("7".equals(type)){//资料时效考核
            list.add("业务员");
            list.add("客户");
            list.add("车型");
            list.add("垫款时间");
            list.add("合同资料寄出");
            list.add("间隔天数");
            list1.add("1");
            list1.add("3");
        }
        appTableInfoVO.setTableHead(list);
        appTableInfoVO.setTableScreen(list1);
        return appTableInfoVO;
    }
}
