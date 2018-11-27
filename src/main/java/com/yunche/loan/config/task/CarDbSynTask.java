package com.yunche.loan.config.task;

import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.domain.entity.CarBrandDO;
import com.yunche.loan.domain.entity.CarDetailDO;
import com.yunche.loan.domain.entity.CarModelDO;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarDbSynTask
{

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



    /*public  void changeDb()
    {
        List<BaseBrandDO> baseBrandDOS = baseBrandDOMapper.selectAll();
        baseBrandDOS
                .stream()
                .forEach(e->{
                            CarBrandDO carBrandDO =new CarBrandDO();
                            carBrandDO.setId(Long.valueOf(e.getCode()));
                            carBrandDO.setInitial(e.getInitial());
                            carBrandDO.setLogo(e.getIcon());
                            carBrandDO.setName(e.getName());
                            carBrandDOMapper.insertSelective(carBrandDO);
                        }
                );


        List<BaseSeriesDO> baseSeriesDOS = baseSeriesDOMapper.selectAll();
        baseSeriesDOS.stream()
                .forEach(
                        f->{
                            CarModelDO carModelDO = new CarModelDO();
                            carModelDO.setId(Long.valueOf(f.getCode()));
                            carModelDO.setBrandId(Long.valueOf(f.getBrand()));
                            carModelDO.setLogo(f.getImg_url());
                            carModelDO.setName(f.getName());

                            carModelDOMapper.insertSelective(carModelDO);
                        }
                );


        List<BaseModelDO> baseModelDOS = baseModelDOMapper.selectAll();
        baseModelDOS.stream()
                .forEach(g ->{
                    CarDetailDO carDetailDO = new CarDetailDO();
                    carDetailDO.setId(Long.valueOf(g.getCode()));

                    StringBuilder stringBuilder =new StringBuilder();
                    if (g.getModel_year()!=null)
                    {
                        stringBuilder.append(g.getModel_year()).append("款 ");
                    }
                    if (g.getName()!=null && !"".equals(g.getName()))
                    {
                        stringBuilder.append(g.getName());
                    }
                    carDetailDO.setName(stringBuilder.toString());
                    carDetailDO.setModelId(Long.valueOf(g.getSeries()));

                });
    }*/
}
