package org.example.backend.guanliyuan.service;

import org.example.backend.guanliyuan.entity.FeedbackSample;
import org.example.backend.guanliyuan.entity.InferenceLog;
import org.example.backend.guanliyuan.entity.ModelRegistry;

import java.util.List;
import java.util.Map;

public interface MlOpsService {
    Map<String, Object> getDashboard();
    List<InferenceLog> getLogs(int page, int limit);
    List<FeedbackSample> getSamples(int page, int limit);
    void labelSample(String sampleId, String labelTag);
    ModelRegistry triggerTraining();
    void setTraffic(String modelId, int trafficPct);
    List<ModelRegistry> getModels();
}
