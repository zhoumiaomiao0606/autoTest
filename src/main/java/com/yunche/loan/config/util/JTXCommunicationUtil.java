package com.yunche.loan.config.util;

import com.yunche.loan.config.common.JtxConfig;
import com.yunche.loan.domain.entity.JtxCommunicationDO;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.mapper.JtxCommunicationDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JTXCommunicationUtil {

    private Logger logger = LoggerFactory.getLogger(JTXCommunicationUtil.class);

    @Autowired
    private JtxCommunicationDOMapper jtxCommunicationDOMapper;
    @Autowired
    private JtxConfig jtxConfig;


    //ASSET_01
    public Map borrowerInfoAuth(String name, String idCard, String tel, String bank, String bankCard, AccommodationApplyParam.IDPair idPair) {
        Map returnMap = new HashMap();
        boolean falg = false;
        Map<String, Object> paramMap = new HashMap<>();
        headBuild(paramMap, "ASSET_01",null);
        String jtxId = (String)((Map)paramMap.get("MsgHdr")).get("Ref");
        JtxCommunicationDO jtxCommunicationDO = new JtxCommunicationDO();
        jtxCommunicationDO.setJtxId(jtxId);
        jtxCommunicationDO.setBridgeProcecssId(idPair.getBridgeProcessId());
        jtxCommunicationDO.setOrderId(idPair.getOrderId());
        jtxCommunicationDO.setCreateDate(new Date());
        jtxCommunicationDO.setIdcard(idCard);
        jtxCommunicationDO.setName(name);
        jtxCommunicationDO.setOrderStatus(1);
        jtxCommunicationDOMapper.insertSelective(jtxCommunicationDO);
        Map bodyMap = new HashMap();
        bodyMap.put("Type", "0");
        bodyMap.put("RealName", name);
        bodyMap.put("IdentityNum", idCard);
        bodyMap.put("PhoneNum", tel);
        bodyMap.put("BankName", "工商银行");
        bodyMap.put("BankCard", bankCard);
        bodyMap.put("Organization", "");
        bodyMap.put("OrganizationCode", "");
        paramMap.put("MsgBody", bodyMap);
        String paramXml = MapXmlUtil.createXmlByMap(paramMap, "MsgText");
        logger.info("ASSET_01请求信息:"+paramXml);
        try {
            byte[] param = JTXByteUtil.encrypt(paramXml.getBytes("GBK"), "netwxactive".getBytes("GBK"), "DES");
            String encode_req = new BASE64Encoder().encode(param);
            String result = JTXHttpUtil.sendPost(jtxConfig.getJtxUrl(), encode_req);
            String xml = JTXByteUtil.decrypt(result, "netwxactive", "GBK", "des");
            logger.info("ASSET_01返回信息:" + xml);
            Map map = MapXmlUtil.Xml2Map(xml);
            Map map1 = (Map) map.get("MsgBody");
            Map map2 = (Map) map.get("MsgHdr");
            if ("0000".equals((String) map1.get("RetCode"))) {
                falg = true;
                returnMap.put("REF", (String) map2.get("Ref"));
            }else if ("该借款人已经认证！".equals((String)map1.get("RetMsg"))){
                falg = true;
                returnMap.put("REF", (String) map2.get("Ref"));
            }
            else{
                JtxCommunicationDO jtxCommunicationDO1 = new JtxCommunicationDO();
                jtxCommunicationDO1.setJtxId(jtxId);
                jtxCommunicationDO1.setUpdateDate(new Date());
                jtxCommunicationDO1.setErrorInfo((String)map1.get("RetCode")+(String)map1.get("RetMsg"));
                jtxCommunicationDOMapper.updateByPrimaryKeySelective(jtxCommunicationDO1);
            }
        } catch (Exception e) {
            logger.error("ASSET_01接口通讯异常", e);
        } finally {
            returnMap.put("FLAG", falg);
            return returnMap;
        }
    }

    //ASSET_02
    public boolean batchTransaction(String type, String name, String path, String count) {
        boolean falg = false;
        Map<String, Object> paramMap = new HashMap<>();
        headBuild(paramMap, "ASSET_02",null);
        Map bodyMap = new HashMap();
        bodyMap.put("Type", type);
        bodyMap.put("FileName", name);
        bodyMap.put("FilePath", path);
        bodyMap.put("Counts", count);
        paramMap.put("MsgBody", bodyMap);
        String paramXml = MapXmlUtil.createXmlByMap(paramMap, "MsgText");
        try {
            byte[] param = JTXByteUtil.encrypt(paramXml.getBytes("GBK"), "netwxactive".getBytes("GBK"), "DES");
            String encode_req = new BASE64Encoder().encode(param);
            String result = JTXHttpUtil.sendPost(jtxConfig.getJtxUrl(), encode_req);
            String xml = JTXByteUtil.decrypt(result, "netwxactive", "GBK", "des");
            logger.info("ASSET_02返回信息:" + xml);
            Map map = MapXmlUtil.Xml2Map(xml);
            Map map1 = (Map) map.get("MsgBody");
            if ("0000".equals((String) map1.get("RetCode"))) {
                falg = true;
            }
        } catch (Exception e) {
            logger.error("ASSET_01接口通讯异常", e);
        } finally {
            return falg;
        }
    }

    //ASSET_03
    public String  buildResultInfo(String retCode, String retMsg, String repRef,String ref) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss");
        Map<String, Object> paramMap = new HashMap<>();
        Map headerMap = new HashMap();
        headerMap.put("Ver", "1.0");
        headerMap.put("SysType", "MGR");
        headerMap.put("InstrCd", "ASSET_03");
        headerMap.put("TradSrc", "YUNCHE");
        headerMap.put("SvInst", "ASSET_MANAGE_SYS");
        headerMap.put("Date", sdf.format(date));
        headerMap.put("Time", sdf1.format(date));
        headerMap.put("Ref", ref);
        paramMap.put("MsgHdr", headerMap);
        Map bodyMap = new HashMap();
        bodyMap.put("RetCode", retCode);
        bodyMap.put("RetMsg", retMsg);
        bodyMap.put("RepRef", repRef);
        paramMap.put("MsgBody", bodyMap);
        String paramXml = MapXmlUtil.createXmlByMap(paramMap,"MsgText");
        logger.info("ASSET_03返回信息:"+paramXml);
        try {
            byte[] param = JTXByteUtil.encrypt(paramXml.getBytes("GBK"), "netwxactive".getBytes("GBK"), "DES");
            String encode_req = new BASE64Encoder().encode(param);
            return encode_req;
        } catch (Exception e) {
            logger.error("ASSET_03返回报文异常", e);
            return "";
        }

    }


    //ASSET_04  （EQUAL_CI("等额本息") ）
    public boolean assetRelease(String ref, String name, String principal, String rate, String interestDate,
                                String timeLimit, String interest, String payInterestType, String source,
                                String pledge, String value, String borrowType, String identityNum) {
        JtxCommunicationDO jtxCommunicationDO = new JtxCommunicationDO();
        jtxCommunicationDO.setJtxId(ref);
        jtxCommunicationDO.setOrderStatus(2);
        jtxCommunicationDO.setUpdateDate(new Date());
        jtxCommunicationDOMapper.updateByPrimaryKeySelective(jtxCommunicationDO);
        boolean falg = false;
        Map<String, Object> paramMap = new HashMap<>();
        headBuild(paramMap, "ASSET_04",ref);
        Map bodyMap = new HashMap();
        bodyMap.put("Name", name);
        bodyMap.put("Principal", principal);
        bodyMap.put("Rate", rate);
        bodyMap.put("Type", "车贷");
        bodyMap.put("PayType", "CAPITAL");
        bodyMap.put("InterestDate", interestDate);


        bodyMap.put("EndDate", "");
        //必要
        bodyMap.put("TimeLimit", timeLimit);
        bodyMap.put("Interest", interest);
        bodyMap.put("PayInterestType", payInterestType);
        bodyMap.put("Source", source);
        bodyMap.put("Increase", "");
        //必要
        bodyMap.put("Pledge", pledge);
        bodyMap.put("Value", value);

        bodyMap.put("Lable", "");
        bodyMap.put("Addr", "");
        bodyMap.put("Intro", "");
        //必要
        bodyMap.put("BorrowType", borrowType);
        bodyMap.put("IdentityNum", identityNum);


        bodyMap.put("Profession", "");
        bodyMap.put("Monthincome", "");
        bodyMap.put("Debt", "");
        bodyMap.put("OverdueAmount", "");
        bodyMap.put("OverdueDays", "");

        bodyMap.put("UnsettledAmount", "");
        bodyMap.put("UnsettledSum", "");
        bodyMap.put("PlatformSum", "");
        bodyMap.put("MakerType", "");
        bodyMap.put("MakerName", "");
        bodyMap.put("MakerIdeNum", "");
        bodyMap.put("MakerCard", "");
        bodyMap.put("MakerPhone", "");
        bodyMap.put("MakerAptNo", "");

        paramMap.put("MsgBody", bodyMap);
        String paramXml = MapXmlUtil.createXmlByMap(paramMap, "MsgText");
        logger.info("ASSET_04请求信息:"+paramXml);
        try {
            byte[] param = JTXByteUtil.encrypt(paramXml.getBytes("GBK"), "netwxactive".getBytes("GBK"), "DES");
            String encode_req = new BASE64Encoder().encode(param);
            String result = JTXHttpUtil.sendPost(jtxConfig.getJtxUrl(), encode_req);
            String xml = JTXByteUtil.decrypt(result, "netwxactive", "GBK", "des");
            logger.info("ASSET_04返回信息:" + xml);
            Map map = MapXmlUtil.Xml2Map(xml);
            Map map1 = (Map) map.get("MsgBody");
            if ("0000".equals((String) map1.get("RetCode"))) {
                falg = true;
            }else{
                JtxCommunicationDO jtxCommunicationDO1 = new JtxCommunicationDO();
                jtxCommunicationDO1.setJtxId(ref);
                jtxCommunicationDO1.setUpdateDate(new Date());
                jtxCommunicationDO1.setErrorInfo((String)map1.get("RetCode")+(String)map1.get("RetMsg"));
                jtxCommunicationDOMapper.updateByPrimaryKeySelective(jtxCommunicationDO1);
            }
        } catch (Exception e) {
            logger.error("ASSET_04接口通讯异常", e);
        } finally {
            return falg;
        }
    }

    //ASSET_05
    public boolean enclosureUpdate(String name, String path, String assetSn) {
        boolean falg = false;
        Map<String, Object> paramMap = new HashMap<>();
        headBuild(paramMap, "ASSET_05","");
        Map bodyMap = new HashMap();
        bodyMap.put("FileName", name);
        bodyMap.put("FilePath", path);
        bodyMap.put("AssetSn", assetSn);
        String paramXml = MapXmlUtil.createXmlByMap(paramMap, "MsgText");
        try {
            byte[] param = JTXByteUtil.encrypt(paramXml.getBytes("GBK"), "netwxactive".getBytes("GBK"), "DES");
            String encode_req = new BASE64Encoder().encode(param);
            String result = JTXHttpUtil.sendPost(jtxConfig.getJtxUrl(), encode_req);
            String xml = JTXByteUtil.decrypt(result, "netwxactive", "GBK", "des");
            logger.info("ASSET_05返回信息:" + xml);
            Map map = MapXmlUtil.Xml2Map(xml);
            Map map1 = (Map) map.get("MsgBody");
            if ("0000".equals((String) map1.get("RetCode"))) {
                falg = true;
            }
        } catch (Exception e) {
            logger.error("ASSET_05接口通讯异常", e);
        } finally {
            return falg;
        }
    }

    public void headBuild(Map<String, Object> map, String instrCd,String ref) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss");
        Map headerMap = new HashMap();
        headerMap.put("Ver", "1.0");
        headerMap.put("SysType", "MGR");
        headerMap.put("InstrCd", instrCd);
        headerMap.put("TradSrc", "YUNCHE");
        headerMap.put("SvInst", "ASSET_MANAGE_SYS");
        headerMap.put("Date", sdf.format(date));
        headerMap.put("Time", sdf1.format(date));
        if(ref != null&&!"".equals(ref)) {
            headerMap.put("Ref", ref);
        }else{
            headerMap.put("Ref", "MGR" + System.nanoTime());
        }
        map.put("MsgHdr", headerMap);
    }
}
