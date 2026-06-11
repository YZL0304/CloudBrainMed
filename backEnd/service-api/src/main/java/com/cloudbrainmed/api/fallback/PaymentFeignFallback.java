package com.cloudbrainmed.api.fallback;

import com.cloudbrainmed.api.feign.PaymentFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class PaymentFeignFallback implements PaymentFeignClient {

    @Override
    public Map<String, Object> getPaymentHistory(String patientId) {
        return Map.of("list", Collections.emptyList(), "total", 0);
    }
}
