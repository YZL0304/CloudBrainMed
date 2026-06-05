package com.cloudbrainmed.doctor.mapper;

import org.apache.ibatis.annotations.*;
import com.cloudbrainmed.doctor.entity.Doctor;

@Mapper
public interface DoctorMapper {

    @Select("SELECT d.doctor_id, d.avatar, d.name, d.gender, d.phone, d.email, d.position, d.good_at, d.introduction, d.department_id, d.status, d.create_time, t.dept_name FROM doctor d LEFT JOIN department t ON d.department_id = t.dept_id WHERE d.doctor_id = #{doctorId} AND d.is_deleted = 0")
    @Results({
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "department_id", property = "departmentId"),
        @Result(column = "dept_name", property = "deptName"),
        @Result(column = "good_at", property = "goodAt"),
        @Result(column = "create_time", property = "createTime")
    })
    Doctor findById(@Param("doctorId") String doctorId);

    @Update("UPDATE doctor SET good_at=#{goodAt}, introduction=#{introduction}, email=#{email} WHERE doctor_id=#{doctorId}")
    int updateProfile(Doctor doctor);

    @Update("UPDATE doctor SET avatar=#{avatar} WHERE doctor_id=#{doctorId}")
    int updateAvatar(@Param("doctorId") String doctorId, @Param("avatar") String avatar);

    @Select("SELECT password FROM doctor WHERE doctor_id=#{doctorId} AND is_deleted=0")
    String findPasswordById(@Param("doctorId") String doctorId);

    @Update("UPDATE doctor SET password=#{newPassword} WHERE doctor_id=#{doctorId}")
    int updatePassword(@Param("doctorId") String doctorId, @Param("newPassword") String newPassword);
}
