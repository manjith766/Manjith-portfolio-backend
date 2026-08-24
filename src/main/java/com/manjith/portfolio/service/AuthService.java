package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.LoginRequestDTO;
import com.manjith.portfolio.dto.request.RefreshTokenRequestDTO;
import com.manjith.portfolio.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO requestDTO);

    AuthResponseDTO refresh(RefreshTokenRequestDTO requestDTO);

    void logout(RefreshTokenRequestDTO requestDTO);
}
