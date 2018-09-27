package com.yunche.loan;

import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.cache.TokenCache;
import com.yunche.loan.config.thread.ThreadPool;
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
import com.yunche.loan.service.MaterialService;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CacheTest  {
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
    @Resource
    MaterialService materialService;

    @Test
    public void DFDSf(){
        List<String> list =new ArrayList<>();
        list.add(null);
        list.add("1");
        list.add("1");
        HashSet h =new HashSet(list);
        list.clear();
        list.addAll(h);
        System.out.println(list.size());
        list.retainAll(Collections.singleton(null));
        System.out.println(list.size());

        List<String> list1 =new ArrayList<>();
        list1.add("2");
        list.retainAll(list1);
        System.out.println(list.size());
    }
   // @Test
    public void DFDS(){
        List list =new ArrayList(2);
        list.add(1);
        list.add(2);
        list.remove(0);
        System.out.println(list.get(0));
    }
    //@Test
    public void ADdasd(){
        List list =new ArrayList(10);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        for(int i =0;i<list.size();i++) {
            ThreadPool.executorService.execute(() -> {

                synchronized (list) {
                    System.out.println(Thread.currentThread().getId() + "|" + list.get(0));
                    list.remove(0);
                }
            });
        }
    }

    //@Test
    public void doDo(){
        String fileName = "TelUser10.xlsx";
        //创建workbook
        File file = new File( fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
            sheet.addMergedRegion(new CellRangeAddress(0,0,2,4));
            sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));
            sheet.addMergedRegion(new CellRangeAddress(0,0,8,10));
            sheet.addMergedRegion(new CellRangeAddress(0,0,11,13));
            sheet.addMergedRegion(new CellRangeAddress(0,0,14,16));
            XSSFRow headRow = sheet.createRow(0);
            XSSFCell cell0 = headRow.createCell(0);
            cell0.setCellValue("大区");
            XSSFCell cell1= headRow.createCell(1);
            cell1.setCellValue("合伙人");
            XSSFCell cell2 = headRow.createCell(2);
            cell2.setCellValue("电审结果-打回");
            XSSFCell cell3 = headRow.createCell(5);
            cell3.setCellValue("电审结果-资料增补");
            XSSFCell cell4 = headRow.createCell(8);
            cell4.setCellValue("电审结果-通过");
            XSSFCell cell5 = headRow.createCell(11);
            cell5.setCellValue("电审结果-弃单");
            XSSFCell cell6 = headRow.createCell(14);
            cell6.setCellValue("经办汇总");

            XSSFRow headRow1 = sheet.createRow(1);
            ArrayList<String> header1 = Lists.newArrayList("单量", "占比","贷款金额",
                    "单量", "占比","贷款金额","单量", "占比","贷款金额","单量", "占比","贷款金额","单量", "占比","贷款金额"
            );
            for (int i = 0; i < header1.size(); i++) {
                XSSFCell cell = headRow1.createCell(i+1);
                cell.setCellValue(header1.get(i));
            }


            workbook.write(out);
        }catch (Exception e){

        }
    }


    // @Test

    public void getAAA() {
        File f = new File("/Users/admin/Desktop/有意义.xlsx");
        System.out.println(f.length());
    }

    //@Test
    public void doV() {
        System.out.println("tmp/123".substring(4));
    }

    //@Test
    public void doY() {
        materialService.downSupplementFiles2OSS(Long.valueOf("1809051406599576357"), true, Long.valueOf("193"));
    }

    // @Test
    public void doA1123() {
        try {
            ZhongAnQueryParam zhongAnQueryParam = new ZhongAnQueryParam();
            zhongAnQueryParam.setOrder_id("123");
            List<ZhongAnCusParam> customers = Lists.newArrayList();
            ZhongAnCusParam zhongAnCusParam = new ZhongAnCusParam();
            zhongAnCusParam.setIdcard("362228199206074083");
            zhongAnCusParam.setName("易翠");
            zhongAnCusParam.setCustomertype("主贷人");
            zhongAnCusParam.setRalationship("1");
            zhongAnCusParam.setTel("15757871316");
            zhongAnCusParam.setLoanmoney("1");
            customers.add(zhongAnCusParam);
            zhongAnQueryParam.setCustomers(customers);
            appLoanOrderService.zhongAnQuery(zhongAnQueryParam);
            // ZhongAnHttpUtil.queryInfo("易翠","18210819553","362228199206074083","1","2","3333","1","999967");

            // ZhongAnHttpUtil.queryInfo("易翠","18210819553","362228199206074083","1","2","3333","1","99993");
//
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getToken() throws Exception {
        OpenApiUtil.getToken();
    }

    // @Test
    public void refToken() {
        try {
            OpenApiUtil.refreshToken("fcd8c2fcc670aa7d86514b97298678fc", "af1a423014db1117440aadbcb4998ec4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void doA() {
        auxiliaryService.queryJimi("浙江杭州测试张三团队");
    }

    // @Test
    public void doB() {
        GpsDetailTotalVO a = auxiliaryService.detail(Long.valueOf("1805041826416031160"));
        int i = 0;
    }

    //@Test
    public void getGpsinfo() {
        try {
            OpenApiUtil.getGpsInfo(getAccToken(), "浙江鑫宝行");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void update() {
        try {
            OpenApiUtil.updateGpsInfo(getAccToken(), "868120193961791", "", "");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //@Test
    public void doA1() {
        PartnerDO partnerDO = partnerDOMapper.queryLeaderNameById(Long.valueOf("1805041826416031160"));
        System.out.println(partnerDO.getGpsAccount());
    }

    //@Test
    public void getchild() {
        try {
            String accToken = getAccToken();
            System.out.println(accToken);
            List<Map<String, Object>> list = OpenApiUtil.getChildTarget(accToken);
            if (list.size() > 0) {
                while ("1004".equals((String) list.get(0).get("code"))) {
                    list = OpenApiUtil.getChildTarget(getAccToken());
                }
            }
        } catch (Exception e) {
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
    public void createCus() throws Exception {
        // CarLoanHttpUtil.modifyCustomer("111111","808","志飞","奥迪");
        CarLoanHttpUtil.bindGps("39182250606", "808");
    }

    //@Test
    public void unBind() throws Exception {
        CarLoanHttpUtil.unbindGps("39182250606", "808");
    }


    //@Test
    public void getDetail() {
        try {
            String accToken = getAccToken();
            OpenApiUtil.getGpsDetailInfo(accToken, "868120191493086");
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
        String accToken = "";
        if ("".equals(tokens[0])) {
            String[] tokenStr = OpenApiUtil.getToken();
            if ("1006".equals(tokenStr[0])) {
                while (true) {
                    String repToken = tokenCache.getToken()[0];
                    if (!"".equals(repToken)) {
                        accToken = repToken;
                        break;
                    }
                }
            } else {
                accToken = tokenStr[0];
                tokenCache.insertToken(tokenStr[0], tokenStr[1]);
            }
        } else {
            accToken = tokens[0];
        }
        return accToken;
    }
}
