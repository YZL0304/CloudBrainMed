<template>
  <div class="page">
    <div class="detail-head">
      <el-button text @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回列表</el-button>
      <span class="detail-id">挂号 #{{ registerId }}</span>
    </div>

    <div class="detail-grid">
      <div class="card">
        <div class="card-head">患者信息</div>
        <div class="patient-brief">
          <div class="pb-avatar">{{ (detail.name || detail.patientName)?.charAt(0) || '?' }}</div>
          <div>
            <div class="pb-name">{{ detail.name || detail.patientName || '--' }}</div>
            <div class="pb-meta">{{ detail.gender === 1 ? '男' : '女' }} · {{ detail.patientAge }}岁 · {{ detail.department }}</div>
          </div>
          <span class="pb-status" :class="'st-' + detail.consultStatus">{{ statusLabel(detail.consultStatus) }}</span>
        </div>
        <div class="info-row">
          <div class="info-item"><span class="ii-label">主诉</span><span>{{ detail.chiefComplaint || '--' }}</span></div>
          <div class="info-item"><span class="ii-label">就诊时间</span><span>{{ detail.visitDate }} {{ detail.consultTime }}</span></div>
          <div class="info-item"><span class="ii-label">接诊房间</span><span>{{ detail.consultRoom || '--' }}</span></div>
        </div>
      </div>

      <div class="card">
        <div class="card-head">病历编辑</div>
        <el-input v-model="recordDesc" type="textarea" :rows="10" placeholder="请输入病历内容，包含病史、体格检查、诊断意见等..." class="editor" />
        <div class="actions">
          <el-button @click="handleSaveDraft" :loading="saving" size="large">暂存草稿</el-button>
          <el-button type="success" @click="handleConfirm" :loading="confirming" size="large">确认病历</el-button>
          <el-button type="warning" @click="handleCreateExam" size="large">开具检查单</el-button>
          <el-button type="danger" @click="handleComplete" :loading="completing" size="large" plain>完成接诊</el-button>
        </div>
      </div>

      <div class="card" v-if="showExamDialog">
        <div class="card-head">检查申请单</div>
        <el-form label-position="top">
          <el-row :gutter="16">
            <el-col :span="16">
              <el-form-item label="检查项目"><el-input v-model="checkItemList" type="textarea" :rows="3" placeholder='[{"itemName":"血常规","dept":"检验科"}]' /></el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="紧急程度">
                <el-radio-group v-model="urgencyLevel">
                  <el-radio value="NORMAL">常规</el-radio>
                  <el-radio value="URGENT">加急</el-radio>
                  <el-radio value="EMERGENCY">紧急</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-button type="primary" @click="submitExamOrder" :loading="examLoading" style="margin-top:12px">提交申请</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConsultDetail, saveDraft, confirmRecord, createExamOrder, completeConsult } from '@/api/doctor/consult'

const route = useRoute()
const registerId = route.params.registerId as string
const detail = ref<any>({})
const recordDesc = ref('')
const saving = ref(false); const confirming = ref(false); const completing = ref(false)
const examLoading = ref(false); const showExamDialog = ref(false)
const checkItemList = ref(''); const urgencyLevel = ref('NORMAL')

onMounted(async () => {
  try { const res = await getConsultDetail(registerId); detail.value = res.data; recordDesc.value = res.data.description || '' } catch {}
})

async function handleSaveDraft() {
  saving.value = true
  try { await saveDraft({ registerId, recordDesc: recordDesc.value }); ElMessage.success('草稿已保存') } catch {} finally { saving.value = false }
}
async function handleConfirm() {
  confirming.value = true
  try { await confirmRecord({ registerId, recordDesc: recordDesc.value }); ElMessage.success('病历已确认'); detail.value.consultStatus = 'RECORD_CONFIRMED' } catch {} finally { confirming.value = false }
}
function handleCreateExam() { showExamDialog.value = true }
async function submitExamOrder() {
  examLoading.value = true
  try { await createExamOrder({ registerId, checkItemList: checkItemList.value, urgencyLevel: urgencyLevel.value }); ElMessage.success('检查申请已生成'); showExamDialog.value = false } catch {} finally { examLoading.value = false }
}
async function handleComplete() {
  try { await ElMessageBox.confirm('确定完成本次接诊？完成后不可修改。', '确认', { type: 'warning' }) } catch { return }
  completing.value = true
  try { await completeConsult(registerId); ElMessage.success('接诊已完成'); detail.value.consultStatus = 'COMPLETED' } catch {} finally { completing.value = false }
}
function statusLabel(s: string) { const m: Record<string, string> = { PENDING: '待接诊', IN_PROGRESS: '接诊中', RECORD_CONFIRMED: '已确认', COMPLETED: '已完成' }; return m[s] || s }
</script>

<style scoped>
.page { padding: 28px 36px; }
.detail-head { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.detail-id { font-size: 14px; color: #94a3b8; font-weight: 500; }
.detail-grid { display: flex; flex-direction: column; gap: 20px; }
.card { background: #fff; border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); }
.card-head { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 18px; padding-bottom: 12px; border-bottom: 1px solid #f1f5f9; }

.patient-brief { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.pb-avatar { width: 48px; height: 48px; border-radius: 50%; background: #dbeafe; color: #1d4ed8; font-size: 18px; font-weight: 700; display: flex; align-items: center; justify-content: center; }
.pb-name { font-size: 17px; font-weight: 700; color: #1e293b; }
.pb-meta { font-size: 13px; color: #94a3b8; margin-top: 2px; }
.pb-status { margin-left: auto; font-size: 12px; font-weight: 600; padding: 4px 12px; border-radius: 12px; }
.st-PENDING { background: #fef3c7; color: #b45309; }
.st-IN_PROGRESS { background: #dbeafe; color: #1d4ed8; }
.st-RECORD_CONFIRMED { background: #d1fae5; color: #065f46; }
.st-COMPLETED { background: #f1f5f9; color: #64748b; }

.info-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.info-item { display: flex; flex-direction: column; gap: 4px; font-size: 14px; color: #334155; }
.ii-label { font-size: 12px; color: #94a3b8; font-weight: 600; }

.editor { margin-bottom: 16px; }
.actions { display: flex; gap: 12px; flex-wrap: wrap; }
</style>
