## 기존 프로젝트와의 비교
1. 내가 jwt 필수 의존성을 썼구나!
```
implementation 'io.jsonwebtoken:jjwt-api:0.12.3' <br>
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3' 
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```



## 1. Security Config 클래스
- 스프링 시큐리티의 인가 및 설정을 담당하는 클래스
- SecurityFilterChain 메소드
SecurityFilterChain은 Spring Security의 보안 필터 체인을 설정하는 메서드로, 애플리케이션에 적용할 보안 규칙과 필터들의 순서를 정의합니다.

간단히 요약하면:
SecurityFilterChain은 HTTP 요청에 대해 어떤 보안 필터들이 어떤 순서로 적용될지를 지정하여, 인증과 권한을 처리할 수 있도록 합니다.

예시 설명
SecurityFilterChain을 정의할 때는 보통 HttpSecurity를 사용해 보안 설정을 구성하며, 예를 들어 다음과 같은 작업을 할 수 있습니다:

특정 URL에 대한 접근 권한을 설정
로그인 페이지나 로그아웃 기능을 정의
JWT 인증 필터나 CSRF 보호 같은 추가적인 보안 필터를 설정
다음과 같은 코드로 보안 설정을 구성할 수 있습니다:
```
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
            .antMatchers("/public/**").permitAll()  // 공개 경로 설정
            .anyRequest().authenticated()          // 그 외 요청은 인증 필요
        .and()
        .formLogin()
            .loginPage("/login")                   // 커스텀 로그인 페이지
        .and()
        .logout()
            .permitAll();
    return http.build();
}
```
이 메서드는 Spring Security가 요청을 받을 때 정의된 순서대로 보안 필터들을 적용하도록 보안 규칙을 체인 형태로 만들어 반환합니다.


