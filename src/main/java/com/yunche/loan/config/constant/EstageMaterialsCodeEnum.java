package com.yunche.loan.config.constant;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
public enum  EstageMaterialsCodeEnum {
    /**
     * 身份证正面
     */
    SFZZM("sfzzm",new Byte("2")),
    /**
     * 身份证反面
     */
    SFZFM("sfzfm",new Byte("3")),
    /**
     * 授权书
     */
    ZXSQS("zxsqs",new Byte("4")),
    /**
     * 授权书签字照
     */
    SJCXSQS("sjcxsqs",new Byte("5")),
    ;


    String estageMatrCode;
    Byte fileType;

    EstageMaterialsCodeEnum(String estageMatrCode, Byte fileType) {
        this.estageMatrCode = estageMatrCode;
        this.fileType = fileType;
    }

    public static String getMatrCode(Byte fileType) {
        EstageMaterialsCodeEnum[] values = EstageMaterialsCodeEnum.values();
        for(EstageMaterialsCodeEnum value : values) {
            if (value.fileType.equals(fileType)) {
                return value.estageMatrCode;
            }
        }
        return null;
    }
}
