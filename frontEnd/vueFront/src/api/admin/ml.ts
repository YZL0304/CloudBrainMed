import request from '../request'

export function getInferenceStats() {
  return request.get('/api/admin/ml/dashboard/inference-stats')
}

export function getInferenceLogs(params: { page: number; limit: number }) {
  return request.get('/api/admin/ml/inference/logs', { params })
}

export function getSampleList(params: { page: number; limit: number }) {
  return request.get('/api/admin/ml/sample/list', { params })
}

export function labelSample(data: { sampleId: string; labelTag: string }) {
  return request.put('/api/admin/ml/sample/label', data)
}

export function triggerTraining() {
  return request.post('/api/admin/ml/model/train')
}

export function setModelTraffic(data: { modelId: string; trafficPct: number }) {
  return request.put('/api/admin/ml/model/traffic', data)
}
