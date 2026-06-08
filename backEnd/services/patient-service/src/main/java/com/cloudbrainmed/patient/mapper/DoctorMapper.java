package com.cloudbrainmed.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbrainmed.patient.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT d.* FROM doctor d WHERE d.department_id = #{deptId} AND d.status = 1 AND d.is_deleted = 0")
    List<Doctor> selectByDeptId(@Param("deptId") String deptId);

    @Select("SELECT d.* FROM doctor d WHERE d.doctor_id = #{doctorId} AND d.status = 1 AND d.is_deleted = 0")
    Doctor selectByDoctorId(@Param("doctorId") String doctorId);
}
