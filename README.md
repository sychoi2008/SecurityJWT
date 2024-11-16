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
- 

---
제가 신입 백엔드 개발자를 대상으로 스프링 시큐리티를 이용한 JWT 로그인 구현에 대해 질문한다면, 지원자의 기초적인 이해도, 문제 해결 능력, 실제 프로젝트에서의 적용 경험을 확인하는 데 초점을 맞출 것입니다. 아래는 단계별 질문입니다:

1. 기본 개념 확인
JWT란 무엇인가요?

JWT의 구조와 각 부분의 역할(Header, Payload, Signature)에 대해 설명해보세요.
JWT의 장점과 단점은 무엇인가요?
스프링 시큐리티에서 JWT를 사용하면 어떤 이점이 있나요?

JWT와 세션 기반 인증의 차이점은 무엇인가요?

2. 구현 과정 확인
JWT를 발급하는 과정을 설명해보세요.

사용자가 로그인하면 JWT가 어떻게 생성되고 전달되나요?
JWT 토큰을 검증하는 과정은 어떻게 이루어지나요?

토큰 만료, 위변조 여부를 어떻게 확인했나요?
SecurityFilterChain에서 JWT 인증을 처리하는 필터는 어떻게 동작하나요?

3. 구현 세부사항 확인
JWT 발급 시 어떤 라이브러리를 사용했나요? (e.g., jjwt, nimbus-jose-jwt 등)

JWT 토큰에 어떤 정보를 담았나요?

민감한 정보를 포함하지 않는 이유는 무엇인가요?
Refresh Token을 사용했나요? 사용했다면, Refresh Token과 Access Token의 역할은 어떻게 나눴나요?

SecurityContextHolder는 무엇이고 어떻게 활용했나요?

4. 설계 및 보안 관련 질문
JWT 로그인 구현 시 Stateless한 방식을 유지하기 위해 어떤 설정을 했나요?

예를 들어, http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)와 같은 설정을 언급하세요.
JWT 사용 시 발생할 수 있는 보안 문제는 무엇인가요?

예를 들어, 토큰 탈취(Man-in-the-Middle Attack), 토큰 만료 처리 등.
이를 해결하기 위해 어떤 방법을 사용했나요?
만약 JWT가 탈취되었을 때, 어떤 방식으로 대응할 수 있나요?

5. 실제 경험 확인
JWT 인증 방식을 적용한 프로젝트에 대해 설명해보세요.
프로젝트에서 어떤 요구사항이 있었고, 어떻게 해결했나요?
JWT를 구현하며 어려웠던 점은 무엇인가요?
프로젝트에서 스프링 시큐리티 외에 추가로 사용한 기술이 있다면 무엇이었나요?
6. 문제 해결 능력 확인
JWT를 사용하는 서비스에서 갑자기 토큰 인증이 실패하는 상황이 발생했습니다. 어디서부터 디버깅을 시작하시겠습니까?

사용자가 로그아웃을 요청했습니다. JWT는 Stateless한 방식인데, 어떻게 로그아웃 처리를 구현하시겠습니까?

클라이언트에서 JWT를 로컬 스토리지에 저장하면 어떤 문제가 발생할 수 있나요? 이를 해결할 수 있는 방법은 무엇인가요?

7. 심화 질문
JWT 대신 OAuth2.0을 사용해야 하는 상황은 어떤 경우인가요?
JWT를 Redis와 함께 사용해야 한다면 어떤 이유일까요? 그리고 어떻게 설계할 수 있을까요?
이 질문들을 통해 지원자의 기술적 이해도, 문제 해결 능력, 보안에 대한 민감도, 실제 프로젝트 경험을 다각도로 평가할 수 있습니다.
