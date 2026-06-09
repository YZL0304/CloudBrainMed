package com.cloudbrainmed.doctor.service;

import com.cloudbrainmed.doctor.vo.DoctorProfileVo;

public interface DoctorService {
    DoctorProfileVo getDoctorInfo(String doctorId);
    void updateDoctorProfile(String doctorId, String goodAt, String introduction, String email);
}
