package com.cloudbrainmed.ai.ml;

import com.cloudbrainmed.ai.entity.ModelRegistry;
import com.cloudbrainmed.ai.mapper.MlMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 机器学习模型训练器
 * 对应 ai_model_registry 表
 * 后续替换为 SpringAI + PyTorch/CNN 真实训练逻辑
 */
@Component
public class ModelTrainer {

    private final MlMapper mlMapper;

    public ModelTrainer(MlMapper mlMapper) {
        this.mlMapper = mlMapper;
    }

    public ModelRegistry train() {
        String modelId = "MOD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String version = "v" + System.currentTimeMillis();

        ModelRegistry model = new ModelRegistry();
        model.setModelId(modelId);
        model.setModelKey("medical-cnn");
        model.setVersion(version);
        model.setArtifactPath("/models/" + version + ".pt");

        mlMapper.insertModel(model);
        return model;
    }
}
