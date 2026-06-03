package com.cloudbrainmed.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "admin-service")
public interface AdminFeignClient {
}