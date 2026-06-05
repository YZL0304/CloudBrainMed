package org.example.backend.guanliyuan.entity;

import java.time.LocalDateTime;

/**
 * 对应 ai_model_registry 表（原 ModelVersion）
 */
public class ModelRegistry {
    private String modelId;
    private String modelKey;
    private String version;
    private String artifactPath;
    private String status;
    private Integer trafficPct;
    private LocalDateTime createdAt;
    private LocalDateTime updateTime;

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public String getModelKey() { return modelKey; }
    public void setModelKey(String modelKey) { this.modelKey = modelKey; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getArtifactPath() { return artifactPath; }
    public void setArtifactPath(String artifactPath) { this.artifactPath = artifactPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTrafficPct() { return trafficPct; }
    public void setTrafficPct(Integer trafficPct) { this.trafficPct = trafficPct; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
