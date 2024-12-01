package com.example.springJWT.jwt;

import com.example.springJWT.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{

        // 모든 요청은 이 필터를 지나가기에 로그아웃 요청만 획득해야 함
        // 로그아웃 요청인지 아닌지
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) { // 로그아웃 필터가 아니라면?
            filterChain.doFilter(request, response); // 다음 필터로 넘김
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) { // post 요청이 아니면
            filterChain.doFilter(request, response); // 또 넘겨
            return;
        }

        // 로그아웃 요청인지 아닌지 확인 끝

        // 로그아웃 작업 시작
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) { // 리프레시 토큰을 가져옴
            if(cookie.getName().equals("refresh")) refresh = cookie.getValue();
        }

        if (refresh == null) { // 해당 쿠키에 리프레시 토큰이 없으면?
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 에러 발생
            return;
        }

        // 리프레시 토큰이 만료가 되었는지?
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) { // 이미 로그아웃이 되었다는 뜻
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 활성화가 되어 있으면? 어떤 토큰인지
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) { // 리프레시 토큰이 아니라면
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 토큰이 저장되어 있는지?
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) { // 없다면? 이미 로그아웃된 상태임
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 진짜 진행
        // DB에서 리프레시 토큰을 지움
        refreshRepository.deleteByRefresh(refresh);

        // 쿠키에 있는 리프레시 토큰을 null값으로 바꿈
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
