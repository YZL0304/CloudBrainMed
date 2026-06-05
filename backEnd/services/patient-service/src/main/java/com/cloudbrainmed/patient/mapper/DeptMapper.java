package com.cloudbrainmed.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudbrainmed.patient.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//import org.apache.ibatis.annotations.Mapper;
//
@Mapper
public interface DeptMapper extends BaseMapper<Dept>{
    @Select("SELECT d.*, COUNT(do.doctor_id) as doctor_count " +
            "FROM department d " +
            "LEFT JOIN doctor do ON d.dept_id = do.department_id AND do.is_deleted = 0 " +
            "WHERE d.status = 1 " +
            "GROUP BY d.dept_id " +
            "ORDER BY d.create_time")
    List<Dept> selectDeptWithDoctorCount();
}