package com.yunche.loan.manager.finance;

import com.google.gson.Gson;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.HttpUtils;
import com.yunche.loan.domain.param.ParternerRuleParam;
import com.yunche.loan.domain.param.ParternerRuleSharpTuningeParam;
import com.yunche.loan.domain.vo.FinanceResult;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.yunche.loan.config.constant.BaseExceptionEnum.EL00000003;

@Component
public class BusinessReviewManager {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessReviewManager.class);

    private static final String HOST = "http://47.96.78.20:8012";


   /* public ResultBean parternerRule(ParternerRuleParam param)
    {
        String result = null;
        try {
            result = HttpUtils.doPost(HOST, PATH, null, param.toString());
        } catch (Exception e) {
            LOG.error("请求财务系统出错---！！",e);
            return ResultBean.ofError(EL00000003);
        }
        return ResultBean.ofSuccess(result);
    }*/

   /* public ResultBean parternerRuleSharpTuning(ParternerRuleSharpTuningeParam param)
    {
        return null;
    }*/


   //通用
    public static <T> String financeUnisal(T param,String PATH)
    {
        String result = null;
        try {
            result = HttpUtils.doPost(HOST, PATH, null, param.toString());
            System.out.println("请求参数"+param.toString());
            System.out.println("请求结果"+result);

            if (result == null)
            {
                throw new BizException("请求到财务数据为空");
            }

        } catch (Exception e) {
            LOG.error("请求财务系统出错---！！",e);
            throw new BizException("请求财务系统出错");

        }

        return result;
    }

    //通用
    public static <T> String getFinanceUnisal(String PATH)
    {
        String result =null;
        try {
            HttpResponse httpResponse = HttpUtils.doGet(HOST, PATH, null,null,null);


            if(httpResponse!=null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                result = EntityUtils.toString(httpResponse.getEntity());// 返回json格式：
                LOG.error("数据读取！！！！"+result);
            }


            if (result == null)
            {
                throw new BizException("请求到财务数据为空");
            }

        } catch (Exception e) {
            LOG.error("请求财务系统出错---！！",e);
            throw new BizException("请求财务系统出错");

        }

        return result;
    }
}
