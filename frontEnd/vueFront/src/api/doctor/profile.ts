import request from '../request'

export function getDoctorInfo() {
  return request.get('/api/doctor/profile/info')
}

export function updateDoctorProfile(data: { goodAt: string; introduction: string; email: string }) {
  return request.put('/api/doctor/profile/update', data)
}

export function uploadDoctorAvatar(file: File) {
  const form = new FormData()
  form.append('file', file)
  return request.post('/api/doctor/profile/avatar-upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function changeDoctorPassword(data: { oldPassword: string; newPassword: string }) {
  return request.post('/api/doctor/profile/change-password', data)
}

export function getDoctorSetupStatus() {
  return request.get('/api/doctor/profile/setup-status')
}
