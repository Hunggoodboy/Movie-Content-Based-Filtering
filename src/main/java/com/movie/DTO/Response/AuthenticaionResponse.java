package com.movie.DTO.Response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticaionResponse {
    boolean authenticated;
    String token;
}
