package com.yunche.loan;

import com.google.common.collect.Lists;
import com.jcraft.jsch.SftpException;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.cache.TokenCache;
import com.yunche.loan.config.thread.ThreadPool;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.param.ZhongAnCusParam;
import com.yunche.loan.domain.param.ZhongAnQueryParam;
import com.yunche.loan.domain.vo.GpsDetailTotalVO;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.AppLoanOrderService;
import com.yunche.loan.service.AuxiliaryService;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import com.yunche.loan.service.MaterialService;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

public class CacheTest extends BaseTest{
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
    @Autowired
    JinTouHangAccommodationApplyService jinTouHangAccommodationApplyService;
    //@Test
    public void SDFSDF(){
        JTXFileUtil sftp = new JTXFileUtil("root", "jtx@1722", "183.136.187.207", 22);
        sftp.login();
        //byte[] buff = sftp.download("/opt", "start.sh");
        //System.out.println(Arrays.toString(buff));
        File file = new File("/Users/admin/Desktop/jjqTest.txt");
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            sftp.upload("/root/yunche/reqFile", "jjqTest.txt", is);
            sftp.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    //@Test
    public void BCXBVXCBX(){
        JTXFileUtil sftp = new JTXFileUtil("root", "jtx@1722", "183.136.187.207", 22);
        sftp.login();
        try {
            sftp.download("/root/yunche/reqFile/20181031","jjqTest.txt","/Users/admin/Desktop/jjqjjq.txt");
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sftp.logout();
    }
    //@Test
    public void doXVZXV(){
        Map<String, Object>  map = new HashMap();
        Map map1 = new HashMap();
        map1.put("A1","小李");
        map1.put("A2","2");
        map.put("A",map1);
        Map map2 = new HashMap();
        map2.put("B1","1");
        map2.put("B2","2");
        map.put("B",map2);
       System.out.println( MapXmlUtil.createXmlByMap(map,"MsgText"));
    }
   // @Test
    public void asdaksdjals(){
        String xml ="<?xml version=\"1.0\" encoding=\"GB2312\"?>\n" +
                "<MsgText>\n" +
                " <A>\n" +
                "  <A1>1</A1>\n" +
                "  <A2>2</A2>\n" +
                " </A>\n" +
                " <B>\n" +
                "  <B2>2</B2>\n" +
                "  <B1>1</B1>\n" +
                " </B>\n" +
                "</MsgText>";
        try {
           System.out.println( MapXmlUtil.Xml2Map(xml).toString());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }//
    //@Test
    public void asdasdas(){
        String data ="+Ee3tYz3PT34o6F4LkLjjrkafwTx1IdP6C+ZVewEOUF/U77RXsHRn6v1eJ05ZXYp5Krxa4p+wNCC\n" +
                "9Y30dtOwtQaoMaSRRtr3j8bQVdPFlRvR1Zekan4Pm/8QQZcqsCPHPegmHG7fn6iEphnglA9VH3ZJ\n" +
                "lIewvFlyZZIaPBjNkXlbUYv1Vikq70+k8ddLE20VGesQadd2FqRQ61rZ1fUwIjiWHtOEJFrSbo7X\n" +
                "dp4HsJ4wh/vQ8k6RPAeY32bx9wbczgvQdL8KJ/m/jBLLEWs6Q1elO79iYeJtrxw8gMSFsXet334h\n" +
                "ibs7RePD9i+7c7b8d67xuC2lRBzpjz5mRnXu8Pm7twoMAhGKx1t1XebdItGAjJnjIhwZtbRW+52E\n" +
                "kpymnGoL8VBITWD+ZVJ5NRM3nG/i5p/yUDyspbTXtEmuAaFCTWE6pg1KJ3GKIdou+sDO";
        try {
            System.out.println(JTXByteUtil.decrypt(data,"netwxactive","GBK","des"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //@Test
    public void bvjkashkj(){
        try {
            byte[] param = JTXByteUtil.encrypt("123".getBytes("GBK"), "netwxactive".getBytes("GBK"),"DES");
            String result = JTXHttpUtil.sendPost("http://121.41.20.151:7013/assetApi/service",param);
            String xml = JTXByteUtil.decrypt(result,"netwxactive","GBK","des");
            Map map = MapXmlUtil.Xml2Map(xml);
            Map map1 = (Map)map.get("MsgBody");
            System.out.println(map1.get("RetCode"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void knkjashkjadkd(){
        System.out.println(jinTouHangAccommodationApplyService.jtxResult("+Ee3tYz3PT34o6F4LkLjjrkafwTx1IdP6C+ZVewEOUF/U77RXsHRn6v1eJ05ZXYp5Krxa4p+wNCC\n" +
                "9Y30dtOwtQaoMaSRRtr3j8bQVdPFlRvR1Zekan4Pm/8QQZcqsCPHPegmHG7fn6iEphnglA9VH3ZJ\n" +
                "lIewvFlyZZIaPBjNkXlbUYv1Vikq70+k8ddLE20VGesQadd2FqRQ61rZ1fUwIjiWHtOEJFrSbo7X\n" +
                "dp4HsJ4wh/vQ8k6RPAeY32bx9wbczgvQdL8KJ/m/jBLLEWs6Q1elO79iYeJtrxw8gMSFsXet334h\n" +
                "ibs7RePD9i+7c7b8d67xuC2lRBzpjz5mRnXu8Pm7twoMAhGKx1t1XebdItGAjJnjIhwZtbRW+52E\n" +
                "kpymnGoL8VBITWD+ZVJ5NRM3nG/i5p/yUDyspbTXtEmuAaFCTWE6pg1KJ3GKIdou+sDO"));
    }

    //@Test
    public void ASDASD(){
        String chinese="金祎朋";
        int length = chinese.length();
        char[] value = new char[length << 1];
        for (int i=0, j=0; i<length; ++i, j = i << 1) {
            value[j] = chinese.charAt(i);
            value[1 + j] = ' ';
        }
        chinese = new String(value);
        chinese = chinese.substring(0,chinese.length()-1);
        System.out.println(chinese.length());

        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        System.out.println(pybf.toString());
    }

   // @Test
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
