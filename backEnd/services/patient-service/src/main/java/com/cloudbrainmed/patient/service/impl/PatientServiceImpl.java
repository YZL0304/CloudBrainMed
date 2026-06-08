package com.cloudbrainmed.patient.service.impl;


import com.cloudbrainmed.patient.service.PatientService;
import org.springframework.stereotype.Service;


import com.cloudbrainmed.patient.dto.authRegisterDto;
import com.cloudbrainmed.patient.dto.authLoginDto;
import com.cloudbrainmed.patient.dto.ChangePasswordDto;
import com.cloudbrainmed.patient.dto.PatientInfoDto;
import com.cloudbrainmed.patient.dto.RegisterQueryDto;
import com.cloudbrainmed.patient.dto.RegisterSubmitDto;

import com.cloudbrainmed.patient.entity.Patient;

import com.cloudbrainmed.patient.mapper.PatientMapper;

import com.cloudbrainmed.patient.vo.AuthLoginVo;
import com.cloudbrainmed.patient.vo.PatientInfoVo;
import com.cloudbrainmed.patient.vo.RegisterVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cloudbrainmed.patient.util.JWTUtil;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 患者Service实现类
 */
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private JWTUtil jwtUtil;

    // ========== 患者基本信息管理 ==========

    @Override
    @Transactional
    public PatientInfoVo  register(authRegisterDto registerDTO) {
        // 1. 创建患者实体
        Patient patient = new Patient();
        patient.setPatientId(generatePatientId());
        patient.setName(registerDTO.getName());
        patient.setGender(registerDTO.getGender());
        patient.setPhone(registerDTO.getPhone());
        patient.setIdCard(registerDTO.getIdCard());
        patient.setAddress(registerDTO.getAddress());
        LocalDate birthday = registerDTO.getBirthdayFromIdCard();
        patient.setBirthday(birthday);
        patient.setPassword(encryptPassword(registerDTO.getPassword()));
        patient.setCreateTime(LocalDateTime.now());
        patient.setUpdateTime(LocalDateTime.now());
        patient.setIsDeleted(0);

        // 2. 保存到数据库
        int result = patientMapper.insert(patient);
        if (result <= 0) {
            return null;
        }

        // 3. 转换为VO返回
        return convertToVO(patient);
    }

    @Override
    public AuthLoginVo login(authLoginDto loginDTO) {
        // 1. 根据手机号查询患者
        Patient patient = patientMapper.selectByPhone(loginDTO.getPhone());
        if (patient == null) {
            return null;
        }

        // 2. 验证密码
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(patient.getPassword())) {
            return null;
        }

        // 3. 更新最后登录时间
        patientMapper.updateLastLoginTime(patient.getPatientId());

        // 4. 生成JWT token
        String token = jwtUtil.generateToken(patient.getPatientId(), patient.getPhone());
        Long expireTime = jwtUtil.getExpirationTime();

        // 5. 返回登录VO
        AuthLoginVo loginVO = new AuthLoginVo();
        loginVO.setPatientId(patient.getPatientId());
        loginVO.setName(patient.getName());
        loginVO.setPhone(patient.getPhone());
        loginVO.setToken(token);
        loginVO.setExpireTime(expireTime);

        return loginVO;
    }

    @Override
    public Patient getPatientInfo(String patientId) {
        return patientMapper.selectById(patientId);
    }

    @Override
    @Transactional
    public boolean updatePatientInfo(PatientInfoDto patientInfoDto) {
        Patient patient = patientMapper.selectById(patientInfoDto.getPatientId());
        if (patient == null) {
            return false;
        }

        patient.setName(patientInfoDto.getName());
        patient.setGender(patientInfoDto.getGender());
        patient.setPhone(patientInfoDto.getPhone());
        patient.setAddress(patientInfoDto.getAddress());
        patient.setBirthday(patientInfoDto.getBirthday());
        patient.setUpdateTime(LocalDateTime.now());

        return patientMapper.update(patient) > 0;
    }

    @Override
    @Transactional
    public boolean changePassword(ChangePasswordDto changePasswordDto) {
        Patient patient = patientMapper.selectById(changePasswordDto.getPatientId());
        if (patient == null) {
            return false;
        }

        // 验证旧密码
        String oldEncrypted = encryptPassword(changePasswordDto.getOldPassword());
        if (!oldEncrypted.equals(patient.getPassword())) {
            return false;
        }

        // 验证新密码和确认密码是否一致
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            return false;
        }

        // 更新密码
        String newEncrypted = encryptPassword(changePasswordDto.getNewPassword());
        return patientMapper.updatePassword(changePasswordDto.getPatientId(), newEncrypted) > 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return patientMapper.selectByPhone(phone) != null;
    }

    @Override
    public boolean checkIdCardExists(String idCard) {
        return patientMapper.selectByIdCard(idCard) != null;
    }

    // ========== 挂号管理 ==========

    @Override
    public List<Map<String, Object>> getAvailableDepts() {
        // 实际应该从DeptMapper查询
        List<Map<String, Object>> depts = new ArrayList<>();
        Map<String, Object> dept1 = new HashMap<>();
        dept1.put("deptId", "D001");
        dept1.put("deptName", "内科");
        dept1.put("deptDesc", "内科科室描述");
        depts.add(dept1);

        Map<String, Object> dept2 = new HashMap<>();
        dept2.put("deptId", "D002");
        dept2.put("deptName", "外科");
        dept2.put("deptDesc", "外科科室描述");
        depts.add(dept2);

        Map<String, Object> dept3 = new HashMap<>();
        dept3.put("deptId", "D003");
        dept3.put("deptName", "儿科");
        dept3.put("deptDesc", "儿科科室描述");
        depts.add(dept3);

        return depts;
    }

    @Override
    public List<Map<String, Object>> getDoctorsByDept(String deptId) {
        // 实际应该从DoctorMapper查询
        List<Map<String, Object>> doctors = new ArrayList<>();
        Map<String, Object> doctor1 = new HashMap<>();
        doctor1.put("doctorId", "DOC001");
        doctor1.put("doctorName", "张医生");
        doctor1.put("title", "主任医师");
        doctor1.put("deptId", deptId);
        doctors.add(doctor1);

        Map<String, Object> doctor2 = new HashMap<>();
        doctor2.put("doctorId", "DOC002");
        doctor2.put("doctorName", "李医生");
        doctor2.put("title", "副主任医师");
        doctor2.put("deptId", deptId);
        doctors.add(doctor2);

        return doctors;
    }

    @Override
    public List<String> getAvailableTimeSlots(String doctorId, String registerDate) {
        // 实际应该查询已预约的时间段，返回可预约的时间段
        List<String> timeSlots = Arrays.asList(
                "08:00-09:00", "09:00-10:00", "10:00-11:00",
                "14:00-15:00", "15:00-16:00", "16:00-17:00"
        );
        return timeSlots;
    }

    @Override
    @Transactional
    public boolean submitRegister(RegisterSubmitDto registerSubmitDto) {
        // 实际应该插入挂号记录到Register表
        // 这里模拟实现
        System.out.println("提交挂号：" + registerSubmitDto.getPatientId() +
                " 医生：" + registerSubmitDto.getDoctorId() +
                " 日期：" + registerSubmitDto.getVisitDate());
        return true;
    }

    @Override
    public List<RegisterVo> getRegisterHistory(RegisterQueryDto registerQueryDto) {
        // 实际应该从RegisterMapper查询
        List<RegisterVo> registers = new ArrayList<>();

        RegisterVo vo1 = new RegisterVo();
        vo1.setRegisterId("REG001");
        vo1.setPatientId(registerQueryDto.getPatientId());
        vo1.setPatientName("张三");
        vo1.setDeptName("内科");
        vo1.setDoctorName("张医生");
        vo1.setRegisterDate("2024-01-15");
        vo1.setTimeSlot("09:00-10:00");
        vo1.setStatus(2); // 2-已完成
        vo1.setStatusText("已完成");
        registers.add(vo1);

        RegisterVo vo2 = new RegisterVo();
        vo2.setRegisterId("REG002");
        vo2.setPatientId(registerQueryDto.getPatientId());
        vo2.setPatientName("张三");
        vo2.setDeptName("外科");
        vo2.setDoctorName("李医生");
        vo2.setRegisterDate("2024-01-20");
        vo2.setTimeSlot("14:00-15:00");
        vo2.setStatus(1); // 1-已预约
        vo2.setStatusText("已预约");
        registers.add(vo2);

        return registers;
    }

    @Override
    @Transactional
    public boolean cancelRegister(String registerId, String patientId) {
        // 实际应该更新Register表的状态
        System.out.println("取消挂号：" + registerId + " 患者：" + patientId);
        return true;
    }

    @Override
    public RegisterVo getRegisterDetail(String registerId, String patientId) {
        // 实际应该从RegisterMapper查询详情
        RegisterVo vo = new RegisterVo();
        vo.setRegisterId(registerId);
        vo.setPatientId(patientId);
        vo.setPatientName("张三");
        vo.setDeptName("内科");
        vo.setDoctorName("张医生");
        vo.setRegisterDate("2024-01-15");
        vo.setTimeSlot("09:00-10:00");
        vo.setFee(50.0);
        vo.setSymptom("头痛、发热");
        vo.setStatus(1);
        vo.setStatusText("已预约");
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    // ========== 病历管理 ==========

    @Override
    public List<Map<String, Object>> getMedicalRecords(String patientId) {
        // 实际应该从MedicalRecordMapper查询
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record1 = new HashMap<>();
        record1.put("recordId", "MR001");
        record1.put("patientId", patientId);
        record1.put("doctorName", "张医生");
        record1.put("diagnosis", "上呼吸道感染");
        record1.put("visitDate", "2024-01-15");
        records.add(record1);
        return records;
    }

    @Override
    public Map<String, Object> getMedicalRecordDetail(String recordId, String patientId) {
        // 实际应该从MedicalRecordMapper查询详情
        Map<String, Object> detail = new HashMap<>();
        detail.put("recordId", recordId);
        detail.put("patientId", patientId);
        detail.put("doctorName", "张医生");
        detail.put("diagnosis", "上呼吸道感染");
        detail.put("symptom", "头痛、发热、咳嗽");
        detail.put("advice", "多休息，多喝水，按时服药");
        detail.put("visitDate", "2024-01-15");
        return detail;
    }

    // ========== 处方管理 ==========

    @Override
    public List<Map<String, Object>> getPrescriptions(String patientId) {
        // 实际应该从PrescriptionMapper查询
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        Map<String, Object> pres1 = new HashMap<>();
        pres1.put("prescriptionId", "PRE001");
        pres1.put("patientId", patientId);
        pres1.put("doctorName", "张医生");
        pres1.put("prescriptionDate", "2024-01-15");
        pres1.put("totalAmount", 156.5);
        prescriptions.add(pres1);
        return prescriptions;
    }

    @Override
    public Map<String, Object> getPrescriptionDetail(String prescriptionId, String patientId) {
        // 实际应该从PrescriptionMapper查询详情
        Map<String, Object> detail = new HashMap<>();
        detail.put("prescriptionId", prescriptionId);
        detail.put("patientId", patientId);
        detail.put("doctorName", "张医生");
        detail.put("prescriptionDate", "2024-01-15");
        detail.put("diagnosis", "上呼吸道感染");

        List<Map<String, Object>> drugs = new ArrayList<>();
        Map<String, Object> drug1 = new HashMap<>();
        drug1.put("drugName", "阿莫西林");
        drug1.put("specification", "0.5g*20粒");
        drug1.put("dosage", "每次1粒，每日3次");
        drug1.put("quantity", 2);
        drug1.put("price", 25.0);
        drugs.add(drug1);

        detail.put("drugs", drugs);
        detail.put("totalAmount", 50.0);
        return detail;
    }

    // ========== 私有辅助方法 ==========

    /**
     * 生成患者ID
     */
    private String generatePatientId() {
        return "p" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 转换为VO
     */
    private PatientInfoVo convertToVO(Patient patient) {
        PatientInfoVo vo = new PatientInfoVo();
        vo.setPatientId(patient.getPatientId());
        vo.setName(patient.getName());

        // 性别转换
        if (patient.getGender() != null) {
            if (patient.getGender() == 1) {
                vo.setGenderText("男");
            } else if (patient.getGender() == 2) {
                vo.setGenderText("女");
            } else {
                vo.setGenderText("未知");
            }
        }

        vo.setPhone(patient.getPhone());
        vo.setAddress(patient.getAddress());
        vo.setCreateTime(patient.getCreateTime());

        // 计算年龄
        if (patient.getBirthday() != null) {
            vo.calculateAge(patient.getBirthday());
        }

        return vo;
    }

}