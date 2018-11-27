package com.yunche.loan.config.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ModelsPara;
import com.yunche.loan.domain.param.SeriesPara;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class CarDbSynTask
{

    @Resource
    private BusinessReviewManager businessReviewManager;

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    private static final Logger LOG = LoggerFactory.getLogger(CarDbSynTask.class);

    @Scheduled(cron = "0 0 2 ? * SAT")
    @DistributedLock(200)
    public void synCarDb()
    {

    }



    public  void changeDb()
    {
        try {

            String brandsResult = businessReviewManager.getFinanceUnisal2("/api/car/brand", null);

            CommonFinanceResult<List<BaseBrandDO>> brandsResult1 = new CommonFinanceResult<List<BaseBrandDO>>();
            if (brandsResult != null && !"".equals(brandsResult)) {
                Type type = new TypeToken<CommonFinanceResult<List<BaseBrandDO>>>() {
                }.getType();
                Gson gson = new Gson();
                brandsResult1 = gson.fromJson(brandsResult, type);
            }

            if (brandsResult1.getDatas() != null && brandsResult1.getDatas().size() != 0) {
                List<BaseBrandDO> baseBrandDOS = brandsResult1.getDatas();
                baseBrandDOS
                        .stream()
                        .forEach(e -> {
                                    //先查询是否存在该id
                                    CarBrandDO isExistCarBrand = carBrandDOMapper.selectByPrimaryKey(Long.valueOf(e.getCode()), null);
                                    if (isExistCarBrand == null)//仅当不存在时进行插入
                                    {
                                        CarBrandDO carBrandDO = new CarBrandDO();
                                        carBrandDO.setId(Long.valueOf(e.getCode()));
                                        carBrandDO.setInitial(e.getInitial());
                                        carBrandDO.setLogo(e.getIcon());
                                        carBrandDO.setName(e.getName());
                                        carBrandDOMapper.insertSelective(carBrandDO);
                                    }


                                    //根据品牌id继续查车系
                                    SeriesPara seriesPara = new SeriesPara();
                                    seriesPara.setBrand(e.getCode());
                                    String seriesesResult = businessReviewManager.financeUnisal2(seriesPara, "/api/car/series");

                                    CommonFinanceResult<List<BaseSeriesDO>> seriesesResult1 = new CommonFinanceResult<List<BaseSeriesDO>>();
                                    if (seriesesResult != null && !"".equals(seriesesResult)) {
                                        Type type = new TypeToken<CommonFinanceResult<List<BaseSeriesDO>>>() {
                                        }.getType();
                                        Gson gson = new Gson();
                                        seriesesResult1 = gson.fromJson(seriesesResult, type);
                                    }

                                    if (seriesesResult1.getDatas() != null && seriesesResult1.getDatas().size() != 0) {
                                        List<BaseSeriesDO> baseSeriesDOS = seriesesResult1.getDatas();
                                        baseSeriesDOS.stream()
                                                .forEach(
                                                        f -> {
                                                            CarModelDO isExistCarSeries = carModelDOMapper.selectByPrimaryKey(Long.valueOf(f.getCode()), null);
                                                            if (isExistCarSeries == null) {
                                                                CarModelDO carModelDO = new CarModelDO();
                                                                carModelDO.setId(Long.valueOf(f.getCode()));
                                                                carModelDO.setBrandId(Long.valueOf(f.getBrand()));
                                                                carModelDO.setLogo(f.getImg_url());
                                                                carModelDO.setName(f.getName());

                                                                carModelDOMapper.insertSelective(carModelDO);
                                                            }

                                                            ModelsPara modelsPara = new ModelsPara();
                                                            modelsPara.setSeries(f.getCode());
                                                            String modelsResult = businessReviewManager.financeUnisal2(modelsPara, "/api/car/model");

                                                            CommonFinanceResult<List<BaseModelDO>> modelsResult1 = new CommonFinanceResult<List<BaseModelDO>>();
                                                            if (modelsResult != null && !"".equals(modelsResult)) {
                                                                Type type = new TypeToken<CommonFinanceResult<List<BaseModelDO>>>() {
                                                                }.getType();
                                                                Gson gson = new Gson();
                                                                modelsResult1 = gson.fromJson(modelsResult, type);
                                                            }

                                                            if (modelsResult1.getDatas() != null && modelsResult1.getDatas().size() != 0) {
                                                                List<BaseModelDO> baseModelDOS = modelsResult1.getDatas();
                                                                baseModelDOS.stream()
                                                                        .forEach(g -> {

                                                                            //-------
                                                                            CarDetailDO isExistCarDetail = carDetailDOMapper.selectByPrimaryKey(Long.valueOf(g.getCode()), null);
                                                                            if (isExistCarDetail == null) {
                                                                                CarDetailDO carDetailDO = new CarDetailDO();
                                                                                carDetailDO.setId(Long.valueOf(g.getCode()));

                                                                                StringBuilder stringBuilder = new StringBuilder();
                                                                                if (g.getModel_year() != null) {
                                                                                    stringBuilder.append(g.getModel_year()).append("款 ");
                                                                                }
                                                                                if (g.getName() != null && !"".equals(g.getName())) {
                                                                                    stringBuilder.append(g.getName());
                                                                                }
                                                                                carDetailDO.setName(stringBuilder.toString());
                                                                                carDetailDO.setModelId(Long.valueOf(g.getSeries()));

                                                                                carDetailDOMapper.insertSelective(carDetailDO);
                                                                            }


                                                                        });

                                                            }


                                                        }
                                                );

                                    }

                                }
                        );
            }
        }catch (Exception e)
        {
            LOG.error("同步车型库失败");
        }

    }
}
