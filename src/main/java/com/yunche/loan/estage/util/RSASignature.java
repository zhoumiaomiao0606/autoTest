/*
 * huirong Inc.
 * Copyright (c) 2017 All Rights Reserved.
 * Author     :liyb
 * Create Date:2017年6月5日
 */
package com.yunche.loan.estage.util;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA签名验签类
 * <p>私钥签名、公钥验签</p>
 * @author liyb
 * @version RSASignature.java,2017年6月5日 上午10:21:41
 */
public class RSASignature {

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * 字符集编码
     */
    private static final String encode          = "UTF-8";

    /**
     * RSA私钥签名
     * @param data 待签名数据(原数据)
     * @param privateKey 商户私钥
     * @return 签名值
     */
    public static String sign(String data, String privateKey) {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
            Base64Util.base64StringToByte(privateKey));
        KeyFactory keyf = null;
        PrivateKey priKey = null;
        try {
            keyf = KeyFactory.getInstance("RSA");
            priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(data.getBytes(encode));
            byte[] signed = signature.sign();
            return Base64Util.byteToStringBase64(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * RSA公钥验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 分配的商户公钥
     * @return 布尔值
     */
    public static boolean doCheck(String data, String sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64Util.base64StringToByte(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(data.getBytes(encode));
            boolean bool = signature.verify(Base64Util.base64StringToByte(sign));
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
