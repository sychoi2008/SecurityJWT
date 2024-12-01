package com.example.springJWT.controller;

import com.example.springJWT.dto.JoinDTO;
import com.example.springJWT.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;


    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) { // 클라이언트에서 JSON 형태가 아닌 폼 데이터(key-value) 형태로 받음
        joinService.joinProcess(joinDTO);
        return "ok";
    }
}
