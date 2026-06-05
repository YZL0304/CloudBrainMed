package com.cloudbrainmed.ai.mapper;

import org.apache.ibatis.annotations.*;
import com.cloudbrainmed.ai.entity.FeedbackSample;
import com.cloudbrainmed.ai.entity.InferenceLog;
import com.cloudbrainmed.ai.entity.ModelRegistry;

import java.util.List;

@Mapper
public interface MlMapper {

    // ==================== AI 推理统计 ====================
    @Select("SELECT COUNT(*) FROM ai_inference_log WHERE created_at::date = CURRENT_DATE")
    int countTotalToday();

    @Select("SELECT COUNT(*) FROM ai_inference_log WHERE status = '成功' AND created_at::date = CURRENT_DATE")
    int countSuccessToday();

    @Select("SELECT COALESCE(AVG(duration_ms), 0) FROM ai_inference_log WHERE created_at::date = CURRENT_DATE")
    double avgLatencyToday();

    @Select("SELECT COUNT(*) FROM ai_feedback_sample WHERE is_adopted = 1 AND created_at::date = CURRENT_DATE")
    int countAdoptedToday();

    @Select("SELECT COUNT(*) FROM ai_inference_log WHERE created_at::date = CURRENT_DATE GROUP BY EXTRACT(HOUR FROM created_at) ORDER BY EXTRACT(HOUR FROM created_at)")
    List<Integer> hourlyCount();

    // ==================== AI 推理日志 ====================
    @Select("SELECT log_id, trace_id, call_source, model_key, model_version, input_summary, output_summary, status, duration_ms, created_at, patient_id FROM ai_inference_log ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(column = "log_id", property = "logId"),
        @Result(column = "trace_id", property = "traceId"),
        @Result(column = "call_source", property = "callSource"),
        @Result(column = "model_key", property = "modelKey"),
        @Result(column = "model_version", property = "modelVersion"),
        @Result(column = "input_summary", property = "inputSummary"),
        @Result(column = "output_summary", property = "outputSummary"),
        @Result(column = "duration_ms", property = "durationMs"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "patient_id", property = "patientId")
    })
    List<InferenceLog> findLogs(@Param("offset") int offset, @Param("limit") int limit);

    // ==================== AI 反馈样本 ====================
    @Select("SELECT sample_id, trace_id, ai_output_json, final_output_json, is_adopted, diff_score, label_tag, used_for_training, created_at, doctor_id FROM ai_feedback_sample ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(column = "sample_id", property = "sampleId"),
        @Result(column = "trace_id", property = "traceId"),
        @Result(column = "ai_output_json", property = "aiOutputJson"),
        @Result(column = "final_output_json", property = "finalOutputJson"),
        @Result(column = "is_adopted", property = "isAdopted"),
        @Result(column = "diff_score", property = "diffScore"),
        @Result(column = "label_tag", property = "labelTag"),
        @Result(column = "used_for_training", property = "usedForTraining"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "doctor_id", property = "doctorId")
    })
    List<FeedbackSample> findSamples(@Param("offset") int offset, @Param("limit") int limit);

    @Update("UPDATE ai_feedback_sample SET label_tag=#{labelTag}, used_for_training=1 WHERE sample_id=#{sampleId}")
    int labelSample(@Param("sampleId") String sampleId, @Param("labelTag") String labelTag);

    // ==================== AI 模型注册 ====================
    @Select("SELECT model_id, model_key, version, artifact_path, status, traffic_pct, created_at, update_time FROM ai_model_registry ORDER BY created_at DESC")
    @Results({
        @Result(column = "model_id", property = "modelId"),
        @Result(column = "model_key", property = "modelKey"),
        @Result(column = "artifact_path", property = "artifactPath"),
        @Result(column = "traffic_pct", property = "trafficPct"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "update_time", property = "updateTime")
    })
    List<ModelRegistry> findModels();

    @Insert("INSERT INTO ai_model_registry (model_id, model_key, version, artifact_path, status, traffic_pct, created_at, update_time) VALUES (#{modelId}, #{modelKey}, #{version}, #{artifactPath}, '测试', 0, NOW(), NOW())")
    int insertModel(ModelRegistry model);

    @Update("UPDATE ai_model_registry SET traffic_pct=#{trafficPct}, update_time=NOW() WHERE model_id=#{modelId}")
    int updateTraffic(@Param("modelId") String modelId, @Param("trafficPct") int trafficPct);
}
