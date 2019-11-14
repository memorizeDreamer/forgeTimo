package com.liufeng.feignclient;

import com.liufeng.BaseResponse.ServerResponse;
import com.liufeng.entity.UserAccount;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.xml.ws.Response;

@FeignClient(name = "user-service", fallbackFactory = AppClient.FeignClientFallbackFactory.class)
public interface AppClient {

    @GetMapping("/iot/app/{APIKey}")
    ServerResponse<UserAccount> findByClientID(@PathVariable("APIKey") String APIKey);

    @Slf4j
    @Component
    class FeignClientFallbackFactory implements FallbackFactory<AppClient> {
        @Override
        public AppClient create(Throwable cause) {
            return app -> ServerResponse.createByError();
        }

    }
}