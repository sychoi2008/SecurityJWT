package com.example.springJWT.dto;

import com.example.springJWT.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    // ROLE 값 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() { // 익명 클래스의 인스턴스가 저장된다. 즉, 참조값이 저장된다
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });


        return collection;

        /*
        LoginFilter에서 해당 메서드를 실행할 때 익명 클래스 내부의 메서드가 실행이 됨
         */
    }

    // password 반환
    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 expired되지 않았다
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
