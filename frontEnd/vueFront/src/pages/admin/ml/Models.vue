<template>
  <div class="page">
    <header class="page-top">
      <div>
        <h2>模型管理</h2>
        <p class="top-sub">版本注册、流量灰度、训练触发</p>
      </div>
      <el-button type="primary" size="large" @click="handleTrain" :loading="training" round>
        <el-icon><Cpu /></el-icon> 手动触发训练
      </el-button>
    </header>

    <div class="card">
      <div class="card-head">模型版本管理</div>
      <el-table :data="models" stripe v-loading="loading" class="model-table">
        <el-table-column prop="modelId" label="模型 ID" width="220" show-overflow-tooltip />
        <el-table-column prop="modelKey" label="模型标识" min-width="140" />
        <el-table-column prop="version" label="版本号" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <span class="status-tag" :class="'st-' + statusTag(row.status)">{{ row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column label="流量配置" width="300">
          <template #default="{ row }">
            <div class="traffic-cell">
              <el-slider
                v-model="localTraffic[row.modelId]"
                :min="0" :max="100"
                :show-tooltip="false"
                @change="(val: number) => handleTraffic(row.modelId, val)"
              />
              <span class="traffic-val">{{ localTraffic[row.modelId] ?? row.trafficPct }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="artifactPath" label="模型路径" show-overflow-tooltip min-width="180" />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ row.createdAt?.replace('T', ' ') }}</template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && models.length === 0" description="暂无模型，请点击上方训练按钮创建" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { triggerTraining, setModelTraffic, getInferenceStats } from '@/api/admin/ml'

const models = ref<any[]>([])
const loading = ref(false)
const training = ref(false)
const localTraffic = reactive<Record<string, number>>({})

function statusTag(status: string) {
  const map: Record<string, string> = { '上线': 'success', '测试': 'warning', '下线': 'info', '废弃': 'danger' }
  return map[status] || 'info'
}

async function fetchModels() {
  loading.value = true
  try {
    const res = await getInferenceStats()
    models.value = res.data.models || []
    models.value.forEach(m => { localTraffic[m.modelId] = m.trafficPct })
  } finally { loading.value = false }
}

async function handleTrain() {
  training.value = true
  try {
    const res = await triggerTraining()
    ElMessage.success(`模型训练完成，新版本: ${res.data.version}`)
    fetchModels()
  } catch {
    ElMessage.error('训练失败')
  } finally { training.value = false }
}

async function handleTraffic(modelId: string, trafficPct: number) {
  try {
    await setModelTraffic({ modelId, trafficPct })
    ElMessage.success('流量配置已更新')
  } catch {}
}

fetchModels()
</script>

<style scoped>
.page { padding: 28px 36px; }
.page-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; }
.page-top h2 { font-size: 22px; font-weight: 700; color: #0f172a; }
.top-sub { font-size: 13px; color: #94a3b8; margin-top: 4px; }

.card { background: #fff; border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); }
.card-head { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 18px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }

.model-table :deep(th) { background: #f8fafc; color: #64748b; font-weight: 600; font-size: 13px; border-bottom: none; }
.model-table :deep(td) { font-size: 14px; }

.status-tag { font-size: 12px; font-weight: 600; padding: 3px 10px; border-radius: 12px; }
.st-success { background: #d1fae5; color: #065f46; }
.st-warning { background: #fef3c7; color: #b45309; }
.st-info { background: #dbeafe; color: #1d4ed8; }
.st-danger { background: #fee2e2; color: #991b1b; }

.traffic-cell { display: flex; align-items: center; gap: 12px; }
.traffic-cell .el-slider { flex: 1; }
.traffic-val { font-size: 13px; font-weight: 700; color: #2563eb; min-width: 38px; text-align: right; }
</style>
