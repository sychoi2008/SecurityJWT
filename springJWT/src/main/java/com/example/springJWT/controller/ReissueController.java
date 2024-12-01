package com.example.springJWT.controller;

import com.example.springJWT.entity.RefreshEntity;
import com.example.springJWT.jwt.JWTUtil;
import com.example.springJWT.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 요청 쿠키에서 refresh token 에서 뽑기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) { // refresh token 찾기
            if(cookie.getName().equals("refresh")) refresh = cookie.getValue();
        }

        // 만약 refresh token이 없다면?
        if(refresh == null) return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);

        // refresh token이 만료가 되었는가?
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh 인가?
        String catgory = jwtUtil.getCategory(refresh);

        // 페이로드의 값이 리프레시가 아니면
        if(!catgory.equals("refresh")) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        // DB에 리프레시 토큰이 저장되어 있는지?
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        // 없다면 오류
        if(!isExist) return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);


        // 모든 검증 끝
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새로운 access token 생성 & Refresh token rotate
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // 새로운 refresh token을 만들었기에 기존의 refresh token을 DB에서 지워야 함
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);


        // 응답헤더에 새로운 access token을 헤더에 넣기
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        // 응답코드와 함께 전송
        return new ResponseEntity<>(HttpStatus.OK);

        /*
        ResponseEntity와 HttpServletResponse를 둘 다 사용
        결국 ResponseEntity에서 조금 더 정밀하게 response를 만들고,
         최종적으로 스프링에서 다듬을 때에는 그 ResponseEntity를 기반으로 이미 메서드 내부에서 만들어진 HttpServletResponse에 추가해서
         클라이언트에 응답한다는 것
         */

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키 생명 주기
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
