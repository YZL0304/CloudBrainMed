package com.cloudbrainmed.payment.service.impl;

import com.cloudbrainmed.payment.entity.PaymentOrder;
import com.cloudbrainmed.payment.mapper.PaymentOrderMapper;
import com.cloudbrainmed.payment.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderMapper paymentOrderMapper;

    public PaymentServiceImpl(PaymentOrderMapper paymentOrderMapper) {
        this.paymentOrderMapper = paymentOrderMapper;
    }

    @Override
    public List<PaymentOrder> getPaymentHistory(String patientId) {
        return paymentOrderMapper.selectByPatientId(patientId);
    }
}
