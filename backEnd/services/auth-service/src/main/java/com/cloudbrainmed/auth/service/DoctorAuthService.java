package com.cloudbrainmed.auth.service;

import com.cloudbrainmed.auth.dto.LoginDto;

public interface DoctorAuthService {
     String login(LoginDto dto);
}