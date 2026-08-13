package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;


public interface AuthService {
    JwtResponse authenticate(LoginRequest loginRequest);

    MessageResponse register(SignupRequest signUpRequest);
}
