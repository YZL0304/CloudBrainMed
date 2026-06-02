package com.cloudbrainmed.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "doctor-service")
public interface DoctorFeignClient {
}