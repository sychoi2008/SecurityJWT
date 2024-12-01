package com.example.springJWT.jwt;

import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    // 객체 타입으로 key를 저장
    private final SecretKey secretKey;

    private JWTUtil(@Value("${jwt.secret}") String secret) {
        // String 으로 받은 키를 객체 타입으로 암호화하기 위한 작업
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // jwt를 parse해서 내부 데이터를 확인한다. ex: jwt가 만료되었는지, username은 무엇인지 등
    public String getUsername(String token) {
        // 토큰이 암호화가 진행되어있으니 우리가 가지고 있는 시크릿 키로 암호화 진행
        // 우리 서버에서 진행되었는지, 맞는지
        // claim 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }


    // UsernamePasswordAuthenticationFilter에서 successfulMethod를 진행 -> 토큰 생성
    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 발행시간
                .expiration(new Date(System.currentTimeMillis()+expiredMs)) // 소멸시간
                .signWith(secretKey) // 암호화 진행
                .compact();
    }
}
