package com.movie.Service;

import com.movie.DTO.Request.FavouriteMovieRequest;
import com.movie.DTO.Response.ApiResponse;
import com.movie.Entity.User;
import com.movie.Entity.UserVector;
import com.movie.Repository.UserRepository;
import com.movie.Repository.UserVectorRepository;
import com.movie.Service.vectorizer.VectorBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserVectorService {
    private final VectorBuilder  vectorBuilder;
    private final UserVectorRepository userVectorRepository;
    private final UserRepository userRepository;
    public ApiResponse buildUserVector(FavouriteMovieRequest request, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        UserVector userVector = UserVector.builder()
                .user(user)
                .vector(vectorBuilder.buildFavouriteUser(request))
                .interactionCount(1)
                .build();
        userVectorRepository.save(userVector);
        return ApiResponse.builder().message("Bạn đã thêm phim ưu thích thành công").success(true).build();
    }
}
