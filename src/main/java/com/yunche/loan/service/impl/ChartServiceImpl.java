package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
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

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-06 17:12
 * @description: 报表统一实现类
 **/
@Service
public class ChartServiceImpl implements ChartService
{
    @Autowired
    private ChartDOMapper chartDOMapper;

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
