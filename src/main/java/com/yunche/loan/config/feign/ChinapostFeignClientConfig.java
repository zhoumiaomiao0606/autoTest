package com.yunche.loan.config.feign;

import com.yunche.loan.domain.vo.Postcode;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;

@Configuration
public class ChinapostFeignClientConfig {

        @Bean
        public RequestInterceptor chinapostFeignClientRequestInterceptor() {
            return new RequestInterceptor() {
                @Override
                public void apply(RequestTemplate requestTemplate) {
                    requestTemplate.query("m","postsearch");
                    requestTemplate.query("c","index");
                    requestTemplate.query("a","ajax_addr");
                }
            };
        }
}
