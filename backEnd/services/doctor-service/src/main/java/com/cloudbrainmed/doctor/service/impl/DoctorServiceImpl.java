package com.cloudbrainmed.doctor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudbrainmed.doctor.entity.Dept;
import com.cloudbrainmed.doctor.entity.Doctor;
import com.cloudbrainmed.doctor.mapper.DeptMapper;
import com.cloudbrainmed.doctor.mapper.DoctorMapper;
import com.cloudbrainmed.doctor.service.DoctorService;
import com.cloudbrainmed.doctor.vo.DoctorProfileVo;
import com.cloudbrainmed.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DeptMapper deptMapper;

    @Override
    public DoctorProfileVo getDoctorInfo(String doctorId) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生信息不存在");
        }

        DoctorProfileVo vo = new DoctorProfileVo();
        vo.setName(doctor.getName());
        vo.setPosition(doctor.getPosition());
        vo.setAvatar(doctor.getAvatar());
        vo.setGoodAt(doctor.getGoodAt());
        vo.setIntroduction(doctor.getIntroduction());
        vo.setEmail(doctor.getEmail());

        // 查科室名称
        if (doctor.getDepartmentId() != null) {
            Dept dept = deptMapper.selectById(doctor.getDepartmentId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }

        return vo;
    }

    @Override
    public void updateDoctorProfile(String doctorId, String goodAt, String introduction, String email) {
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生信息不存在");
        }
        if (goodAt != null) doctor.setGoodAt(goodAt);
        if (introduction != null) doctor.setIntroduction(introduction);
        if (email != null) doctor.setEmail(email);
        doctorMapper.updateById(doctor);
    }
}
