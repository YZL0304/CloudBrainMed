package org.example.backend.yisheng.service;

import org.example.backend.yisheng.entity.Doctor;

public interface DoctorProfileService {
    Doctor getInfo(String doctorId);
    void updateProfile(String doctorId, String goodAt, String introduction, String email);
    String uploadAvatar(String doctorId, byte[] fileBytes, String originalFilename);
    void changePassword(String doctorId, String oldPassword, String newPassword);
    boolean getSetupStatus(String doctorId);
}
