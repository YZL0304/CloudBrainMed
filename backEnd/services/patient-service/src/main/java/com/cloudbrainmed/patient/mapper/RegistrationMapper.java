package com.cloudbrainmed.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbrainmed.patient.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    @Select("SELECT * FROM registration WHERE patient_id = #{patientId} ORDER BY create_time DESC")
    List<Registration> selectByPatientId(@Param("patientId") String patientId);

    @Select("SELECT * FROM registration WHERE register_id = #{registerId}")
    Registration selectByRegisterId(@Param("registerId") String registerId);
}
