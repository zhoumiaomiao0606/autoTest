package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.PartnerQuery;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.viewObj.BaseVO;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.PartnerVO;
import com.yunche.loan.service.PartnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
@Transactional
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private PartnerDOMapper partnerDOMapper;
    @Autowired
    private DepartmentDOMapper departmentDOMapper;
    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private BizModelDOMapper bizModelDOMapper;
    @Autowired
    private BizModelRelaAreaPartnersDOMapper bizModelRelaAreaPartnersDOMapper;


    @Override
    public ResultBean<Long> create(PartnerParam partnerParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getName()), "团队名称不能为空");
        Preconditions.checkNotNull(partnerParam.getDepartmentId(), "对应负责部门不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderName()), "团队负责人不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderMobile()), "负责人手机不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getOpenBank()), "开户行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getAccountName()), "开户名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getBankAccount()), "银行账号不能为空");
        Preconditions.checkNotNull(partnerParam.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(partnerParam.getStatus()) || INVALID_STATUS.equals(partnerParam.getStatus()),
                "状态非法");

        // 创建实体，并返回ID
        Long id = insertAndGetId(partnerParam);

        // 绑定业务产品列表
        bindBizModel(id, partnerParam.getAreaId(), partnerParam.getBizModelIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(PartnerDO partnerDO) {
        Preconditions.checkNotNull(partnerDO.getId(), "id不能为空");

        partnerDO.setGmtModify(new Date());
        int count = partnerDOMapper.updateByPrimaryKeySelective(partnerDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        // 编辑绑定业务产品的限制区域
        updateRelaBizModelArea(partnerDO.getId(), partnerDO.getAreaId());

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    /**
     * 编辑绑定业务产品的限制区域
     *
     * @param id
     * @param areaId
     */
    private void updateRelaBizModelArea(Long id, Long areaId) {
        if (null == areaId) {
            return;
        }
        // check
        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDO.setGroupId(id);
        List<BizModelRelaAreaPartnersDO> bizModelRelaAreaPartnersDOS = bizModelRelaAreaPartnersDOMapper.listQuery(bizModelRelaAreaPartnersDO);
        if (CollectionUtils.isEmpty(bizModelRelaAreaPartnersDOS)) {
            return;
        }

        // update
        bizModelRelaAreaPartnersDOS.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    if (areaId.equals(e.getAreaId())) {
                        return;
                    } else {
                        // delete
                        int delCount = bizModelRelaAreaPartnersDOMapper.delete(e);
                        Preconditions.checkArgument(delCount > 0, "编辑业务区域失败");

                        // insert
                        e.setAreaId(areaId);
                        e.setGmtModify(new Date());
                        int insertCount = bizModelRelaAreaPartnersDOMapper.insert(e);
                        Preconditions.checkArgument(insertCount > 0, "编辑业务区域失败");
                    }

                });
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = partnerDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<PartnerVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(partnerDO, "id有误，数据不存在");

        PartnerVO partnerVO = new PartnerVO();
        BeanUtils.copyProperties(partnerDO, partnerVO);

        // fillMsg
        fillDepartment(partnerDO.getDepartmentId(), partnerVO);
        fillArea(partnerDO.getAreaId(), partnerVO);

        return ResultBean.ofSuccess(partnerVO);
    }

    @Override
    public ResultBean<List<PartnerVO>> query(PartnerQuery query) {
        int totalNum = partnerDOMapper.count(query);
        if (totalNum > 0) {
            List<PartnerDO> partnerDOS = partnerDOMapper.query(query);
            if (!CollectionUtils.isEmpty(partnerDOS)) {
                List<PartnerVO> partnerVOS = partnerDOS.stream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            PartnerVO partnerVO = new PartnerVO();
                            BeanUtils.copyProperties(e, partnerVO);

                            fillDepartment(e.getDepartmentId(), partnerVO);
                            fillArea(e.getAreaId(), partnerVO);

                            return partnerVO;
                        })
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(partnerVOS, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }


    /**
     * 填充部门信息
     *
     * @param departmentId
     * @param partnerVO
     */
    private void fillDepartment(Long departmentId, PartnerVO partnerVO) {
        if (null == departmentId) {
            return;
        }
        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(departmentId, VALID_STATUS);
        if (null != departmentDO) {
            BaseVO baseVO = new BaseVO();
            BeanUtils.copyProperties(departmentDO, baseVO);
            partnerVO.setDepartment(baseVO);
        }
    }

    /**
     * 填充区域(城市)信息
     *
     * @param areaId
     * @param partnerVO
     */
    private void fillArea(Long areaId, PartnerVO partnerVO) {
        if (null == areaId) {
            return;
        }
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        if (null != baseAreaDO) {
            BaseVO baseVO = new BaseVO();
            baseVO.setId(baseAreaDO.getAreaId());
            baseVO.setName(baseAreaDO.getAreaName());
            partnerVO.setArea(baseVO);
        }
    }

    @Override
    public ResultBean<List<BizModelVO>> listBizModel(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "合伙人ID不能为空");

        int totalNum = bizModelDOMapper.countListBizModelByPartnerId(query);
        if (totalNum > 0) {

            List<BizModelDO> bizModelDOS = bizModelDOMapper.listBizModelByPartnerId(query);
            if (!CollectionUtils.isEmpty(bizModelDOS)) {

                List<BizModelVO> bizModelVOList = bizModelDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(bizModelDO -> {

                            BizModelVO bizModelVO = new BizModelVO();
                            BeanUtils.copyProperties(bizModelDO, bizModelVO);

                            return bizModelVO;
                        })
                        .sorted(Comparator.comparing(BizModelVO::getGmtModify))
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(bizModelVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<Void> bindBizModel(Long id, String bizModelIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(bizModelIds), "业务产品ID不能为空");

        // convert
        List<Long> bizModelIdList = Arrays.asList(bizModelIds.split(",")).stream()
                .map(e -> {
                    return Long.valueOf(e);
                })
                .distinct()
                .collect(Collectors.toList());

        // getAreaId
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        // bind
        bindBizModel(id, partnerDO.getAreaId(), bizModelIdList);

        return ResultBean.ofSuccess(null, "关联成功");
    }

    @Override
    public ResultBean<Void> unbindBizModel(Long id, String bizModelIds) {
        Preconditions.checkNotNull(id, "合伙人ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(bizModelIds), "业务产品ID不能为空");

        // getAreaId
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(partnerDO, "id有误,合伙人不存!");
        Preconditions.checkNotNull(partnerDO.getAreaId(), "合伙人业务区域为空，请先设置业务区域");

        Arrays.asList(bizModelIds.split(",")).stream()
                .distinct()
                .forEach(bizModelId -> {
                    BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                    bizModelRelaAreaPartnersDO.setGroupId(id);
                    bizModelRelaAreaPartnersDO.setAreaId(partnerDO.getAreaId());
                    bizModelRelaAreaPartnersDO.setBizId(Long.valueOf(bizModelId));
                    int count = bizModelRelaAreaPartnersDOMapper.delete(bizModelRelaAreaPartnersDO);
                    Preconditions.checkArgument(count > 0, "取消关联失败");
                });

        return ResultBean.ofSuccess(null, "取消关联成功");
    }

    /**
     * 创建实体，并返回主键ID
     *
     * @param partnerParam
     * @return
     */
    private Long insertAndGetId(PartnerParam partnerParam) {
        List<String> nameList = partnerDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(partnerParam.getName()), "团队名称已存在");

        PartnerDO partnerDO = new PartnerDO();
        BeanUtils.copyProperties(partnerParam, partnerDO);
        partnerDO.setGmtCreate(new Date());
        partnerDO.setGmtModify(new Date());

        int count = partnerDOMapper.insertSelective(partnerDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return partnerDO.getId();
    }

    /**
     * 绑定业务产品列表
     *
     * @param partnerId      合伙人ID
     * @param areaId         业务限制区域ID
     * @param bizModelIdList 业务产品ID列表
     */
    private void bindBizModel(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        if (CollectionUtils.isEmpty(bizModelIdList)) {
            return;
        }
        Preconditions.checkNotNull(areaId, "业务限制区域ID不能为空");

        // 去重
        distinctBizModelIdList(partnerId, areaId, bizModelIdList);

        // 执行绑定
        execBindBizModel(partnerId, areaId, bizModelIdList);
    }

    /**
     * 业务产品ID去重
     *
     * @param partnerId
     * @param areaId
     * @param bizModelIdList
     */
    private void distinctBizModelIdList(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
        bizModelRelaAreaPartnersDO.setGroupId(partnerId);
        bizModelRelaAreaPartnersDO.setAreaId(areaId);
        List<BizModelRelaAreaPartnersDO> existBizModelRelaAreaPartnersDOS = bizModelRelaAreaPartnersDOMapper.listQuery(bizModelRelaAreaPartnersDO);
        if (!CollectionUtils.isEmpty(existBizModelRelaAreaPartnersDOS)) {
            List<Long> existBizModelIdList = existBizModelRelaAreaPartnersDOS.parallelStream()
                    .filter(e -> null != e && null != e.getBizId())
                    .map(e -> {
                        return e.getBizId();
                    })
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> repeatTmp = Lists.newArrayList();
            bizModelIdList.parallelStream()
                    .forEach(e -> {
                        if (existBizModelIdList.contains(e)) {
                            repeatTmp.add(e);
                        }
                    });

            bizModelIdList.removeAll(repeatTmp);
        }
    }

    /**
     * 执行绑定
     *
     * @param partnerId
     * @param areaId
     * @param bizModelIdList
     */
    private void execBindBizModel(Long partnerId, Long areaId, List<Long> bizModelIdList) {
        bizModelIdList.parallelStream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(bizModelId -> {

                    BizModelRelaAreaPartnersDO bizModelRelaAreaPartnersDO = new BizModelRelaAreaPartnersDO();
                    bizModelRelaAreaPartnersDO.setGroupId(partnerId);
                    bizModelRelaAreaPartnersDO.setBizId(bizModelId);
                    bizModelRelaAreaPartnersDO.setAreaId(areaId);
                    bizModelRelaAreaPartnersDO.setGmtCreate(new Date());
                    bizModelRelaAreaPartnersDO.setGmtModify(new Date());
                    int count = bizModelRelaAreaPartnersDOMapper.insert(bizModelRelaAreaPartnersDO);
                    Preconditions.checkArgument(count > 0, "关联业务产品失败");
                });
    }
}
