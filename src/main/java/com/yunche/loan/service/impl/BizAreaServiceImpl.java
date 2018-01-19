package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.AreaConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.BaseAreaDOMapper;
import com.yunche.loan.dao.mapper.BizAreaDOMapper;
import com.yunche.loan.dao.mapper.BizAreaRelaAreaDOMapper;
import com.yunche.loan.dao.mapper.EmployeeDOMapper;
import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.valueObj.BizAreaVO;
import com.yunche.loan.service.BizAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Service
@Transactional(rollbackFor = BizException.class)
public class BizAreaServiceImpl implements BizAreaService {

    @Autowired
    private BizAreaDOMapper bizAreaDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private EmployeeDOMapper employeeDOMapper;
    @Autowired
    private BizAreaRelaAreaDOMapper bizAreaRelaAreaDOMapper;


    @Override
    public ResultBean<Long> create(BizAreaDO bizAreaDO) {
        Preconditions.checkArgument(null != bizAreaDO && StringUtils.isNotBlank(bizAreaDO.getName()), "名称不能为空");
        Preconditions.checkNotNull(null != bizAreaDO.getEmployeeId(), "部门负责人不能为空");

        // 名称已存在校验
        List<String> nameList = bizAreaDOMapper.getAllName();
        Preconditions.checkArgument(!nameList.contains(bizAreaDO.getName().trim()), "名称已存在");

        bizAreaDO.setGmtCreate(new Date());
        bizAreaDO.setGmtModify(new Date());
        int count = bizAreaDOMapper.insertSelective(bizAreaDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return ResultBean.ofSuccess(bizAreaDO.getId());
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = bizAreaDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<Void> update(BizAreaDO bizAreaDO) {
        Preconditions.checkArgument(null != bizAreaDO && null != bizAreaDO.getId(), "id不能为空");

        bizAreaDO.setGmtModify(new Date());
        int count = bizAreaDOMapper.updateByPrimaryKeySelective(bizAreaDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<BizAreaVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        BizAreaDO bizAreaDO = bizAreaDOMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(bizAreaDO, "id有误，数据不存在.");

        BizAreaVO bizAreaVO = new BizAreaVO();
        BeanUtils.copyProperties(bizAreaDO, bizAreaVO);

        // 补充大区负责人
        fillHead(bizAreaDO.getEmployeeId(), bizAreaVO);

        // 补充下级区域列表
        fillChildBizAreas(bizAreaDO.getId(), bizAreaVO);

        // 补充当前区域覆盖城市
        fillAreas(bizAreaDO.getAreaId(), bizAreaVO);

        return ResultBean.ofSuccess(bizAreaVO);
    }


    /**
     * 补充大区负责人
     *
     * @param employeeId
     * @param bizAreaVO
     */
    private void fillHead(Long employeeId, BizAreaVO bizAreaVO) {
        if (null == employeeId) {
            return;
        }
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(employeeId);
        if (null != employeeDO) {
            BizAreaVO.Head head = new BizAreaVO.Head();
            head.setEmployeeId(employeeDO.getId());
            head.setEmployeeName(employeeDO.getName());
            bizAreaVO.setHead(head);
        }
    }

    /**
     * 补充下级区域列表
     *
     * @param parentId
     * @param bizAreaVO
     */
    private void fillChildBizAreas(Long parentId, BizAreaVO bizAreaVO) {
        if (null == parentId) {
            return;
        }

        BizAreaQuery query = new BizAreaQuery();
        query.setParentId(parentId);
        bizAreaDOMapper.query(query);

    }

    /**
     * 补充当前区域覆盖城市
     *
     * @param areaId
     * @param bizAreaVO
     */
    private void fillAreas(Long areaId, BizAreaVO bizAreaVO) {


    }


    @Override
    public ResultBean<List<BizAreaVO>> query(BizAreaQuery query) {
        int totalNum = bizAreaDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaDOS), "无符合条件的数据");

        List<BizAreaVO> bizAreaVOS = bizAreaDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    BizAreaVO bizAreaVO = new BizAreaVO();
                    BeanUtils.copyProperties(e, bizAreaVO);
                    return bizAreaVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(bizAreaVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AreaVO>> listCity(BizAreaQuery query) {
        // 业务区域ID不能为空
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        int totalNum = bizAreaRelaAreaDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<BizAreaRelaAreaDO> bizAreaRelaAreaDOS = bizAreaRelaAreaDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(bizAreaRelaAreaDOS), "无符合条件的数据");

//        List<AreaVO> bizAreaVOS = bizAreaRelaAreaDOS.stream()
//                .filter(e -> null != e && null != e.getAreaId())
//                .map(e -> {
//
//                    AreaVO areaVO = new AreaVO();
//
//                    BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e.getAreaId());
//                    if (null != baseAreaDO) {
//                        Byte level = baseAreaDO.getLevel();
//                        // 市
//                        if (LEVEL_CITY.equals(level)) {
//
//                            AreaVO.City city = new AreaVO.City();
//                            city.setId(baseAreaDO.getAreaId());
//                            city.setName(baseAreaDO.getAreaName());
//                            city.setLevel(LEVEL_CITY);
//                            return city;
//
//                            // 省
//                            BaseAreaDO parentBaseAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId());
//                            if (null != parentBaseAreaDO) {
//
//                            }
//
//
//                        }
//                    }
//
//                    Long areaId = bizAreaDO.getAreaId();
//                    String name = bizAreaDO.getName();
//
//
//                    BeanUtils.copyProperties(e, bizAreaVO);
//                    return bizAreaVO;
//                })
//                .collect(Collectors.toList());
//
//        return ResultBean.ofSuccess(bizAreaVOS, totalNum, query.getPageIndex(), query.getPageSize());


        return null;
    }

//    @Override
//    public ResultBean<List<BizAreaVO>> listByParentId(Long parentId) {
//        Preconditions.checkNotNull(parentId, "parentId不能为空");
//
//        List<BizAreaDO> bizAreaDOS = bizAreaDOMapper.listByParentId(parentId);
//        if (!CollectionUtils.isEmpty(bizAreaDOS)) {
//            List<BizAreaVO> bizAreaVOS = bizAreaDOS.stream()
//                    .filter(Objects::nonNull)
//                    .map(e -> {
//                        BizAreaVO bizAreaVO = new BizAreaVO();
//                        BeanUtils.copyProperties(e, bizAreaVO);
//                        return bizAreaVO;
//                    })
//                    .collect(Collectors.toList());
//            return ResultBean.ofSuccess(bizAreaVOS);
//        }
//
//        return null;
//    }
}
