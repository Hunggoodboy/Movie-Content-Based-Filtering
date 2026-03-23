package com.movie.Controller;

import com.movie.DTO.Request.LoginRequest;
import com.movie.DTO.Request.RegisterRequest;
import com.movie.DTO.Response.AuthenticaionResponse;
import com.movie.DTO.Response.RegisterResponse;
import com.movie.Service.Authentication.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticaionResponse> login(@RequestBody LoginRequest loginRequest) throws JOSEException {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) throws JOSEException {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}
