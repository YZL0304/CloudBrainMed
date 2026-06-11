package com.cloudbrainmed.doctor.mapper;

import com.cloudbrainmed.doctor.entity.Schedule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} ORDER BY work_date ASC, time_slot ASC")
    @Results({
        @Result(column = "schedule_id", property = "scheduleId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "dept_id", property = "deptId"),
        @Result(column = "work_date", property = "workDate"),
        @Result(column = "time_slot", property = "timeSlot"),
        @Result(column = "max_num", property = "maxNum"),
        @Result(column = "remain_num", property = "remainNum"),
        @Result(column = "create_time", property = "createTime")
    })
    List<Schedule> selectByDoctorId(@Param("doctorId") String doctorId);

    @Select("SELECT * FROM doctor_schedule WHERE doctor_id = #{doctorId} AND work_date >= CURRENT_DATE ORDER BY work_date ASC")
    @Results({
        @Result(column = "schedule_id", property = "scheduleId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "dept_id", property = "deptId"),
        @Result(column = "work_date", property = "workDate"),
        @Result(column = "time_slot", property = "timeSlot"),
        @Result(column = "max_num", property = "maxNum"),
        @Result(column = "remain_num", property = "remainNum"),
        @Result(column = "create_time", property = "createTime")
    })
    List<Schedule> selectUpcoming(@Param("doctorId") String doctorId);
}
