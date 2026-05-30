package org.example.backend.yisheng.service.impl;

import org.example.backend.common.BusinessException;
import org.example.backend.yisheng.entity.Doctor;
import org.example.backend.yisheng.mapper.DoctorMapper;
import org.example.backend.yisheng.service.DoctorProfileService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private final DoctorMapper doctorMapper;

    public DoctorProfileServiceImpl(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    @Override
    public Doctor getInfo(String doctorId) {
        Doctor d = doctorMapper.findById(doctorId);
        if (d == null) throw new BusinessException("医生不存在");
        d.setPassword(null);
        return d;
    }

    @Override
    public void updateProfile(String doctorId, String goodAt, String introduction, String email) {
        Doctor d = new Doctor();
        d.setDoctorId(doctorId);
        d.setGoodAt(goodAt);
        d.setIntroduction(introduction);
        d.setEmail(email);
        doctorMapper.updateProfile(d);
    }

    @Override
    public String uploadAvatar(String doctorId, byte[] fileBytes, String originalFilename) {
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = "avatar/doctor/" + doctorId + "_" + System.currentTimeMillis() + ext;
        // TODO: 对接 MinIO 后替换
        String avatarUrl = "/files/" + filename;
        doctorMapper.updateAvatar(doctorId, avatarUrl);
        return avatarUrl;
    }

    @Override
    public void changePassword(String doctorId, String oldPassword, String newPassword) {
        String stored = doctorMapper.findPasswordById(doctorId);
        if (stored == null) throw new BusinessException("医生不存在");
        String oldEncrypted = DigestUtils.md5DigestAsHex(oldPassword.getBytes(StandardCharsets.UTF_8));
        if (!stored.equals(oldEncrypted)) throw new BusinessException("原密码不正确");
        String newEncrypted = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        doctorMapper.updatePassword(doctorId, newEncrypted);
    }

    @Override
    public boolean getSetupStatus(String doctorId) {
        Doctor d = doctorMapper.findById(doctorId);
        if (d == null) throw new BusinessException("医生不存在");
        // good_at 或 introduction 为空时认为资料未完善
        return d.getGoodAt() == null || d.getGoodAt().isEmpty()
            || d.getIntroduction() == null || d.getIntroduction().isEmpty();
    }
}
