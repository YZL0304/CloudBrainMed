package com.cloudbrainmed.auth.service;

import com.cloudbrainmed.auth.dto.LoginDto;

public interface AuthService {
    String login(LoginDto dto);
}