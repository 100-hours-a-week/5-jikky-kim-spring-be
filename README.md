# **프로젝트 소개**

기본적인 게시판의 형태를 띄고 있는 커뮤니티 프로젝트로 기술간의 장단점을 체감하기 위해 버전에 따라 다른 기술로 구현

-   ver1 : `vanila` `express` `json`
    -   [🔗FE Github](https://github.com/jjikky/5-jikky-kim-vanila-fe)
    -   [🔗BE Github](https://github.com/jjikky/5-jikky-kim-express-be/tree/json-archive)
-   ver2 : `react` `express` `mySQL`

    -   [🔗FE Github](https://github.com/jjikky/5-jikky-kim-react-fe/tree/with-express)
    -   [🔗BE Github](https://github.com/jjikky/5-jikky-kim-express-be)

-   ver3 : `react` `spring` `mySQL`

    -   [🔗FE Github](https://github.com/jjikky/5-jikky-kim-react-fe)
    -   [🔗BE Github](https://github.com/jjikky/5-jikky-kim-spring-be)

# 커뮤니티 게시판 ver 3 BE

## 사용 기술

`Spring Boot` `mySQL`

## 시연 영상

https://github.com/100-hours-a-week/5-jikky-kim-react-fe/assets/59151187/6a78967d-4bb8-4ebd-920d-f7e8b4000ff8



## Github

**FE**

[GitHub - jjikky/5-jikky-kim-react-fe: Dev Word FE : 커뮤니티 게시판 + 개발 용어 한국어 발음 검색](https://github.com/jjikky/5-jikky-kim-react-fe)

**BE**

[GitHub - jjikky/5-jikky-kim-spring-be](https://github.com/jjikky/5-jikky-kim-spring-be)

## 목표

- 커뮤니티 및 개발 용어 발음 검색 서비스 개발을 통한 스프링 학습 및 이해
    - JWT 기반 인증 구현
    - jdbcTemplate 적용

## JWT

<aside>
💡 Spring Security와 JWT를 활용하여 사용자 인증을 처리

</aside>

### JwtRequestFilter

- Spring Security에서 요청당 한 번 실행되는 필터
- HTTP 요청 헤더에서 JWT를 추출하고 검증하여 유효한 경우 인증 정보를 설정하여 Spring Security의 Security Context에 사용자 정보를 저장
- JwtRequestFilter **코드**
    
    ```java
    @Component
    public class JwtRequestFilter extends OncePerRequestFilter {
        @Autowired
        private CustomUserDetailsService userDetailsService;
    
        @Autowired
        private JwtTokenUtil jwtTokenUtil;
    
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            final String requestTokenHeader = request.getHeader("Authorization");
    
            String userEmail = null;
            String jwtToken = null;
    
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    userEmail = jwtTokenUtil.getUserEmailFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    logger.warn("Unable to get JWT Token", e);
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token has expired", e);
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
            logger.info("userEmail: " + userEmail);
            logger.info("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
    
            // 토큰을 받았으면 유효성 검사를 합니다.
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
    
                // 토큰이 유효하면 수동으로 인증을 설정합니다.
                if (jwtTokenUtil.validateToken(jwtToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            chain.doFilter(request, response);
        }
    }
    ```
    

### JwtTokenUtil

- 토큰을 생성하고 검증하는 데 필요한 메서드 제공
- 주요 메서드
    - `generateToken(String username, Long userId)`:
        - 주어진 `username`과 `userId`를 포함한 JWT 생성
    - `doGenerateToken(Map<String, Object> claims, String subject)`:
        - 주어진 클레임과 주제를 사용하여 JWT  생성. 서명에는 `HS512` 알고리즘과 비밀 키가 사용
    - `validateToken(String token, String username)`:
        - 토큰의 사용자 이름과 만료 여부를 확인하여 토큰의 유효성을 검사
    - `getUserEmailFromToken(String token)`, `getUserIdFromToken(String token)`, `getUsernameFromToken(String token)`:
        - 토큰에서 사용자 이메일, 사용자 ID, 사용자 이름을 추출
    - `getExpirationDateFromToken(String token)`:
        - 토큰의 만료 날짜를 추출
    - `getClaimFromToken(String token, Function<Claims, T> claimsResolver)`:
        - 토큰에서 특정 클레임을 추출
    - `getAllClaimsFromToken(String token)`:
        - 토큰에서 모든 클레임을 파싱하여 반환
    - `isTokenExpired(String token)`:
        - 토큰이 만료되었는지 여부를 확인
- JwtTokenUtil 코드
    
    ```java
    @Component
    public class JwtTokenUtil {
    
        @Value("${jwt.secret}")
        private String secret;
    
        @Value("${jwt.expiration}")
        private long expiration;
    
        public String generateToken(String username,Long userId) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", userId);
            return doGenerateToken(claims, username);
        }
    
        private String doGenerateToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        }
    
        public Boolean validateToken(String token, String username) {
            final String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        }
    
        public String getUserEmailFromToken(String token) {
            return getClaimFromToken(token, Claims::getSubject);
        }
    
        public Long getUserIdFromToken(String token) {
            final Claims claims = getAllClaimsFromToken(token);
            return claims.get("user_id", Long.class);
        }
    
        public String getUsernameFromToken(String token) {
            return getClaimFromToken(token, Claims::getSubject);
        }
    
        public Date getExpirationDateFromToken(String token) {
            return getClaimFromToken(token, Claims::getExpiration);
        }
    
        public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        }
    
        private Claims getAllClaimsFromToken(String token) {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }
    
        private Boolean isTokenExpired(String token) {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        }
    }
    ```
    

### 동작 과정

- **요청 필터링**:
    - 클라이언트의 요청에 포함된 `Authorization` 헤더에서 JWT를 추출
    - 토큰이 유효한 경우, `JwtTokenUtil`을 사용하여 사용자 정보를 로드하고 인증을 설정
- **토큰 검증**:
    - `JwtTokenUtil`을 사용하여 토큰의 유효성을 검사
    - 유효한 경우, 사용자 인증 정보를 `SecurityContext`에 설정하여 보호된 리소스에 대한 접근을 허용
- **인증 설정**:
    - `UsernamePasswordAuthenticationToken`을 사용하여 사용자 인증을 설정하고, 이를 `SecurityContext`에 저장하여 이후의 인증 처리를 가능하게

## 유저 기능

### 회원가입 : [ POST ] /users/register

![image](https://github.com/user-attachments/assets/1880feb9-e9b0-42cf-b602-4b3915f19b46)

서버에 유저 프로필 저장

![image](https://github.com/user-attachments/assets/de018528-27e1-486d-955c-ea428aa081dc)

[DB에 유저 생성 완료

![image](https://github.com/user-attachments/assets/0447da62-6363-45de-97c6-17b55c6c942f)


### 로그인 : [ POST ] /users/login

![image](https://github.com/user-attachments/assets/186854f8-9bda-49d4-bd3f-bd43fae9274d)


### 닉네임 중복 체크 : [ GET ] /users/nickname/check

해당 닉네임을 보유한 유저가 없는 경우

![image](https://github.com/user-attachments/assets/378df8be-847b-4d2c-a248-eea56b6374d9)


해당 닉네임을 보유한 유저가 있는 경우

![image](https://github.com/user-attachments/assets/ab493b3b-0dad-47ef-8268-7df20c3a4848)


### 이메일 중복 체크 : [ GET ] /users/email/check

해당 이메을 보유한 유저가 없는 경우

![image](https://github.com/user-attachments/assets/f3cb4c41-73c3-4e3b-b47c-9ac791a75b21)


해당 이메일을 보유한 유저가 있는 경우

![image](https://github.com/user-attachments/assets/edf22476-e173-4a25-9d75-36081b85e76f)


### 유저 정보 조회: [ GET ] /users

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/755e4bce-4ae4-47c2-b1d6-0d90628db2bf)


### 유저 정보수정: [ PATCH ] /users

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/3ff26cfc-28d2-4c02-b824-1aa114c9a218)


### 비밀번호변경: [ PATCH ] /users/password/change

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/1438e75a-9fc7-4a5b-ad2b-d5f680651d95)


### 회원 탈퇴: [ DELETE ] /users

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/fb001ec8-7c89-452c-b9e5-0760625ae27f)


## 게시판 기능

### 게시글 목록 조회 : [ GET ] /posts

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/6d642183-1c0a-460a-bd49-36057b8bf687)

### 게시글 생성 : [ POST ] /posts

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/501783a8-9f44-4287-87e3-dcdd6c6848a7)


### 게시글 수정 : [ PATCH ] /posts/{id}

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/8d52e6f9-4aad-4703-9a12-5bc25a78d5ce)


### 게시글 삭제 : [ DELETE ] /posts/{id}

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/ad1f5f39-ff00-4f83-b63a-0f11d3ab1e99)


### 게시글 상세 조회 : [ GET ] /posts/{id}

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/6feae328-eefe-43e8-97de-eed9242fe9e1)


### 게시글 댓글 조회 : [ GET ] /posts/{id}/comments

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/67e1424f-38aa-419e-8d87-3a2dbdecdeb1)


### 게시글 댓글 생성 : [ POST ] /posts/{id}/comment

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/aa330499-bee4-4ef1-b44d-278f2f61c2a1)


### 게시글 댓글 수정 : [ PATCH ] /posts/{id}/{commentId}

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/bec50ab7-bdba-4d12-b764-f8e530e15bd1)


### 게시글 댓글 삭제 : [ DELETE ] /posts/{id}/{commentId}

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/82d45ee1-871c-4fd1-8a01-9051bad76804)


## 단어 기능

### 모든 단어 조회: [ GET ] /words

`Authorization`: `Bearer <JWT>`

![image](https://github.com/user-attachments/assets/c7ff2bf1-c388-4e46-97ca-e3150ea5fb5d)




