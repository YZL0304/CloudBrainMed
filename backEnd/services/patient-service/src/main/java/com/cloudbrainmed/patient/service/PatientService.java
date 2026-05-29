package com.cloudbrainmed.patient.service;

import com.cloudbrainmed.patient.dto.ChangePasswordDto;
import com.cloudbrainmed.patient.dto.RegisterSubmitDto;
import com.cloudbrainmed.patient.dto.authRegisterDto;
import com.cloudbrainmed.patient.dto.authLoginDto;
import com.cloudbrainmed.patient.dto.PatientInfoDto;
import com.cloudbrainmed.patient.dto.RegisterQueryDto;
import com.cloudbrainmed.patient.entity.Patient;
import com.cloudbrainmed.patient.vo.RegisterVo;
import com.cloudbrainmed.patient.vo.AuthLoginVo;
import com.cloudbrainmed.patient.vo.PatientInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 患者Service接口
 */
public interface PatientService {

    // ========== 患者基本信息管理 ==========

    /**
     * 患者注册
     */
    PatientInfoVo register(authRegisterDto registerDTO);

    /**
     * 患者登录
     */
    AuthLoginVo login(authLoginDto loginDTO);

    /**
     * 根据ID查询患者信息
     */
    Patient getPatientInfo(String patientId);

    /**
     * 更新患者信息
     */
    boolean updatePatientInfo(PatientInfoDto patientInfoDto);

    /**
     * 修改密码
     */
    boolean changePassword(ChangePasswordDto changePasswordDto);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检查身份证是否存在
     */
    boolean checkIdCardExists(String idCard);

    // ========== 挂号管理 ==========

    /**
     * 获取可预约科室列表
     */
    List<Map<String, Object>> getAvailableDepts();

    /**
     * 根据科室ID获取医生列表
     */
    List<Map<String, Object>> getDoctorsByDept(String deptId);

    /**
     * 获取医生的可预约时间段
     */
    List<String> getAvailableTimeSlots(String doctorId, String registerDate);

    /**
     * 提交挂号
     */
    boolean submitRegister(RegisterSubmitDto registerSubmitDto);

    /**
     * 查询挂号记录
     */
    List<RegisterVo> getRegisterHistory(RegisterQueryDto registerQueryDto);

    /**
     * 取消挂号
     */
    boolean cancelRegister(String registerId, String patientId);

    /**
     * 获取挂号详情
     */
    RegisterVo getRegisterDetail(String registerId, String patientId);

    // ========== 病历管理 ==========

    /**
     * 获取患者的病历记录
     */
    List<Map<String, Object>> getMedicalRecords(String patientId);

    /**
     * 获取病历详情
     */
    Map<String, Object> getMedicalRecordDetail(String recordId, String patientId);

    // ========== 处方管理 ==========

    /**
     * 获取患者的处方记录
     */
    List<Map<String, Object>> getPrescriptions(String patientId);

    /**
     * 获取处方详情
     */
    Map<String, Object> getPrescriptionDetail(String prescriptionId, String patientId);
}