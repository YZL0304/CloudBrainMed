package com.cloudbrainmed.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbrainmed.patient.entity.Patient;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    @Select("SELECT * FROM patient WHERE patient_id = #{patientId} AND is_deleted = 0")
    Patient selectByPatientId(@Param("patientId") String patientId);

    @Select("SELECT * FROM patient WHERE phone = #{phone} AND is_deleted = 0")
    Patient selectByPhone(@Param("phone") String phone);
}