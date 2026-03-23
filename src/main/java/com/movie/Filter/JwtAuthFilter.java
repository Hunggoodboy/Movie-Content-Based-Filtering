package com.movie.Filter;

import com.movie.DTO.Response.UserResponse;
import com.movie.Service.Authentication.JwtService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService  jwtService;

    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterchain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader ==  null || !authHeader.startsWith("Bearer ")){
            filterchain.doFilter(request,response);
            return;
        }
        String token = authHeader.substring(7);

        UserResponse userResponse = null;
        try {
            userResponse = jwtService.findUser(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userResponse, List.of(new SimpleGrantedAuthority(userResponse.getRole())));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterchain.doFilter(request,response);
    }
}
