package com.cloudbrainmed.patient.service;

import com.cloudbrainmed.patient.dto.RegisterSubmitDto;
import com.cloudbrainmed.patient.entity.Dept;
import com.cloudbrainmed.patient.entity.Doctor;
import com.cloudbrainmed.patient.entity.DoctorSchedule;
import com.cloudbrainmed.patient.entity.Registration;
import com.cloudbrainmed.patient.vo.*;

import java.util.List;

public interface RegisterService {
    List<Dept> getAllDepts();
    List<Doctor> getDoctorsByDept(String deptId);
    DoctorDetailVo getDoctorDetail(String doctorId);
    List<DoctorSchedule> getDoctorSchedules(String doctorId);
    Registration submitRegister(RegisterSubmitDto dto);
    List<Registration> getRegisterHistory(String patientId);
    Registration getRegisterDetail(String registerId);
}
