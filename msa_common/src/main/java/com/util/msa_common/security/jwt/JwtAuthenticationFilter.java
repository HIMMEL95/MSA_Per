package com.per.msa_common.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String token = jwtTokenProvider.resolveToken(req, 1);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userPk = jwtTokenProvider.getUserPk(token);
            List<String> authList = jwtTokenProvider.getAuth(token);

            if (authList != null && authList.size() > 0) {
                List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
                for (String auth : authList) {
                    roles.add(new SimpleGrantedAuthority(auth));
                }
                UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userPk, "", roles);
                Map<String, Object> detail = jwtTokenProvider.getUserInfo(token);
                detail.put("accessToken", token);
                result.setDetails(detail);

                log.info("==========user detail=========");
                log.info(jwtTokenProvider.getUserInfo(token).toString());
                log.info("=============================");
                SecurityContextHolder.getContext().setAuthentication(result);
            } else {
                res.sendError(HttpStatus.FORBIDDEN.value());
            }

        } else {
            res.sendError(HttpStatus.UNAUTHORIZED.value());
        }
        chain.doFilter(request, response);

    }
}
