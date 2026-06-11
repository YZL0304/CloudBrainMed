package com.cloudbrainmed.doctor.service.impl;

import com.cloudbrainmed.doctor.entity.Schedule;
import com.cloudbrainmed.doctor.mapper.ScheduleMapper;
import com.cloudbrainmed.doctor.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public List<Schedule> getByDoctorId(String doctorId) {
        return scheduleMapper.selectByDoctorId(doctorId);
    }

    @Override
    public List<Schedule> getUpcoming(String doctorId) {
        return scheduleMapper.selectUpcoming(doctorId);
    }
}
