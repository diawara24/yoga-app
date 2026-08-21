package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.request.LoginRequest;
import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.dto.response.JwtResponse;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;


public interface AuthService {
    JwtResponse authenticate(LoginRequest loginRequest);

    MessageResponse register(SignupRequest signUpRequest);
}
