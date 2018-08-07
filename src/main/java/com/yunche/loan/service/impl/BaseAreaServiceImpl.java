package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.query.BaseAreaQuery;
import com.yunche.loan.domain.vo.BaseAreaVO;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.mapper.BaseAreaDOMapper;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.PartnerRelaAreaDOMapper;
import com.yunche.loan.service.BaseAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;
import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
@Transactional
public class BaseAreaServiceImpl implements BaseAreaService {

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;
    @Autowired
    private AreaCache areaCache;

    @Autowired
    private PartnerRelaAreaDOMapper partnerRelaAreaDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


    @Override
    public ResultBean<BaseAreaVO> getById(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
        Preconditions.checkNotNull(baseAreaDO, "areaId有误，数据不存在.");

        BaseAreaVO baseAreaVO = new BaseAreaVO();
        BeanUtils.copyProperties(baseAreaDO, baseAreaVO);

        return ResultBean.ofSuccess(baseAreaVO);
    }

    @Override
    public ResultBean<List<BaseAreaVO>> getByIdList(List<Long> areaIdList) {
        Preconditions.checkNotNull(areaIdList, "areaIdList不能为空");

        List<BaseAreaDO> baseAreaDOList = baseAreaDOMapper.selectByIdList(areaIdList, null);
        Preconditions.checkNotNull(baseAreaDOList, "areaId有误，数据不存在.");

        List<BaseAreaVO> baseAreaVOList = Lists.newArrayList();
        for (BaseAreaDO baseAreaDO : baseAreaDOList) {
            BaseAreaVO baseAreaVO = new BaseAreaVO();
            BeanUtils.copyProperties(baseAreaDO, baseAreaVO);
            baseAreaVOList.add(baseAreaVO);
        }

        return ResultBean.ofSuccess(baseAreaVOList);
    }

    @Override
    public ResultBean<Long> create(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");
        Preconditions.checkNotNull(baseAreaDO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(baseAreaDO.getStatus()) || INVALID_STATUS.equals(baseAreaDO.getStatus()),
                "状态非法");

        baseAreaDO.setGmtCreate(new Date());
        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.insertSelective(baseAreaDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(baseAreaDO.getAreaId(), "创建成功");
    }

    @Override
    public ResultBean<Void> update(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        baseAreaDO.setGmtModify(new Date());
        int count = baseAreaDOMapper.updateByPrimaryKeySelective(baseAreaDO);
        Preconditions.checkArgument(count > 0, "更新失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        int count = baseAreaDOMapper.deleteByPrimaryKey(areaId);
        Preconditions.checkArgument(count > 0, "删除失败");

        // 刷新缓存
        areaCache.refresh();

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<BaseAreaVO> query(BaseAreaQuery query) {

        List<BaseAreaDO> baseAreaDOS = baseAreaDOMapper.query(query);

        return null;
    }

    @Override
    public ResultBean<List<CascadeAreaVO>> list() {
        // 走缓存
        List<CascadeAreaVO> cascadeAreaVOList = areaCache.get();
        return ResultBean.ofSuccess(cascadeAreaVOList);
    }

    @Override
    public ResultBean<String> getFullAreaName(Long areaId) {
        Preconditions.checkNotNull(areaId, "区域ID不能为空");

        String fullAreaName = "";
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, null);
        if (null != baseAreaDO) {

            if (LEVEL_CITY.equals(baseAreaDO.getLevel())) {
                Long parentAreaId = baseAreaDO.getParentAreaId();
                if (null != parentAreaId) {
                    BaseAreaDO parentAreaDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                    if (null != parentAreaDO) {
                        // 省
                        fullAreaName = parentAreaDO.getAreaName();
                    }
                }
                // 市
                fullAreaName += baseAreaDO.getAreaName();
            } else {
                fullAreaName = baseAreaDO.getAreaName();
            }
        }

        return ResultBean.ofSuccess(fullAreaName);
    }

    @Override
    public ResultBean<List<CascadeAreaVO>> getApplyLicensePlateArea(Long orderId) {


        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO,"订单不存在");
        Long partnerId=loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId()).getPartnerId();


        List<CascadeAreaVO> ableAreaList = Lists.newArrayList();
        //根据父areaid获取所有子区域id
        List<Long> areaIdList = partnerRelaAreaDOMapper.getAreaIdListByPartnerId(partnerId);
        //根据子区域id获取所有子区域信息
        List<BaseAreaDO> hasBindArea = areaIdList.parallelStream().map(e->{
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(e, VALID_STATUS);
            return baseAreaDO;
        }).distinct().collect(Collectors.toList());
        List<CascadeAreaVO.City> baseAreaDOS = Lists.newArrayList();


        HashMap<Long, Set<Long>> areaMap = Maps.newHashMap();

        hasBindArea.stream().forEach(hasArea->{
            switch(hasArea.getLevel()){

                case 0:
                    ableAreaList.addAll(areaCache.get());
                    break;
                case 1:
                    List<Long> citys = baseAreaDOMapper.selectCityIdByProvenceId(hasArea.getAreaId());
                    if(areaMap.keySet().contains(hasArea.getAreaId())){
                        Set<Long> aaa = areaMap.get(hasArea.getAreaId());
                        aaa.addAll(citys);
                        areaMap.put(hasArea.getAreaId(),aaa);
                    }else{
                        Set<Long> tmp =  Sets.newHashSet();
                        tmp.addAll(citys);
                        areaMap.put(hasArea.getAreaId(),tmp);
                    }
                    break;
                case 2:
                    if(areaMap.keySet().contains(hasArea.getParentAreaId())){
                        Set<Long> aaa = areaMap.get(hasArea.getParentAreaId());
                        aaa.add(hasArea.getAreaId());
                    }else{
                        Set<Long> tmp =  Sets.newHashSet();
                        tmp.add(hasArea.getAreaId());
                        areaMap.put(hasArea.getParentAreaId(),tmp);
                    }
                    break;

            }
        });

        List<CascadeAreaVO> cascadeAreaVOS = fillInfo(ableAreaList,areaMap);


        return ResultBean.ofSuccess(cascadeAreaVOS);
    }

    /**
     * 填充信息
     * @param areaMap
     */
    private List<CascadeAreaVO> fillInfo( List<CascadeAreaVO> ableAreaList ,HashMap<Long, Set<Long>> areaMap) {
        List<CascadeAreaVO> voList = areaCache.get();
        voList.parallelStream().filter(Objects::nonNull).map(e->{

            return null;
        }).collect(Collectors.toList());
        areaMap.keySet().parallelStream().filter(Objects::nonNull).forEach(e->{
            CascadeAreaVO baseArea = new CascadeAreaVO();
            BaseAreaDO prov = baseAreaDOMapper.selectByPrimaryKey(e, VALID_STATUS);//省
            baseArea.setId(prov.getAreaId());
            baseArea.setName(prov.getAreaName());
//            baseArea.setProvAreaId(prov.getAreaId());
//            baseArea.setProvName(prov.getAreaName());

            List<Long> list1 = new ArrayList(areaMap.get(e));
            List<BaseAreaDO> cityList = baseAreaDOMapper.selectByIdList(list1, VALID_STATUS);
            List citys = cityList.parallelStream().map(f->{
                CascadeAreaVO.City city = new CascadeAreaVO.City();
                city.setId(f.getAreaId());
                city.setName(f.getAreaName());
                city.setLevel(f.getLevel());
                return city;
            }).collect(Collectors.toList());

            baseArea.setCityList(citys);

            ableAreaList.add(baseArea);
        });

        return ableAreaList;
    }

}
