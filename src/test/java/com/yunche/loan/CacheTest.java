package com.yunche.loan;

import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.cache.TokenCache;
import com.yunche.loan.config.util.CarLoanHttpUtil;
import com.yunche.loan.config.util.OpenApiUtil;
import com.yunche.loan.domain.vo.GpsDetailTotalVO;
import com.yunche.loan.service.AuxiliaryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CacheTest extends BaseTest {
    @Autowired
    private TokenCache tokenCache;
    @Autowired
    private BankCache bankCache;

    @Resource
    private AuxiliaryService auxiliaryService;



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
        auxiliaryService.queryJimi("yy团队");
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



   @Test
    public void getDetail(){
        try {
            String accToken=getAccToken();
            OpenApiUtil.getGpsDetailInfo(accToken,"868120182173432");
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
