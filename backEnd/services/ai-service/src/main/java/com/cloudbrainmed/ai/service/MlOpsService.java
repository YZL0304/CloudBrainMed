package com.cloudbrainmed.ai.service;

import com.cloudbrainmed.ai.entity.FeedbackSample;
import com.cloudbrainmed.ai.entity.InferenceLog;
import com.cloudbrainmed.ai.entity.ModelRegistry;

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
