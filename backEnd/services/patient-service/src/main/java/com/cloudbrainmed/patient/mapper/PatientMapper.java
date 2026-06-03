package com.cloudbrainmed.patient.mapper;

import com.cloudbrainmed.patient.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper {

    /**
     * 插入患者
     */
    int insert(Patient patient);

//    /**
//     * 根据ID删除患者（逻辑删除）
//     */
//    @Update("UPDATE patient SET is_deleted = 1, update_time = NOW() WHERE patient_id = #{patientId}")
//    int deleteById(@Param("patientId") String patientId);

    /**
     * 更新患者信息
     */
    int update(Patient patient);

    /**
     * 根据ID查询患者
     */
    @Select("SELECT * FROM patient WHERE patient_id = #{patientId} AND is_deleted = 0")
    Patient selectById(@Param("patientId") String patientId);

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

//    /**
//     * 查询所有患者
//     */
//    @Select("SELECT * FROM patient WHERE is_deleted = 0")
//    List<Patient> selectAll();

    /**
     * 更新密码
     */
    @Update("UPDATE patient SET password = #{password}, update_time = NOW() WHERE patient_id = #{patientId}")
    int updatePassword(@Param("patientId") String patientId, @Param("password") String password);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE patient SET update_time = NOW() WHERE patient_id = #{patientId}")
    int updateLastLoginTime(@Param("patientId") String patientId);
}