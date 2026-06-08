package com.cloudbrainmed.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {
}