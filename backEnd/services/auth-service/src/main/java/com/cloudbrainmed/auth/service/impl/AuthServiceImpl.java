package com.cloudbrainmed.auth.service.impl;

import com.cloudbrainmed.auth.dto.authLoginDto;
import com.cloudbrainmed.auth.dto.authRegisterDto;
import com.cloudbrainmed.auth.mapper.PatientUserMapper;
import com.cloudbrainmed.auth.service.AuthService;
import com.cloudbrainmed.common.utils.JwtUtil;
import com.cloudbrainmed.auth.vo.AuthLoginVo;
import com.cloudbrainmed.auth.vo.PatientInfoVo;
import com.cloudbrainmed.patient.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    PatientUserMapper patientUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // ========== 患者基本信息管理 ==========

    @Override
    @Transactional
    public PatientInfoVo register(authRegisterDto registerDTO) {
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
        int result = patientUserMapper.insert(patient);
        if (result <= 0) {
            return null;
        }

        // 3. 转换为VO返回
        return convertToVO(patient);
    }

    @Override
    public AuthLoginVo login(authLoginDto loginDTO) {
        // 1. 参数验证
        if (loginDTO.getPhone() == null || loginDTO.getPhone().trim().isEmpty()) {
            return null;
        }
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return null;
        }

        // 2. 根据手机号查询患者
        Patient patient = patientUserMapper.selectByPhone(loginDTO.getPhone());
        if (patient == null) {
            return null;
        }

        // 3. 验证密码
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(patient.getPassword())) {
            return null;
        }

        // 4. 更新最后登录时间
        patientUserMapper.updateLastLoginTime(patient.getPatientId());

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(patient.getPatientId(), patient.getPhone());
        Long expireTime = jwtUtil.getExpirationTime();

        // 6. 返回登录VO
        AuthLoginVo loginVO = new AuthLoginVo();
        loginVO.setPatientId(patient.getPatientId());
        loginVO.setName(patient.getName());
        loginVO.setPhone(patient.getPhone());
        loginVO.setToken(token);
        loginVO.setExpireTime(expireTime);

        return loginVO;
    }


    @Override
    public boolean checkPhoneExists(String phone) {
        return patientUserMapper.selectByPhone(phone) != null;
    }

    @Override
    public boolean checkIdCardExists(String idCard) {
        return patientUserMapper.selectByIdCard(idCard) != null;
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