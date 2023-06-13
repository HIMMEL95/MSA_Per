package com.per.msa_common.security.jwt;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Component("JwtTokenProvider")
public class JwtTokenProvider {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("#{config['spring.jwt.secret']}")
    private String secretKey;

    @Value("#{config['spring.jwt.access.header']}")
    private String accessTokenHeader;

    @Value("#{config['spring.jwt.refresh.header']}")
    private String refreshTokenHeader;

    private long AccessTokenValidMillisecond = 1000L * 60 * 60; // 1시간만 토큰 유효
    // private long AccessTokenValidMillisecond = 1L; // 1시간만 토큰 유효
    private long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 14; // 2주 토큰 유효

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * Request의 Header에서 token 파싱
     * 
     * @param req
     * @param type 1: Access Token 2: Refresh Token
     * @return
     */
    public String resolveToken(HttpServletRequest req, int type) {
        if (type == 1) {
            return req.getHeader(accessTokenHeader);
        } else {
            return req.getHeader(refreshTokenHeader);
        }
    }

    /**
     * Jwt 토큰으로 인증 정보를 조회
     */
    /*
     * public Authentication getAuthentication(String token) {
     * UserDetails userDetails =
     * userDetailsService.loadUserByUsername(this.getUserPk(token));
     * return new UsernamePasswordAuthenticationToken(userDetails, "",
     * userDetails.getAuthorities());
     * }
     */

    /**
     * Jwt 토큰에서 회원 구별 정보 추출
     */
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

    /**
     * Jwt 토큰의 유효성 + 만료일자 확인
     * 
     * @throws IOException
     */
    public boolean validateToken(String jwtToken) throws IOException {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("expired token", e);
            return false;
        } catch (Exception e) {
            log.info("invalid token", e);
            return false;
        }
    }
}
