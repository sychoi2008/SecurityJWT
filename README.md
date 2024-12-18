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

## 2. 로그인 로직
- 로그인 필터 & 로그인 검증 -> 두 가지가 중요
  
- form login을 disable 시켰기 때문에 UsernamePasswordAuthenticationFilter, Authentication Manager을 구현해줘야 한다

<로그인 과정>
1. 사용자의 요청을 Security Filter Chain이 가로채서 적절한 필터에게 요청을 넘긴다.
2. 여러 필터를 거쳐 UsernamePasswordAuthenticationFilter에 요청이 도착하며, 요청에서 id와 pwd를 꺼내 이를 UsernamePasswordAuthenticationToken에 담아 Authentication Manager에게 넘긴다.
3. Authentication Manager는 UserDetailsService를 호출하여, id를 기반으로 사용자 정보를 조회한다.
UserDetailsService는 id를 기반으로 DB에서 회원 정보를 가져와 UserDetails 객체에 담아 Authentication Manager에게 반환한다.
4. Authentication Manager는 요청에서 받은 비밀번호와 UserDetails에 저장된 비밀번호를 비교해 검증한다. 비밀번호가 일치하면, 인증이 완료된 Authentication 객체를 생성하여 UsernamePasswordAuthenticationFilter에게 반환한다.
5. UsernamePasswordAuthenticationFilter는 successfulAuthentication 메서드를 호출하여 인증 성공 처리를 진행한다.


## 3. JWT 검증
<검증 과정>
1. security filter chain 중 JWTFilter가 요청을 가로챔
2. 요청 헤더에서 jwt를 빼서 토큰 검증
3. 유효한 토큰이면 UserDetails로 만들어서 Security Context Holder를 일시적으로 생성
   - 하나의 요청 안에서 회원 정보를 조금 더 효율적으로 접근하기 위해 일시적인 세션(?)을 생성한 것


## 4. Refresh Token을 이용해서 Access token 재발급 과정(간단히)
- 로그인이 완료되면 스프링의 successHandler에서 Access와 Refresh 토큰을 2개 발급해서 응답한다
- 만약, 클라이언트가 보낸 Access Token이 만료가 되었다면 프론트 개발자와 협의해 상태코드와 메세지를 응답함
- 프론트에서는 만료되었다는 response가 오면 예외문을 통해 refresh token을 서버측으로 전송하고 새로운 Access token을 발급받는다(기존 것을 제거)
- 서버 측에서는 refresh token을 받아서 검증하고 새로운 access token을 응답할 로직을 만든다
