package org.example.backend.guanliyuan.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 对应 ai_feedback_sample 表（原 TrainingSample）
 */
public class FeedbackSample {
    private String sampleId;
    private String traceId;
    private String aiOutputJson;
    private String finalOutputJson;
    private Integer isAdopted;
    private BigDecimal diffScore;
    private String labelTag;
    private Integer usedForTraining;
    private LocalDateTime createdAt;
    private String doctorId;

    public String getSampleId() { return sampleId; }
    public void setSampleId(String sampleId) { this.sampleId = sampleId; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getAiOutputJson() { return aiOutputJson; }
    public void setAiOutputJson(String aiOutputJson) { this.aiOutputJson = aiOutputJson; }
    public String getFinalOutputJson() { return finalOutputJson; }
    public void setFinalOutputJson(String finalOutputJson) { this.finalOutputJson = finalOutputJson; }
    public Integer getIsAdopted() { return isAdopted; }
    public void setIsAdopted(Integer isAdopted) { this.isAdopted = isAdopted; }
    public BigDecimal getDiffScore() { return diffScore; }
    public void setDiffScore(BigDecimal diffScore) { this.diffScore = diffScore; }
    public String getLabelTag() { return labelTag; }
    public void setLabelTag(String labelTag) { this.labelTag = labelTag; }
    public Integer getUsedForTraining() { return usedForTraining; }
    public void setUsedForTraining(Integer usedForTraining) { this.usedForTraining = usedForTraining; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
}
