package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.PartnerDOMapper;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.PartnerQuery;
import com.yunche.loan.domain.dataObj.DepartmentDO;
import com.yunche.loan.domain.dataObj.PartnerDO;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.DepartmentVO;
import com.yunche.loan.domain.viewObj.PartnerVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.PartnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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


    @Override
    public ResultBean<Long> create(PartnerParam partnerParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getName()), "团队名称不能为空");
        Preconditions.checkNotNull(partnerParam.getDepartmentId(), "对应负责部门不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderName()), "团队负责人不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(partnerParam.getLeaderMobile()), "负责人手机不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(partnerParam);

        // 绑定业务产品列表
        bindBizModel(id, partnerParam.getBizModelIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(PartnerDO partnerDO) {
        Preconditions.checkNotNull(partnerDO.getId(), "id不能为空");

        partnerDO.setGmtModify(new Date());
        int count = partnerDOMapper.updateByPrimaryKeySelective(partnerDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
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



        return ResultBean.ofSuccess(partnerVO);
    }

    @Override
    public ResultBean<List<UserGroupVO>> query(PartnerQuery query) {
        return null;
    }

    @Override
    public ResultBean<List<AuthVO>> listBizModel(BaseQuery query) {
        return null;
    }

    @Override
    public ResultBean<Void> deleteRelaBizModels(Long id, String bizModelIds) {
        return null;
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
        partnerDO.setStatus(VALID_STATUS);
        partnerDO.setGmtCreate(new Date());
        partnerDO.setGmtModify(new Date());

        int count = partnerDOMapper.insertSelective(partnerDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return partnerDO.getId();
    }

    /**
     * TODO 绑定业务产品列表
     *
     * @param id             合伙人ID
     * @param bizModelIdList 业务产品ID列表
     */
    private void bindBizModel(Long id, List<Long> bizModelIdList) {


    }
}
