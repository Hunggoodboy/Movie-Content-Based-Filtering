package com.movie.DTO.Request;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String email;
}
