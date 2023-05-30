package com.util.msa_frontend.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.util.msa_frontend.auth.AuthService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component("JwtTokenProvider")
public class JwtTokenProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "authService")
    private AuthService authService;

    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.access.header}")
    String accessTokenHeader;
    @Value("${jwt.refresh.header}")
    String refreshTokenHeader;
    private long tokenValidTime = 60 * 60 * 1000L; // 60분

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // generate Token
    public String createToken(String userPk, List<String> roles, Map<String, Object> userInfo, int type) { // userPk =
                                                                                                           // email

        Map<String, Object> authMap = new HashMap<>();
        authMap.put("auth", roles.toString());

        Claims claims = Jwts.claims().setSubject(userPk); // JWT Payload에 저장되는 정보단위
        claims.put("authMap", authMap); // 정보는 key / value 쌍으로 저장
        claims.put("userInfo", userInfo);

        if (type != 1) {
            tokenValidTime *= (24 * 14);
        }

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 토큰 유효시각 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘과, secret 값
                .compact();
    }

    // Request의 Header에서 token 값 가져오기
    public String resolveToken(HttpServletRequest request, int type) {
        if (type == 1) {
            return request.getHeader(accessTokenHeader);
        } else {
            return request.getHeader(refreshTokenHeader);
        }
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> getAuth(String token) {
        return (List<String>) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("auth");
    }

    public Map<String, Object> getUserInfo(String token) {
        return (Map<String, Object>) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .get("userInfo");
    }

    // 토큰 유효성, 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("expired Token : ", e);
            return false;
        } catch (Exception e) {
            log.info("invalid Token", e);
            return false;
        }
    }

    public Map<String, Object> regenToken(String refreshToken) throws Exception {
        return authService.regenAccessToken(refreshToken);
    }
}
