package com.yunche.loan;

import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.cache.TokenCache;
import com.yunche.loan.config.util.CarLoanHttpUtil;
import com.yunche.loan.config.util.OpenApiUtil;
import com.yunche.loan.config.util.ZhongAnHttpUtil;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.param.ZhongAnCusParam;
import com.yunche.loan.domain.param.ZhongAnQueryParam;
import com.yunche.loan.domain.vo.GpsDetailTotalVO;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.AppLoanOrderService;
import com.yunche.loan.service.AuxiliaryService;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheTest extends BaseTest {
    @Autowired
    private TokenCache tokenCache;
    @Autowired
    private BankCache bankCache;

    @Resource
    private AuxiliaryService auxiliaryService;

    @Resource
    PartnerDOMapper partnerDOMapper;

    @Resource
    AppLoanOrderService appLoanOrderService;

    @Test

    public void getAAA(){
        Map map = new HashMap();
        boolean fals = (boolean)map.get("A");
    }

   //@Test
    public void doA1123(){
        try {
            /*ZhongAnQueryParam zhongAnQueryParam = new ZhongAnQueryParam();
            zhongAnQueryParam.setOrder_id("123");
            List<ZhongAnCusParam> customers = Lists.newArrayList();
            ZhongAnCusParam zhongAnCusParam = new ZhongAnCusParam();
            zhongAnCusParam.setIdcard("341226198404016579");
            zhongAnCusParam.setName("武军");
            zhongAnCusParam.setCustomertype("主贷人");
            zhongAnCusParam.setRalationship("1");
            zhongAnCusParam.setTel("15658880777");
            zhongAnCusParam.setLoanmoney("1");
            customers.add(zhongAnCusParam);
            zhongAnQueryParam.setCustomers(customers);
            appLoanOrderService.zhongAnQuery(zhongAnQueryParam);*/
           // ZhongAnHttpUtil.queryInfo("易翠","18210819553","362228199206074083","1","2","3333","1","999967");

            // ZhongAnHttpUtil.queryInfo("易翠","18210819553","362228199206074083","1","2","3333","1","99993");
           ZhongAnHttpUtil.queryInfo("文秋生","17985865856","36078119860618513X","1","2","3333","","98811");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getToken() throws Exception {
        OpenApiUtil.getToken();
    }
   // @Test
    public void refToken(){
        try {
            OpenApiUtil.refreshToken("fcd8c2fcc670aa7d86514b97298678fc","af1a423014db1117440aadbcb4998ec4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   //@Test
    public void doA(){
        auxiliaryService.queryJimi("浙江杭州测试张三团队");
    }
   // @Test
    public void doB(){
        GpsDetailTotalVO a=auxiliaryService.detail(Long.valueOf("1805041826416031160"));
        int i=0;
    }

    //@Test
    public void getGpsinfo(){
        try {
            OpenApiUtil.getGpsInfo(getAccToken(),"浙江鑫宝行");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   //@Test
    public void update()  {
        try{
            OpenApiUtil.updateGpsInfo(getAccToken(),"868120193961791","","");
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //@Test
    public void doA1(){
        PartnerDO partnerDO = partnerDOMapper.queryLeaderNameById(Long.valueOf("1805041826416031160"));
        System.out.println(partnerDO.getGpsAccount());
    }
    //@Test
    public void getchild(){
        try{
            String accToken=getAccToken();
            System.out.println(accToken);
            List<Map<String,Object>> list = OpenApiUtil.getChildTarget(accToken);
            if(list.size() > 0){
                while("1004".equals((String)list.get(0).get("code"))){
                    list = OpenApiUtil.getChildTarget(getAccToken());
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //@Test
    public void carLoanGetGpsInfo() throws Exception {
        CarLoanHttpUtil.getGpsInfo("39182250606");
    }

    //@Test
    public void carLoanUpdateGpsInfo() throws Exception {
        CarLoanHttpUtil.getGpsStatus("39182250606");
    }
    //@Test
    public void createCus()throws Exception {
       // CarLoanHttpUtil.modifyCustomer("111111","808","志飞","奥迪");
        CarLoanHttpUtil.bindGps("39182250606","808");
    }
    //@Test
    public void unBind()throws Exception{
        CarLoanHttpUtil.unbindGps("39182250606","808");
    }


   //@Test
    public void getDetail(){
        try {
            String accToken=getAccToken();
            OpenApiUtil.getGpsDetailInfo(accToken,"868120191493086");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private String getAccToken() throws Exception {
        String[] tokens = tokenCache.getToken();

        /*String[] tokens = new String[3];
        tokens[0]="";
        tokens[1]="";
        tokens[2]="";*/
        String accToken="";
        if("".equals(tokens[0])){
            String[] tokenStr = OpenApiUtil.getToken();
            if("1006".equals(tokenStr[0])){
                while(true){
                    String repToken = tokenCache.getToken()[0];
                    if(!"".equals(repToken)){
                        accToken = repToken;
                        break;
                    }
                }
            }else{
                accToken = tokenStr[0];
                tokenCache.insertToken(tokenStr[0], tokenStr[1]);
            }
        }else{
            accToken = tokens[0];
        }
        return accToken;
    }
}
