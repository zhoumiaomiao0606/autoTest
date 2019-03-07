package com.yunche.loan.estage.util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA工具类提供加密，解密，生成密钥对等方法。
 * <p>公钥加密、私钥解密</p>
 * @author liyb
 * @version RSAUtil.java,2017年6月5日 上午10:37:53
 */
public class RSAUtil {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
    
    /**
     * 字符集编码
     */
    private static final String ENCODE = "UTF-8";
    
    /** 
     * RSA最大加密明文大小 
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
  
    /** 
     * RSA最大解密密文大小 
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    /** 
     * 获取公钥的key 
     */  
    private static final String PUBLIC_KEY = "publicKey";  
  
    /** 
     * 获取私钥的key 
     */  
    private static final String PRIVATE_KEY = "privateKey";
    
//    private static final String RSANOPADDING = "RSA/ECB/NoPadding";
    
    /**
     * 生成密钥对(公钥和私钥)
     * @return
     */
    public static Map<String, Object> genKeyPair(){
        String pubKey = "";
        String prvKey = "";
        Map<String, Object> keyMap = new HashMap<String, Object>();
        try {
            KeyPair keypair = RSAUtil.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keypair.getPublic();
            RSAPrivateKey privateKey =  (RSAPrivateKey) keypair.getPrivate();
            pubKey = Base64Util.byteToStringBase64(publicKey.getEncoded());
            prvKey = Base64Util.byteToStringBase64(privateKey.getEncoded());
//            pubKey = Base64.encodeBase64String(publicKey.getEncoded());
//            prvKey = Base64.encodeBase64String(privateKey.getEncoded());
            keyMap.put(PUBLIC_KEY, pubKey);
            keyMap.put(PRIVATE_KEY, prvKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyMap;
    }
    
    /**
     * 生成密钥对 
     * @return KeyPair
     */
    private static KeyPair generateKeyPair() throws Exception {
        try {
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",new BouncyCastleProvider());
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            final int KEY_SIZE = 1024;//这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
//            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            keyPairGen.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return keyPair;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param publicKey 公钥
     * @return
     */
    private static byte[] encryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] encryptedData = data.getBytes(ENCODE);
        byte[] keyBytes = Base64Util.base64StringToByte(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
//        Cipher cipher = Cipher.getInstance(RSANOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        //对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();  
        out.close();
        return decryptedData;
    }
    
    /**
     * 私钥解密
     * @param data 已加密数据
     * @param privateKey 私钥
     * @return
     */
    private static byte[] decryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] encryptedData = Base64.decodeBase64(data);
        byte[] keyBytes = Base64Util.base64StringToByte(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm()); 
//        Cipher cipher = Cipher.getInstance(RSANOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;
        //对数据分段解密  
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }  
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param publicKey 商户公钥
     * @return 返回加密后数据
     */
    public static String encrypt(String data,String publicKey){
        String encryptString = "";
        if(StringUtils.isNotEmpty(data)){
            try {
                byte[] encryptData = encryptByPublicKey(data, publicKey);
                encryptString = Base64.encodeBase64String(encryptData);
                return encryptString;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return encryptString;
    }
    
    /**
     * 私钥解密
     * @param data 加密后数据
     * @param privateKey 商户私钥
     * @return
     */
    public static String decrypt(String data,String privateKey){
        String decryptString = "";
        try {
            byte[] decryptData = decryptByPrivateKey(data, privateKey);
            decryptString = new String(decryptData,ENCODE);
            return decryptString;
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("RSA decrypt error:"+e.getMessage());
        }
        return decryptString;
    }
}