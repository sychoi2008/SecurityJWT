package com.example.springJWT.jwt;

import com.example.springJWT.dto.CustomUserDetails;
import com.example.springJWT.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    // JWT 토큰을 검증하는 클래스
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        // 권한이 필요없는 요청일 수도 있어서 뒷 필터로 넘긴다
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 있다면
        // 토큰 만료 여부 확인, 만료 시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) { // 만료가 되었다면
            // 응답 메세지
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            // 오류 코드
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 그 다음 필터로 넘기면 안된다
        }

        // 만료가 되지 않은 토큰이라면
        // 토큰이 access 인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) { // access 토큰이 아니라면
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰 검증 완료
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // 현재의 요청에 대해서 회원 정보를 쉽게 접근하기 위해서 일시적인 세션을 형성함
        // 이 세션은 현재 요청이 끝나면 소멸됨
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // 일시적인 세션 생성 -> 그 요청에 대해서 로그인된 상태이다
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
