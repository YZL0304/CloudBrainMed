package com.cloudbrainmed.patient.mapper;

import com.cloudbrainmed.patient.entity.Prescription;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrescriptionMapper {

    @Select("SELECT * FROM prescription WHERE register_id = #{registerId}")
    @Results({
        @Result(column = "prescription_id", property = "prescriptionId"),
        @Result(column = "register_id", property = "registerId"),
        @Result(column = "patient_id", property = "patientId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "medicine_name", property = "medicineName"),
        @Result(column = "usage_", property = "usage_"),
        @Result(column = "pay_status", property = "payStatus"),
        @Result(column = "create_time", property = "createTime")
    })
    List<Prescription> selectByRegisterId(@Param("registerId") String registerId);

    @Select("SELECT p.* FROM prescription p INNER JOIN registration r ON p.register_id = r.register_id WHERE r.patient_id = #{patientId} ORDER BY p.create_time DESC")
    @Results({
        @Result(column = "prescription_id", property = "prescriptionId"),
        @Result(column = "register_id", property = "registerId"),
        @Result(column = "patient_id", property = "patientId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "medicine_name", property = "medicineName"),
        @Result(column = "usage_", property = "usage_"),
        @Result(column = "pay_status", property = "payStatus"),
        @Result(column = "create_time", property = "createTime")
    })
    List<Prescription> selectByPatientId(@Param("patientId") String patientId);
}
