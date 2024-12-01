package com.example.springJWT.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// refresh token을 DB에 저장할 때 사용될 바구니
@Entity
@Getter @Setter
public class RefreshEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 토큰이 누구 꺼인지 유저 이름
    // 하나의 유저가 여러개의 토큰을 가질 수 있어서 유니크 설정하면 안됨
    private String username;

    private String refresh;

    // 토큰의 만료 시간
    private String expiration;
}
