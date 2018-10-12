package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BizAreaDO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.BizAreaDOMapper;
import com.yunche.loan.mapper.ChartDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.ChartService;
import com.yunche.loan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:12
 * @description: 报表统一实现类
 **/
@Service
@Transactional
public class ChartServiceImpl implements ChartService

{
    @Autowired
    private ChartDOMapper chartDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private BizAreaDOMapper bizAreaDOMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Override
    public ResultBean getSocialCreditChart(SocialCreditChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectSocialCreditChartVO(param);
        // 取分页信息
        PageInfo<SocialCreditChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public String expertSocialCreditQueryForChart(SocialCreditChartParam param) {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(param.getPageIndex(),1000000, true);
        List list = chartDOMapper.selectSocialCreditChartVO(param);

        ArrayList<String> header = Lists.newArrayList("大区","业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("SocialCredit",list,header,SocialCreditChartVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public ResultBean getBankCreditChart(BankCreditChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectBankCreditChartVO(param);
        // 取分页信息
        PageInfo<BankCreditChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }
    @Override
    public ResultBean getFinancialDepartmentRemitDetailChart(FinancialDepartmentRemitDetailChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectFinancialDepartmentRemitDetailChartVO(param);
        // 取分页信息
        PageInfo<FinancialDepartmentRemitDetailChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getMortgageOverdueChart(MortgageOverdueParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }


        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectMortgageOverdueChartVO(param);
        // 取分页信息
        PageInfo<MortgageOverdueChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getMaterialReviewChart(MaterialReviewParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectMaterialReviewChartVO(param);
        // 取分页信息
        PageInfo<MaterialReviewChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getAwaitRemitDetailChart(AwaitRemitDetailChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectAwaitRemitDetailChartVO(param);
        // 取分页信息
        PageInfo<AwaitRemitDetailChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean getCompanyRemitDetailChart(CompanyRemitDetailChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = chartDOMapper.selectCompanyRemitDetailChartVO(param);
        // 取分页信息
        PageInfo<CompanyRemitDetailChartVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(pageInfo);
    }

    @Override
    public ResultBean financialDepartmentRemitDetailChartShortcutStatistics(FinancialDepartmentRemitDetailChartParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        List<FinancialDepartmentRemitDetailChartVO> list = chartDOMapper.selectFinancialDepartmentRemitDetailChartVO(param);
        //计算统计数据====判空
        ShortcutStatisticsVO shortcutStatisticsVO =new ShortcutStatisticsVO();
        if(list !=null && list.size()>0 )
        {
            Optional<BigDecimal> totalLoanAcount = list.stream()
                    .filter(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalRemitAmount = list.stream()
                    .filter(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getRemit_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalBankPeriodPrincipal = list.stream()
                    .filter(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getBank_period_principal())
                    .reduce((x, y) -> x.add(y));

            shortcutStatisticsVO.setTotalBankPeriodPrincipal(totalBankPeriodPrincipal.get());
            shortcutStatisticsVO.setTotalLoanAcount(totalLoanAcount.get());
            shortcutStatisticsVO.setTotalRemitAmount(totalRemitAmount.get());
        }
        return ResultBean.ofSuccess(shortcutStatisticsVO);
    }

    @Override
    public ResultBean mortgageOverdueQueryForChartShortcutStatistics(MortgageOverdueParam param)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        List<MortgageOverdueChartVO> list = chartDOMapper.selectMortgageOverdueChartVO(param);
        //计算统计数据
        ShortcutStatisticsVO shortcutStatisticsVO =new ShortcutStatisticsVO();
        //计算统计数据====判空
        if(list !=null && list.size()>0 )
        {
            Optional<BigDecimal> totalLoanAcount = list.stream()
                    .filter(f -> f.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalBankPeriodPrincipal = list.stream()
                    .filter(f -> f.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getBank_period_principal())
                    .reduce((x, y) -> x.add(y));
            shortcutStatisticsVO.setTotalBankPeriodPrincipal(totalBankPeriodPrincipal.get());
            shortcutStatisticsVO.setTotalLoanAcount(totalLoanAcount.get());

        }
        return ResultBean.ofSuccess(shortcutStatisticsVO);
    }

    @Override
    public ResultBean awaitRemitDetailChartShortcutStatistics(AwaitRemitDetailChartParam param)
    {
        List<AwaitRemitDetailChartVO> list = chartDOMapper.selectAwaitRemitDetailChartVO(param);
        //计算统计数据
        ShortcutStatisticsVO shortcutStatisticsVO =new ShortcutStatisticsVO();
        //计算统计数据====判空
        if(list !=null && list.size()>0 )
        {
            Optional<BigDecimal> totalLoanAcount = list.stream()
                    .filter(f -> f.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getLoan_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalRemitAmount = list.stream()
                    .filter(f -> f.getLoan_amount()!=null)
                    .map(f -> f.getRemit_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalBankPeriodPrincipal = list.stream()
                    .filter(f -> f.getLoan_amount()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getBank_period_principal())
                    .reduce((x, y) -> x.add(y));
            shortcutStatisticsVO.setTotalBankPeriodPrincipal(totalBankPeriodPrincipal.get());
            shortcutStatisticsVO.setTotalLoanAcount(totalLoanAcount.get());
            shortcutStatisticsVO.setTotalRemitAmount(totalRemitAmount.get());
        }
        return ResultBean.ofSuccess(shortcutStatisticsVO);
    }

    @Override
    public ResultBean companyRemitDetailChartShortcutStatistics(CompanyRemitDetailChartParam param)
    {

        List<CompanyRemitDetailChartVO> list = chartDOMapper.selectCompanyRemitDetailChartVO(param);
        //计算统计数据
        ShortcutStatisticsVO shortcutStatisticsVO =new ShortcutStatisticsVO();
        //计算统计数据====判空
        if(list !=null && list.size()>0 )
        {
            Optional<BigDecimal> totalRemitAmount = list.stream()
                    .filter(f -> f.getBank_period_principal()!=null)
                    .map(f -> f.getRemit_amount())
                    .reduce((x, y) -> x.add(y));

            Optional<BigDecimal> totalBankPeriodPrincipal = list.stream()
                    .filter(f -> f.getBank_period_principal()!=null)
                    .map(financialDepartmentRemitDetailChartVO -> financialDepartmentRemitDetailChartVO.getBank_period_principal())
                    .reduce((x, y) -> x.add(y));
            shortcutStatisticsVO.setTotalBankPeriodPrincipal(totalBankPeriodPrincipal.get());

            shortcutStatisticsVO.setTotalRemitAmount(totalRemitAmount.get());
        }
        return ResultBean.ofSuccess(shortcutStatisticsVO);
    }


    public List<Long> getSelfAndChildBiz_area(Long parentId)
    {
        List<BizAreaDO> bizAreaDOs = bizAreaDOMapper.getByParentId(parentId, VALID_STATUS);
        //递归查询所有的子区域--用缓存优化
        List<Long> longList = bizAreaDOs.stream()
                .map(bizAreaDO -> bizAreaDO.getId())
                .collect(Collectors.toList());
        return longList;

    }

}
