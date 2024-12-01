package com.example.springJWT.service;

import com.example.springJWT.dto.JoinDTO;
import com.example.springJWT.entity.UserEntity;
import com.example.springJWT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자를 통한 의존성 주입
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // DB에 해당 아이디의 회원이 이미 테이블에 존재하는가?
        boolean isExist = userRepository.existsByUsername(username);
        // 존재한다면 메서드 종료
        if(isExist) return;

        // 존재하지 않으면 DTO -> Entity로 전환
        UserEntity data = new UserEntity();
        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password)); // 암호화된 비밀번호를 db에 넣기
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
