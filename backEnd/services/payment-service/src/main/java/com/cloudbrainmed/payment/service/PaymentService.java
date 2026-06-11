package com.cloudbrainmed.payment.service;

import com.cloudbrainmed.payment.entity.PaymentOrder;

import java.util.List;

public interface PaymentService {
    /** 查询患者缴费记录 */
    List<PaymentOrder> getPaymentHistory(String patientId);
}
