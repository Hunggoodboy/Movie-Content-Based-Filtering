package com.movie.Controller;

import com.movie.DTO.Request.FavouriteMovieRequest;
import com.movie.DTO.Request.LoginRequest;
import com.movie.DTO.Request.RegisterRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.DTO.Response.AuthenticaionResponse;
import com.movie.DTO.Response.RegisterResponse;
import com.movie.Service.Authentication.AuthService;
import com.movie.Service.Authentication.JwtService;
import com.movie.Service.UserVectorService;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserVectorService  userVectorService;
    private final JwtService  jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticaionResponse> login(@RequestBody LoginRequest loginRequest) throws JOSEException {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) throws JOSEException {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/my-favourite")
    public ResponseEntity<?> myFavourite(@RequestBody FavouriteMovieRequest request, @RequestHeader("Authorization") String authAhead) throws JOSEException, ParseException {
        System.out.println(authAhead);
        String token = authAhead.substring(7);
        UUID userId = UUID.fromString(jwtService.findUser(token).getUserId());
        return ResponseEntity.ok(userVectorService.buildUserVector(request, userId));
    }
}
