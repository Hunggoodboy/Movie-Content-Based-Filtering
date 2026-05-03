package com.movie.Service;

import com.movie.DTO.Request.FavouriteSurveyRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.Entity.User;
import com.movie.Entity.UserVector;
import com.movie.Repository.UserRepository;
import com.movie.Repository.UserVectorRepository;
import com.movie.Service.Authentication.JwtService;
import com.movie.Service.vectorizer.VectorBuilder;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserVectorService {
    private final VectorBuilder  vectorBuilder;
    private final UserVectorRepository userVectorRepository;
    private final UserRepository userRepository;
    private final JwtService  jwtService;
    public ApiResponse buildUserVector(FavouriteSurveyRequest request, String authAhead) throws JOSEException, ParseException {
        UUID userId = UUID.fromString(jwtService.findUser(authAhead).getUserId());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserVector userVector = UserVector.builder()
                .user(user)
                .vector(vectorBuilder.buildFavouriteUser(request))
                .interactionCount(1)
                .build();
        userVectorRepository.save(userVector);
        return ApiResponse.builder().message("Bạn đã thêm phim ưu thích thành công").success(true).build();
    }
    public UserVector getUserVector(String authHeader) throws ParseException, JOSEException {
            UUID uuid = UUID.fromString(jwtService.findUser(authHeader).getUserId());
            return userVectorRepository.findById(uuid).orElseGet(() -> {
                System.out.println("Người dùng chưa có uservector");
                User user = userRepository.findById(uuid).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
                UserVector userVector = UserVector.builder()
                        .user(user)
                        .vector(new float[384])
                        .interactionCount(0)
                        .build();
                userVectorRepository.save(userVector);
                return userVector;
            }
        );
    }
}
