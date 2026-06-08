package com.cloudbrainmed.patient.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudbrainmed.common.exception.BusinessException;
import com.cloudbrainmed.patient.dto.RegisterSubmitDto;
import com.cloudbrainmed.patient.entity.*;
import com.cloudbrainmed.patient.mapper.*;
import com.cloudbrainmed.patient.service.RegisterService;
import com.cloudbrainmed.patient.vo.DoctorDetailVo;
import com.cloudbrainmed.patient.vo.ScheduleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final DeptMapper deptMapper;
    private final DoctorMapper doctorMapper;
    private final DoctorScheduleMapper doctorScheduleMapper;
    private final PatientMapper patientMapper;
    private final RegistrationMapper registrationMapper;

    @Override
    public List<Dept> getAllDepts() {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getStatus, 1);
        wrapper.orderByAsc(Dept::getCreateTime);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public List<Doctor> getDoctorsByDept(String deptId) {
        return doctorMapper.selectByDeptId(deptId);
    }

    @Override
    public DoctorDetailVo getDoctorDetail(String doctorId) {
        Doctor doctor = doctorMapper.selectByDoctorId(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        Dept dept = deptMapper.selectById(doctor.getDepartmentId());

        List<DoctorSchedule> schedules = doctorScheduleMapper.selectByDoctorId(doctorId);

        DoctorDetailVo vo = new DoctorDetailVo();
        vo.setDoctorId(doctor.getDoctorId());
        vo.setName(doctor.getName());
        vo.setPosition(doctor.getPosition());
        vo.setGoodAt(doctor.getGoodAt());
        vo.setIntroduction(doctor.getIntroduction());
        vo.setAvatar(doctor.getAvatar());
        vo.setDeptName(dept != null ? dept.getDeptName() : "");
        vo.setSchedules(schedules.stream().map(s -> {
            ScheduleVo scheduleVo = new ScheduleVo();
            scheduleVo.setScheduleId(s.getScheduleId());
            scheduleVo.setTimeJson(s.getTimeJson());
            scheduleVo.setMaxNum(s.getMaxNum());
            scheduleVo.setRemainNum(s.getRemainNum());
            scheduleVo.setPrice(s.getPrice());
            scheduleVo.setRoom(s.getRoom());
            return scheduleVo;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    public List<DoctorSchedule> getDoctorSchedules(String doctorId) {
        return doctorScheduleMapper.selectByDoctorId(doctorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Registration submitRegister(RegisterSubmitDto dto) {
        // 校验患者是否存在
        Patient patient = patientMapper.selectById(dto.getPatientId());
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }

        // 校验医生是否存在
        Doctor doctor = doctorMapper.selectByDoctorId(dto.getDoctorId());
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 校验排班是否存在且有余号
        DoctorSchedule schedule = doctorScheduleMapper.selectByScheduleId(dto.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }
        if (schedule.getRemainNum() <= 0) {
            throw new BusinessException("号源已满");
        }

        // 扣减剩余号源
        int updated = doctorScheduleMapper.decrementRemainNum(dto.getScheduleId());
        if (updated == 0) {
            throw new BusinessException("扣减号源失败，请重试");
        }

        // 创建挂号记录
        Registration registration = new Registration();
        registration.setPatientId(dto.getPatientId());
        registration.setDoctorId(dto.getDoctorId());
        registration.setName(patient.getName());
        registration.setGender(patient.getGender());
        registration.setBirthday(patient.getBirthday());
        registration.setChiefComplaint(dto.getChiefComplaint());
        registration.setDepartment(doctor.getPosition());
        registration.setConsultRoom(schedule.getRoom());
        registration.setVisitDate(dto.getVisitDate());
        registration.setConsultTime(dto.getConsultTime());
        registration.setPrice(schedule.getPrice());
        registration.setPayStatus("WAITING");
        registration.setCreateTime(LocalDateTime.now());

        registrationMapper.insert(registration);

        return registration;
    }

    @Override
    public List<Registration> getRegisterHistory(String patientId) {
        return registrationMapper.selectByPatientId(patientId);
    }

    @Override
    public Registration getRegisterDetail(String registerId) {
        return registrationMapper.selectByRegisterId(registerId);
    }
}
