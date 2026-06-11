package com.cloudbrainmed.ai.service.impl;

import com.cloudbrainmed.ai.entity.ModelVersion;
import com.cloudbrainmed.ai.mapper.AiInferenceLogMapper;
import com.cloudbrainmed.ai.mapper.ModelVersionMapper;
import com.cloudbrainmed.ai.mapper.TrainingSampleMapper;
import com.cloudbrainmed.ai.model.CnnModel;
import com.cloudbrainmed.ai.model.InferenceEngine;
import com.cloudbrainmed.ai.model.ModelLoader;
import com.cloudbrainmed.ai.model.ModelTrainer;
import com.cloudbrainmed.ai.service.MlOpsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MlOpsServiceImpl implements MlOpsService {

    private final AiInferenceLogMapper inferenceLogMapper;
    private final ModelVersionMapper modelVersionMapper;
    private final TrainingSampleMapper sampleMapper;
    private final ModelLoader modelLoader;
    private final ModelTrainer modelTrainer;
    private final InferenceEngine inferenceEngine;

    public MlOpsServiceImpl(AiInferenceLogMapper inferenceLogMapper,
                             ModelVersionMapper modelVersionMapper,
                             TrainingSampleMapper sampleMapper,
                             ModelLoader modelLoader,
                             ModelTrainer modelTrainer,
                             InferenceEngine inferenceEngine) {
        this.inferenceLogMapper = inferenceLogMapper;
        this.modelVersionMapper = modelVersionMapper;
        this.sampleMapper = sampleMapper;
        this.modelLoader = modelLoader;
        this.modelTrainer = modelTrainer;
        this.inferenceEngine = inferenceEngine;
    }

    @Override
    public Map<String, Object> getInferenceStats() {
        return Map.of(
            "todayTotal", inferenceLogMapper.countToday(),
            "successRate", computeSuccessRate(),
            "avgLatency", Math.round(inferenceLogMapper.avgLatency())
        );
    }

    @Override
    public Map<String, Object> getModelStats() {
        return Map.of(
            "activeModels", 1,
            "totalInference", inferenceLogMapper.countAll()
        );
    }

    @Override
    public Map<String, Object> getInferenceLogs(int page, int limit) {
        int offset = (page - 1) * limit;
        return Map.of(
            "list", inferenceLogMapper.selectPage(offset, limit),
            "total", inferenceLogMapper.countAll()
        );
    }

    @Override
    public Map<String, Object> getSampleList(int page, int limit) {
        int offset = (page - 1) * limit;
        return Map.of(
            "list", sampleMapper.selectPage(offset, limit),
            "total", sampleMapper.countAll()
        );
    }

    @Override
    public void updateSample(String sampleId, String label, String labelType) {
        sampleMapper.updateLabel(sampleId, label, labelType, "LABELED");
    }

    @Override
    public List<Map<String, Object>> getModelList() {
        List<ModelVersion> models = modelLoader.listModels();
        List<Map<String, Object>> result = new ArrayList<>();
        for (ModelVersion mv : models) {
            Map<String, Object> item = new HashMap<>();
            item.put("modelId", mv.getModelId());
            item.put("modelKey", mv.getModelKey());
            item.put("modelType", mv.getModelType());
            item.put("version", mv.getVersion());
            item.put("status", mv.getStatus());
            item.put("createTime", mv.getCreateTime());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> triggerTrain(Map<String, String> params) {
        String modelKey = params.getOrDefault("modelKey", "medical-ct-unet");
        String modelType = params.getOrDefault("modelType", "unet");
        String datasetPath = params.getOrDefault("datasetPath", "/data/ct-artifact/");

        CnnModel.ModelType type = "attention".equals(modelType)
                ? CnnModel.ModelType.ATTENTION_UNET : CnnModel.ModelType.UNET;

        ModelTrainer.TrainingTask task = modelTrainer.createTrainingTask(
                modelKey, type, CnnModel.HyperParams.defaultParams(), datasetPath);
        modelTrainer.startTraining(task.getTaskId());

        return Map.of("taskId", task.getTaskId(), "status", task.getStatus());
    }

    @Override
    public Map<String, Object> getTrainingTasks() {
        Map<String, ModelTrainer.TrainingTask> tasks = modelTrainer.listTasks();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ModelTrainer.TrainingTask t : tasks.values()) {
            list.add(Map.of(
                "taskId", t.getTaskId(),
                "modelKey", t.getModelKey(),
                "modelType", t.getModelType(),
                "status", t.getStatus(),
                "createTime", t.getCreateTime()
            ));
        }
        return Map.of("tasks", list, "stats", modelTrainer.getStats());
    }

    @Override
    public Map<String, Object> checkPythonService() {
        boolean alive = inferenceEngine.isPythonServiceAlive();
        ModelVersion active = modelLoader.getActiveModel();
        return Map.of(
            "pythonServiceAlive", alive,
            "activeModel", active != null ? active.getModelKey() + " v" + active.getVersion() : "无"
        );
    }

    private double computeSuccessRate() {
        int total = inferenceLogMapper.countAll();
        if (total == 0) return 100.0;
        // 简化：全部成功的比例（实际应从 DB 查）
        return 95.0;
    }
}
