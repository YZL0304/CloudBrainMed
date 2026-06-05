<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const uploading = ref(false)
const result = ref<any>(null)
const selectedFile = ref<File | null>(null)

const API_BASE = 'http://localhost:5000'

function handleFileChange(uploadFile: any) {
  selectedFile.value = uploadFile.raw || uploadFile
}

async function runInference() {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择 CT 文件')
    return
  }
  uploading.value = true
  result.value = null

  try {
    const form = new FormData()
    form.append('file', selectedFile.value)

    const res = await fetch(`${API_BASE}/inference/simple`, {
      method: 'POST',
      body: form,
    })
    const data = await res.json()

    if (data.success) {
      result.value = data
      ElMessage.success('推理完成')
    } else {
      ElMessage.error(data.error || '推理失败')
    }
  } catch (e: any) {
    ElMessage.error('服务未启动，请运行 inference_api.py')
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="ct-inference">
    <div class="page-header">
      <h2>CT 伪影检测</h2>
      <p>上传 CT 影像（.nii.gz），U-Net 3D 模型自动检测伪影区域</p>
    </div>

    <!-- 上传区 -->
    <div class="upload-section">
      <el-upload
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        accept=".nii,.nii.gz"
        :limit="1"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将 CT 文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">支持 .nii.gz / .nii 格式</div>
        </template>
      </el-upload>

      <el-button
        type="primary"
        size="large"
        :loading="uploading"
        @click="runInference"
        :disabled="!selectedFile"
        style="margin-top: 16px; width: 100%"
      >
        {{ uploading ? '推理中...' : '开始检测' }}
      </el-button>
    </div>

    <!-- 结果 -->
    <div v-if="result" class="result-section">
      <el-divider />

      <div class="result-cards">
        <el-card shadow="hover">
          <template #header>阳性像素</template>
          <div class="card-value">{{ result.positive_pixels.toLocaleString() }}</div>
        </el-card>

        <el-card shadow="hover">
          <template #header>总像素</template>
          <div class="card-value">{{ result.total_pixels.toLocaleString() }}</div>
        </el-card>

        <el-card shadow="hover">
          <template #header>伪影占比</template>
          <div class="card-value" :class="{ highlight: result.ratio > 0 }">
            {{ result.ratio }}%
          </div>
        </el-card>
      </div>

      <el-alert
        v-if="result.ratio > 0"
        title="检测到伪影"
        type="warning"
        :description="`在 ${result.total_pixels.toLocaleString()} 个像素中检测到 ${result.positive_pixels.toLocaleString()} 个伪影像素`"
        show-icon
        style="margin-top: 16px"
      />
      <el-alert
        v-else
        title="未检测到伪影"
        type="success"
        description="当前 CT 影像中未发现明显的伪影区域"
        show-icon
        style="margin-top: 16px"
      />
    </div>
  </div>
</template>

<style scoped>
.ct-inference {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}
.page-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}
.page-header p {
  color: #909399;
  font-size: 14px;
}
.upload-section {
  margin-top: 24px;
}
.result-cards {
  display: flex;
  gap: 16px;
}
.result-cards .el-card {
  flex: 1;
  text-align: center;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}
.card-value.highlight {
  color: #E6A23C;
}
</style>
