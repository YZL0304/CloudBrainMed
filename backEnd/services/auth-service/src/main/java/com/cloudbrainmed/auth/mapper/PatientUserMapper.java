package com.cloudbrainmed.auth.mapper;

import com.cloudbrainmed.patient.entity.Patient;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PatientUserMapper {
    /**
     * 插入患者
     */
    @Insert("INSERT INTO patient (patient_id, name, gender, phone, id_card, address, birthday, password, create_time) " +
            "VALUES (#{patientId}, #{name}, #{gender}, #{phone}, #{idCard}, #{address}, #{birthday}, #{password}, #{createTime})")
    int insert(Patient patient);

    /**
     * 根据手机号查询患者
     */
    @Select("SELECT * FROM patient WHERE phone = #{phone} AND is_deleted = 0")
    Patient selectByPhone(@Param("phone") String phone);

    /**
     * 根据身份证号查询患者
     */
    @Select("SELECT * FROM patient WHERE id_card = #{idCard} AND is_deleted = 0")
    Patient selectByIdCard(@Param("idCard") String idCard);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE patient SET update_time = NOW() WHERE patient_id = #{patientId}")
    int updateLastLoginTime(@Param("patientId") String patientId);


}
