package com.cloudbrainmed.ai.model;

import com.cloudbrainmed.ai.entity.ModelVersion;
import com.cloudbrainmed.ai.mapper.ModelVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型训练器：管理训练任务的生命周期
 *
 * 职责：
 * - 创建训练任务
 * - 跟踪训练状态（PENDING → RUNNING → COMPLETED/FAILED）
 * - 训练完成后注册模型到 ModelLoader
 */
@Component
public class ModelTrainer {

    private static final Logger log = LoggerFactory.getLogger(ModelTrainer.class);

    private final ModelVersionMapper modelVersionMapper;
    private final ModelLoader modelLoader;

    /** 训练任务状态缓存 */
    private final ConcurrentHashMap<String, TrainingTask> tasks = new ConcurrentHashMap<>();

    public ModelTrainer(ModelVersionMapper modelVersionMapper, ModelLoader modelLoader) {
        this.modelVersionMapper = modelVersionMapper;
        this.modelLoader = modelLoader;
    }

    /**
     * 创建训练任务
     */
    public TrainingTask createTrainingTask(String modelKey, CnnModel.ModelType modelType,
                                            CnnModel.HyperParams params, String datasetPath) {
        String taskId = "TRN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        TrainingTask task = new TrainingTask();
        task.setTaskId(taskId);
        task.setModelKey(modelKey);
        task.setModelType(modelType.getKey());
        task.setHyperParams(params);
        task.setDatasetPath(datasetPath);
        task.setStatus("PENDING");
        task.setCreateTime(LocalDateTime.now());

        tasks.put(taskId, task);
        log.info("训练任务已创建: {} [{}] → 超参: lr={}, epochs={}, batch={}",
                taskId, modelType.getKey(),
                params.getLearningRate(), params.getEpochs(), params.getBatchSize());
        return task;
    }

    /**
     * 启动训练（异步）
     */
    public void startTraining(String taskId) {
        TrainingTask task = tasks.get(taskId);
        if (task == null) throw new RuntimeException("训练任务不存在: " + taskId);

        task.setStatus("RUNNING");
        task.setStartTime(LocalDateTime.now());
        log.info("训练已启动: {}", taskId);

        // TODO: 实际启动 Python 训练脚本（通过 ProcessBuilder 或消息队列）
        // 当前模拟异步执行
        new Thread(() -> simulateTraining(taskId)).start();
    }

    /**
     * 获取训练任务状态
     */
    public TrainingTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * 查询所有训练任务
     */
    public Map<String, TrainingTask> listTasks() {
        return Map.copyOf(tasks);
    }

    /**
     * 获取模型训练统计
     */
    public Map<String, Object> getStats() {
        long pending = tasks.values().stream().filter(t -> "PENDING".equals(t.getStatus())).count();
        long running = tasks.values().stream().filter(t -> "RUNNING".equals(t.getStatus())).count();
        long completed = tasks.values().stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long failed = tasks.values().stream().filter(t -> "FAILED".equals(t.getStatus())).count();

        return Map.of(
            "pending", pending,
            "running", running,
            "completed", completed,
            "failed", failed,
            "totalModels", modelVersionMapper.countAll()
        );
    }

    /**
     * 模拟训练过程（后续替换为真实 Python 训练脚本调用）
     */
    private void simulateTraining(String taskId) {
        try {
            TrainingTask task = tasks.get(taskId);
            // 模拟训练耗时
            Thread.sleep(3000);

            // 构造模型版本信息
            String version = "v" + (System.currentTimeMillis() / 1000);
            String artifactPath = "/models/" + task.getModelKey() + "/" + version + ".pth";

            ModelVersion mv = modelLoader.registerModel(
                    task.getModelKey(), version, artifactPath,
                    CnnModel.ModelType.UNET, task.getHyperParams()
            );
            modelLoader.activateModel(mv.getModelId());

            task.setStatus("COMPLETED");
            task.setCompletedTime(LocalDateTime.now());
            task.setModelId(mv.getModelId());
            log.info("训练完成: {} → 模型 {}", taskId, mv.getModelId());
        } catch (Exception e) {
            TrainingTask task = tasks.get(taskId);
            if (task != null) {
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
            }
            log.error("训练失败: {} → {}", taskId, e.getMessage());
        }
    }

    // ==================== 内部类 ====================

    /**
     * 训练任务实体
     */
    public static class TrainingTask {
        private String taskId;
        private String modelKey;
        private String modelType;
        private CnnModel.HyperParams hyperParams;
        private String datasetPath;
        private String status;
        private String modelId;
        private String errorMessage;
        private LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime completedTime;

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getModelKey() { return modelKey; }
        public void setModelKey(String modelKey) { this.modelKey = modelKey; }
        public String getModelType() { return modelType; }
        public void setModelType(String modelType) { this.modelType = modelType; }
        public CnnModel.HyperParams getHyperParams() { return hyperParams; }
        public void setHyperParams(CnnModel.HyperParams hyperParams) { this.hyperParams = hyperParams; }
        public String getDatasetPath() { return datasetPath; }
        public void setDatasetPath(String datasetPath) { this.datasetPath = datasetPath; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getCompletedTime() { return completedTime; }
        public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    }
}
