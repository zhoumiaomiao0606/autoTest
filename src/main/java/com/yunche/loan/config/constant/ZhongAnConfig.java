package com.yunche.loan.config.constant;

import com.zhongan.scorpoin.common.ZhongAnApiClient;

/**
 * @author liuzhe
 * @date 2019/2/21
 */
public class ZhongAnConfig {

    private static final String version = "1.0.0";

    private static final String env = "prd";
//    private static final String env = "dev";

    // pub
    private static final String appKey = "7e126228ee03c9cd8d3cd868cdb90075";
    // dev
//    private static final String appKey = "dea30205f1356f6d3c99d2a46b786705";

    // pub
    private static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJyYXf+gpY/X/KJ/LuCbR548UXZj5aHpJKm+EeOq0aaq+dlBvgEGy368cmZ2OOTn4hzsXea6oEaEqfw6tRrPumzBd59eV6/8VPIj5dXUc2DrGX0gJXoX5wNDBqUScDn826/oLA65Gz26s+5JHYqIzQtvKhegJ6GC9E53rjTwwgrLAgMBAAECgYAiKB4AvzK9wHfrneBEv7oMas2+DCYvfeIwYDQYu87FEvAykmN2Z9wFReeSL+FTFL0+X9RPmo+RMii9yrRsJ7la3f09wLgLujC7gKCdhZzp+oQ5WDi0t5DUNiVTbhywcQQkeSA48BmnXPIQ8XCdNCzJ7DRlKVzSzG/zla7Okwcu0QJBANaxhnLKC2Hub/h6/vbDLBKykj/eej5yAwa22VzvqsKwGZPvoVKbfL7KQFLiQbOeHWZcdtF3sBAFfpyvErblfUMCQQC6uUeSdnq/P2Jbn+GWyAOSW9pEXcMU3gBqRdimHeR1WNCiRKn/Gn3e5MG5Pl8Bz6M/y0sjW7eeQfXGJO8C1V/ZAkEAnC3aa4jVTPGCXNVEwsfqONPUlkfGz8RqtSiw6O2kYCpxAPAygACCd9xzfJgBSaP9KSicevbBinYky+CEEa7SNwJBAJArSDiso/+QB/hojLxnuGJD61XH8zzkX/ut7CXuhJuaNJRlYcAnCzKS+4R0xNRYJlq2M1Ccmzxk/0e68pQEfZkCQCOIdS1YprMHBIdV8RT/6k/qAvyWSZfScsUShyiBjFD8qoA5HUC21SsVxpZdDuorlhDj+2WGB6xPgu+8VoyFUx4=";
    // dev
//    private static final String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALtzmprQXmbomt9i" +
//            "o0v+m2di/NUrWaPdWEZLGCgM4aZE5zUDeV3Bibmh9Z/Tsr9cDvn/JPqoY1icDpEv" +
//            "ujLe7Fu8uCO5Ao8KwD9XbG210r+oa7PaVHTa4+jl4YVKpRKhj+kkT5bf8Tx8KGX2" +
//            "Dl2ExZphRW7Kk7S9v/CpBbMFpFatAgMBAAECgYAYw5L/NNvj2ILtVAiit4YsSGWC" +
//            "e/GhtlI9JxqP3/PHlX6+ADF+c10Qixb6AGuy4CSSXmSyQKCvSh6ai6WbptuuoJfv" +
//            "aJBUuqVdqCKQSK//wNebf1HD8sat3L2faewLJ4mQOW1cR7cbdIzHTZTa8RxDy0Gr" +
//            "7GJ9yZbPuqN1sANUAQJBAPc75R6EuLCrSct//aCz104nTjpTkAwlM7SlkxOOBRya" +
//            "eyDVdHBGVD4bgOTTqC2Huwpn+TjgNV1Y6WVhxlFu2S0CQQDCGRLE1zZ6bS5kHlYV" +
//            "tCdtCD7dDb96CybedXsg7hlMwhtP5KWvuuYBXq8vB3sm5FBNp9jbX1eEOKBxYGGw" +
//            "zuOBAkAHPjn4Kus+QcZnr9g+XQZxw7UHAGu3718Ua8VjTUXZEK2KyLYgk+7j4upj" +
//            "yc+jhdZ095bVk7v8gB5WWgb1W8oBAkA8CSNcDTFFluXFg/ieh/W17Nn859a0+iQQ" +
//            "pQfrvJnIuzVVdeSlwUqJW+8VvduiwPXxvxv9ZrUcKaO+zdAJr0SBAkAcupbyP51S" +
//            "k/wbdfM7mCcb10o0EU3XbKumbzp4QDNuqdyuC7G9QG1kyLl6CYnm6WHNBFPcja58" +
//            "QVjDO/WUFUnw";

    private static ZhongAnApiClient client = null;


    public static ZhongAnApiClient getClient() {

        if (null == client) {
            client = new ZhongAnApiClient(env, appKey, privateKey, version);
        }

        return client;
    }

}
