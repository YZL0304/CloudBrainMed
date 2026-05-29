package com.cloudbrainmed.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ai-service")
public interface AiFeignClient {
}