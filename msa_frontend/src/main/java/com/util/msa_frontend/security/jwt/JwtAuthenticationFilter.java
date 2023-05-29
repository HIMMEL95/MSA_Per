package com.util.msa_frontend.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("Start {}'s doFilter.....", this.getClass());
        HttpServletRequest req = (HttpServletRequest) request;
        // 헤더에서 토큰 받아오기
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        String requestURI = req.getRequestURI();

        // 토큰이 유효하다면
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 유저 정보를 받아
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // securityContext에 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다. url : {}", authentication.getName(), requestURI);
        } else {
            log.info("유효한 JWT 토큰이 없습니다. url : {}", requestURI);
        }
        // 다음 Filter 실행
        chain.doFilter(request, response);
    }
}
