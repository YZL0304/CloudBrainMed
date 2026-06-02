package com.cloudbrainmed.api.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "patient-service")
public interface PatientFeignClient {
}