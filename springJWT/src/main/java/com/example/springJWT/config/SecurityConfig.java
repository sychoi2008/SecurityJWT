package com.example.springJWT.config;

import com.example.springJWT.jwt.JWTFilter;
import com.example.springJWT.jwt.JWTUtil;
import com.example.springJWT.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    // 스프링 시큐리티를 이용해서 회원 가입이나 로그인을 한다면 반드시 비크립트 암호화를 통해 해쉬함수로 비밀번호를 한번 돌려줘야 함 
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain 메서드 :
    // 보안 규칙과 필터의 순서를 지정
    // 정의된 순서대로 보안 필터를 적용함
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                // 허용할 프론트 서버 포트
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드 허용 (get, post 등)
                                configuration.setAllowCredentials(true); //
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;


                            }
                        }));

        http
                .csrf((auth) -> auth.disable()); // jwt는 기본적으로 stateless라 csrf 공격을 방어하지 X. disable해도 됨


        // form login, http basic은 서버 기반이기에 세션 로그인 방식
        http
                .formLogin((auth) -> auth.disable()); // form login은 사용하지 않으므로
        http
                .httpBasic((auth) -> auth.disable()); // http basic 인증 방식 disable

        // 경로에 대한 접근 권한
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "join").permitAll() // 모든 경로 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // 이 경로에서는 ADMIN 역할이어야 함
                        .anyRequest().authenticated()); // 그 이외의 나머지 경로는 로그인한 사람만 접근 가능


        // 우리가 만든 로그인 필터 앞에서 동작
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        // UsernamePasswordAuthenticationFilter 자리에 대체해서 넣을 거기에 addFilterAt 사용
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
