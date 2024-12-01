package com.example.springJWT.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {

    private String username;
    private String password; // 그냥 db에 넣으면 안되고 암호화를 진행한 값을 넣어줘야 함
}
