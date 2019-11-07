package com.liufeng.feignclient;

import com.liufeng.BaseResponse.ServerResponse;
import com.liufeng.entity.UserAccount;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallbackFactory = UserClient.FeignClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/iot/user/username/{username}")
    ServerResponse<UserAccount> findByUsername(@PathVariable("username") String username);

    @Slf4j
    @Component
    class FeignClientFallbackFactory implements FallbackFactory<UserClient> {
        @Override
        public UserClient create(Throwable cause) {
            return username -> new ServerResponse<>(-1, null);
        }
    }
}