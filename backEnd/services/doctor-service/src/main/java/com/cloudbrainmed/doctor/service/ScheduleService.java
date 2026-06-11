package com.cloudbrainmed.doctor.service;

import com.cloudbrainmed.doctor.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    List<Schedule> getByDoctorId(String doctorId);
    List<Schedule> getUpcoming(String doctorId);
}
