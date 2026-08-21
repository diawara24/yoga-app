package com.openclassrooms.starterjwt.services.impl;

import com.openclassrooms.starterjwt.dto.request.LoginRequest;
import com.openclassrooms.starterjwt.dto.request.SignupRequest;
import com.openclassrooms.starterjwt.dto.response.JwtResponse;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.AuthService;
import com.openclassrooms.starterjwt.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = this.jwtUtils.generateToken(userDetails);

        User user = this.userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        boolean isAdmin = user != null && user.isAdmin();

        return new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                isAdmin
        );
    }

    @Override
    public MessageResponse register(SignupRequest signUpRequest) {
        if (Boolean.TRUE.equals(this.userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new BadRequestException("error.email-already-taken");
        }

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .lastName(signUpRequest.getLastName())
                .firstName(signUpRequest.getFirstName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        this.userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }
}
