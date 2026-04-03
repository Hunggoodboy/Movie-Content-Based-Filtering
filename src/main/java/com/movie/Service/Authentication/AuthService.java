package com.movie.Service.Authentication;

import com.movie.DTO.Request.LoginRequest;
import com.movie.DTO.Request.RegisterRequest;
import com.movie.DTO.Response.AuthenticaionResponse;
import com.movie.DTO.Response.RegisterResponse;
import com.movie.Entity.User;
import com.movie.Repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



    public RegisterResponse register(RegisterRequest request) throws JOSEException {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .displayName(request.getFullName())
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return RegisterResponse.builder().message("Register success.").token(token).build();
    }

    public AuthenticaionResponse login(LoginRequest request) throws JOSEException {
    if(!userRepository.existsByUsername(request.getUsername())){
        return AuthenticaionResponse.builder()
                .authenticated(false)
                .build();
    }

    User user = userRepository.findByUsername(request.getUsername());

    if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
        return AuthenticaionResponse.builder()
                .authenticated(false)
                .build();
    }

    var token = jwtService.generateToken(user);
    return AuthenticaionResponse.builder()
            .authenticated(true)
            .token(token)
            .build();
}
}
