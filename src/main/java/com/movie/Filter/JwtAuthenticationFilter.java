package com.movie.Filter;

import com.movie.DTO.Response.UserResponse;
import com.movie.Service.Authentication.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy token từ Header
        final String authHeader = request.getHeader("Authorization");

        // 2. Nếu không có header hoặc không bắt đầu bằng "Bearer ", bỏ qua cho đi tiếp (Spring Security sẽ tự chặn lại sau vì két sắt trống)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Cắt bỏ chữ "Bearer " (7 ký tự)
            final String token = authHeader.substring(7);

            // 4. Giải mã token bằng hàm của bạn!
            UserResponse userResponse = jwtService.findUser(token);

            // 5. Nếu token hợp lệ và SecurityContext chưa có ai đăng nhập
            if (userResponse != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Tạo Role cho user (Spring Security thường yêu cầu prefix "ROLE_")
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userResponse.getRole());

                // MẸO: Lưu luôn userId vào mục Principal để lát nữa ra Controller lấy cho tiện
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userResponse.getUserId(), // principal
                        null,                     // credentials (không cần mật khẩu nữa)
                        Collections.singletonList(authority) // roles
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. CẤT VÀO KÉT SẮT SECURITY CONTEXT!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Token hết hạn, chữ ký sai... thì lờ đi, két sắt trống rỗng thì Spring sẽ tự văng lỗi 403/401
            System.out.println("JWT Invalid: " + e.getMessage());
        }

        // 7. Cho request đi tiếp vào Controller
        filterChain.doFilter(request, response);
    }
}