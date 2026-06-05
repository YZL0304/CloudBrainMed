package org.example.backend.guanliyuan.service.impl;

import org.example.backend.guanliyuan.entity.FeedbackSample;
import org.example.backend.guanliyuan.entity.InferenceLog;
import org.example.backend.guanliyuan.entity.ModelRegistry;
import org.example.backend.guanliyuan.mapper.MlMapper;
import org.example.backend.guanliyuan.ml.ModelTrainer;
import org.example.backend.guanliyuan.service.MlOpsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MlOpsServiceImpl implements MlOpsService {

    private final MlMapper mlMapper;
    private final ModelTrainer modelTrainer;

    public MlOpsServiceImpl(MlMapper mlMapper, ModelTrainer modelTrainer) {
        this.mlMapper = mlMapper;
        this.modelTrainer = modelTrainer;
    }

    @Override
    public Map<String, Object> getDashboard() {
        int total = mlMapper.countTotalToday();
        int success = mlMapper.countSuccessToday();
        int adopted = mlMapper.countAdoptedToday();
        double avgLatency = mlMapper.avgLatencyToday();
        List<Integer> hourly = mlMapper.hourlyCount();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("todayTotal", total);
        dashboard.put("todaySuccess", success);
        dashboard.put("todayAdopted", adopted);
        dashboard.put("successRate", total > 0 ? String.format("%.2f%%", success * 100.0 / total) : "0.00%");
        dashboard.put("adoptionRate", total > 0 ? String.format("%.2f%%", adopted * 100.0 / total) : "0.00%");
        dashboard.put("avgLatencyMs", String.format("%.1f", avgLatency));
        dashboard.put("hourlyTrend", hourly);
        dashboard.put("models", mlMapper.findModels());
        return dashboard;
    }

    @Override
    public List<InferenceLog> getLogs(int page, int limit) {
        int offset = (page - 1) * limit;
        return mlMapper.findLogs(offset, limit);
    }

    @Override
    public List<FeedbackSample> getSamples(int page, int limit) {
        int offset = (page - 1) * limit;
        return mlMapper.findSamples(offset, limit);
    }

    @Override
    public void labelSample(String sampleId, String labelTag) {
        mlMapper.labelSample(sampleId, labelTag);
    }

    @Override
    public ModelRegistry triggerTraining() {
        return modelTrainer.train();
    }

    @Override
    public void setTraffic(String modelId, int trafficPct) {
        mlMapper.updateTraffic(modelId, trafficPct);
    }

    @Override
    public List<ModelRegistry> getModels() {
        return mlMapper.findModels();
    }
}
