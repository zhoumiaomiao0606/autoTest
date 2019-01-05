package com.yunche.loan.config.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ModelsPara;
import com.yunche.loan.domain.param.SeriesPara;
import com.yunche.loan.domain.vo.BaseBrandInitial;
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
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CarDbSynTask
{

    @Autowired
    private FinanceConfig financeConfig;

    @Resource
    private BusinessReviewManager businessReviewManager;

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    private static final Logger LOG = LoggerFactory.getLogger(CarDbSynTask.class);

    @Scheduled(cron = "0 0 4 ? * MON")
    @DistributedLock(200)
    public void synCarDb()
    {
        changeDb();
    }



    public  void changeDb() {
        try {

        String brandsResult = businessReviewManager.getFinanceUnisal2("/api/car/brand",financeConfig.getSecondCarHost(), null);

        CommonFinanceResult<List<BaseBrandInitial>> brandsResult1 = new CommonFinanceResult<List<BaseBrandInitial>>();
        if (brandsResult != null && !"".equals(brandsResult)) {
            Type type = new TypeToken<CommonFinanceResult<List<BaseBrandInitial>>>() {
            }.getType();
            Gson gson = new Gson();
            brandsResult1 = gson.fromJson(brandsResult, type);
        }

        List<Method> getMethods = new ArrayList();
        Class<BaseBrandInitial> class1 = BaseBrandInitial.class;
        Field[] fields = class1.getDeclaredFields();

        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            // 此处应该判断beanObj,property不为null
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), class1);
            getMethods.add(pd.getReadMethod());
        }

        if (brandsResult1.getDatas() != null && brandsResult1.getDatas().size() != 0) {
            List<BaseBrandInitial> baseBrandInitials = brandsResult1.getDatas();
            baseBrandInitials
                    .stream()
                    .forEach(
                            c -> {
                                //对象体----反射调用方法
                                for (int j = 0; j < getMethods.size(); j++)
                                {
                                    try {
                                        List<BaseBrandDO> list = (List<BaseBrandDO>)getMethods.get(j).invoke(c);

                                        if (list!=null && list.size()!=0)
                                        {
                                            list
                                                    .stream()
                                                    .filter(r -> r.getCode() != null)
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
                                                                    carBrandDO.setGmtCreate(new Date());
                                                                    carBrandDOMapper.insertSelective(carBrandDO);
                                                                }


                                                                //根据品牌id继续查车系
                                                                SeriesPara seriesPara = new SeriesPara();
                                                                seriesPara.setBrand(e.getCode());
                                                                String seriesesResult = businessReviewManager.financeUnisal2(seriesPara,financeConfig.getSecondCarHost(), "/api/car/series");

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
                                                                            .filter(q -> q.getCode() != null)
                                                                            .forEach(
                                                                                    f -> {
                                                                                        CarModelDO isExistCarSeries = carModelDOMapper.selectByPrimaryKey(Long.valueOf(f.getCode()), null);
                                                                                        if (isExistCarSeries == null) {
                                                                                            CarModelDO carModelDO = new CarModelDO();
                                                                                            carModelDO.setId(Long.valueOf(f.getCode()));
                                                                                            carModelDO.setBrandId(Long.valueOf(f.getBrand()));
                                                                                            carModelDO.setLogo(f.getImg_url());
                                                                                            carModelDO.setName(f.getName());
                                                                                            carModelDO.setFullName(f.getName());
                                                                                            carModelDO.setGmtCreate(new Date());

                                                                                            carModelDOMapper.insertSelective(carModelDO);
                                                                                        }

                                                                                        ModelsPara modelsPara = new ModelsPara();
                                                                                        modelsPara.setSeries(f.getCode());
                                                                                        String modelsResult = businessReviewManager.financeUnisal2(modelsPara, financeConfig.getSecondCarHost(),"/api/car/model");

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
                                                                                                    .filter(w -> w.getCode() != null)
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
                                                                                                            carDetailDO.setGmtCreate(new Date());

                                                                                                            carDetailDOMapper.insertSelective(carDetailDO);
                                                                                                        }

                                                                                                        LOG.info("====车型库插入了一条记录");


                                                                                                    });

                                                                                        }


                                                                                    }
                                                                            );

                                                                }

                                                            }
                                                    );

                                        }


                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

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
