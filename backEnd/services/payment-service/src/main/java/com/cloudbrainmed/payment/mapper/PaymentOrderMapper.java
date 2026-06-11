package com.cloudbrainmed.payment.mapper;

import com.cloudbrainmed.payment.entity.PaymentOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentOrderMapper {

    @Select("SELECT * FROM payment_order WHERE patient_id = #{patientId} ORDER BY create_time DESC")
    @Results({
        @Result(column = "order_id", property = "orderId"),
        @Result(column = "patient_id", property = "patientId"),
        @Result(column = "register_id", property = "registerId"),
        @Result(column = "pay_type", property = "payType"),
        @Result(column = "pay_status", property = "payStatus"),
        @Result(column = "pay_method", property = "payMethod"),
        @Result(column = "pay_time", property = "payTime"),
        @Result(column = "create_time", property = "createTime")
    })
    List<PaymentOrder> selectByPatientId(@Param("patientId") String patientId);

    @Insert("INSERT INTO payment_order (order_id, patient_id, register_id, pay_type, description, amount, pay_status, create_time) " +
            "VALUES (#{orderId}, #{patientId}, #{registerId}, #{payType}, #{description}, #{amount}, #{payStatus}, #{createTime})")
    int insert(PaymentOrder order);

    @Update("UPDATE payment_order SET pay_status = #{payStatus}, pay_method = #{payMethod}, pay_time = NOW() WHERE order_id = #{orderId}")
    int updatePayResult(@Param("orderId") String orderId, @Param("payStatus") String payStatus, @Param("payMethod") String payMethod);
}
