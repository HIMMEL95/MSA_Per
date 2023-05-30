package com.util.msa_frontend.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.util.msa_frontend.util.CookieUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource(name = "JwtTokenProvider")
    private JwtTokenProvider jwtTokenProvider;

    private static final List<String> EXCLUDE_URL = Collections.unmodifiableList(
            Arrays.asList("//",
                    "/src/"));

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return EXCLUDE_URL.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("{} : doFilter Start .... ", this.getClass());

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = jwtTokenProvider.resolveToken(req, 1);
        String refresh = "";

        log.info(req.getRequestURI());
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            log.info("AccessToken 정상");
            setAuthentication(token, req);
        } else {
            log.info("AccessToken 만료");
            refresh = CookieUtils.getCookie("refreshToken", req);

            if (StringUtils.hasText(refresh) && jwtTokenProvider.validateToken(refresh)) {
                log.info("accessToken 재발급");
                try {
                    Map<String, Object> newToken = jwtTokenProvider.regenToken(refresh);
                    token = (String) newToken.get("accessToken");
                    refresh = (String) newToken.get("refreshToken");

                    setAuthentication(token, req);
                    CookieUtils.setCookie("refreshToken", refresh, res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // else if ("/calendar/viewCalendar.do".equals(req.getRequestURI())) {
            // log.info("viewCalendar accessToken 재발급");
            // token = req.getParameter("state");
            // if (StringUtils.hasText(token)) {
            // token = token.split(" ")[1];
            // try {
            // Map<String, Object> newToken = jwtTokenProvider.regenToken(token);
            // token = (String) newToken.get("accessToken");
            // refresh = (String) newToken.get("refreshToken");
            // setAuthentication(token, req);
            // CookieUtils.setCookie("refreshToken", refresh, res);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // }
            // }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest req) {
        String userPK = jwtTokenProvider.getUserPk(token);

        List<String> authList = jwtTokenProvider.getAuth(token);
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();

        for (String auth : authList) {
            roles.add(new SimpleGrantedAuthority(auth));
        }

        Map<String, Object> userInfo = jwtTokenProvider.getUserInfo(token);
        log.info("====================== userInfo ======================");
        log.info(userInfo.toString());
        userInfo.put("accessToken", token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPK, "", roles);
        authentication.setDetails(userInfo);

        req.setAttribute("auth", roles.toString());
        req.setAttribute("userInfo", userInfo);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
