package com.example.springJWT.jwt;

import com.example.springJWT.dto.CustomUserDetails;
import com.example.springJWT.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    // JWT 토큰을 검증하는 클래스
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // HTTP request 의 header를 뽑아 올 것임
        String authorization = request.getHeader("Authorization");

        // 헤더 검증 : 토큰이 없거나 접두사가 이상하거나
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            // 이 필터를 종료하고 다음 필터로 요청을 넘기는 것
            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 분리
        String token = authorization.split(" ")[1];
        System.out.println("token : "+token);

        // 토큰이 존재하지만 유효시간이 지났을 경우
        if(jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            return;
        }

        // 토큰이 있고 유효시간이 지나지 않았을 경우

        // 토큰을 기반으로 일시적인 세션을 생성
        // 그렇다며 현재 요청을 처리할 때 회원정보를 더 쉽고 빠르게 가져오기 위해서
        // 잠시 일시적으로 시큐리티 컨텍스트에 저장하는 거고 요청이 끝나면 소멸
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword"); // 정확한 비밀번호 넣을 필요 없음
        userEntity.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 요청을 다음 필터로 넘겨줌
        filterChain.doFilter(request, response);
    }
}
