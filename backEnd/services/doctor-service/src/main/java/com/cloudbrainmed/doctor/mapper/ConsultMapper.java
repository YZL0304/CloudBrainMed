package com.cloudbrainmed.doctor.mapper;

import com.cloudbrainmed.doctor.entity.ConsultRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ConsultMapper {

    @Select("<script>" +
        "SELECT r.register_id, r.patient_id, r.doctor_id, r.name, r.gender, r.birthday, " +
        "r.chief_complaint, r.department, r.consult_room, r.visit_date, r.consult_time, " +
        "r.price, r.pay_status, r.consult_status, r.create_time, " +
        "EXTRACT(YEAR FROM AGE(NOW(), r.birthday)) AS patient_age " +
        "FROM registration r " +
        "WHERE r.doctor_id = #{doctorId} " +
        "<if test='consultStatus != null and consultStatus != \"\"'>AND r.consult_status = #{consultStatus}</if> " +
        "<if test='date != null and date != \"\"'>AND r.visit_date = #{date}::date</if> " +
        "ORDER BY r.create_time DESC LIMIT #{limit} OFFSET #{offset}" +
        "</script>")
    @Results({
        @Result(column = "register_id", property = "registerId"),
        @Result(column = "patient_id", property = "patientId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "chief_complaint", property = "chiefComplaint"),
        @Result(column = "consult_room", property = "consultRoom"),
        @Result(column = "visit_date", property = "visitDate"),
        @Result(column = "consult_time", property = "consultTime"),
        @Result(column = "pay_status", property = "payStatus"),
        @Result(column = "consult_status", property = "consultStatus"),
        @Result(column = "create_time", property = "createTime"),
        @Result(column = "patient_age", property = "patientAge")
    })
    List<ConsultRecord> findList(@Param("doctorId") String doctorId,
                                  @Param("consultStatus") String consultStatus,
                                  @Param("date") String date,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    @Select("SELECT r.*, m.record_id, m.doctor_name, m.patient_name, m.visit_age, m.description, " +
        "EXTRACT(YEAR FROM AGE(NOW(), r.birthday)) AS patient_age " +
        "FROM registration r LEFT JOIN medical_record m ON r.register_id = m.register_id " +
        "WHERE r.register_id = #{registerId}")
    @Results({
        @Result(column = "register_id", property = "registerId"),
        @Result(column = "patient_id", property = "patientId"),
        @Result(column = "doctor_id", property = "doctorId"),
        @Result(column = "chief_complaint", property = "chiefComplaint"),
        @Result(column = "consult_room", property = "consultRoom"),
        @Result(column = "visit_date", property = "visitDate"),
        @Result(column = "consult_time", property = "consultTime"),
        @Result(column = "pay_status", property = "payStatus"),
        @Result(column = "consult_status", property = "consultStatus"),
        @Result(column = "create_time", property = "createTime"),
        @Result(column = "record_id", property = "recordId"),
        @Result(column = "doctor_name", property = "doctorName"),
        @Result(column = "patient_name", property = "patientName"),
        @Result(column = "visit_age", property = "visitAge"),
        @Result(column = "patient_age", property = "patientAge")
    })
    ConsultRecord findDetail(@Param("registerId") String registerId);

    @Select("SELECT record_id FROM medical_record WHERE register_id = #{registerId}")
    String findRecordId(@Param("registerId") String registerId);

    @Insert("INSERT INTO medical_record (record_id, patient_id, doctor_id, register_id, doctor_name, patient_name, visit_age, description, visit_date, pay_status) " +
        "VALUES (#{recordId}, #{patientId}, #{doctorId}, #{registerId}, #{doctorName}, #{patientName}, #{visitAge}, #{description}, #{visitDate}, 'UNPAID')")
    int insertRecord(ConsultRecord r);

    @Update("UPDATE medical_record SET description=#{description} WHERE register_id=#{registerId}")
    int updateRecordDesc(@Param("registerId") String registerId, @Param("description") String description);

    @Update("UPDATE registration SET consult_status='IN_PROGRESS' WHERE register_id=#{registerId}")
    int markInProgress(@Param("registerId") String registerId);

    @Update("UPDATE registration SET consult_status='RECORD_CONFIRMED' WHERE register_id=#{registerId}")
    int markRecordConfirmed(@Param("registerId") String registerId);

    @Insert("INSERT INTO check_report (report_id, patient_id, register_id, doctor_id, patient_name, gender, age, check_type, check_item, price, pay_status) " +
        "VALUES (#{reportId}, #{patientId}, #{registerId}, #{doctorId}, #{patientName}, #{gender}, #{age}, #{checkType}, #{checkItem}, #{price}, 'UNPAID')")
    int insertCheckReport(@Param("reportId") String reportId, @Param("patientId") String patientId,
                           @Param("registerId") String registerId, @Param("doctorId") String doctorId,
                           @Param("patientName") String patientName, @Param("gender") Integer gender,
                           @Param("age") Integer age, @Param("checkType") String checkType,
                           @Param("checkItem") String checkItem, @Param("price") BigDecimal price);

    @Update("UPDATE registration SET consult_status='COMPLETED' WHERE register_id=#{registerId}")
    int completeConsult(@Param("registerId") String registerId);
}
