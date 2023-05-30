package com.util.msa_frontend.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.util.msa_frontend.auth.AuthService;
import com.util.msa_frontend.security.jwt.JwtTokenProvider;

public class CustomAuthenticationProvider implements AuthenticationProvider {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException, AccessDeniedException {
        Map<String, Object> map = new HashMap<String, Object>();

        String user_id = authentication.getPrincipal().toString();
        String user_pw = authentication.getCredentials().toString();

        map.put("member_id_num", user_id);
        map.put("password_enc", user_pw);

        Map<String, Object> resultMap = new HashMap<>();
        try {
            resultMap = authService.selectLoginProccess(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("==입력정보==");
        log.info("MEMBER_ID_NUM : " + user_id);
        log.info("==입력정보==");

        if ("Y".equals(resultMap.get("result"))) {
            log.info(user_id);

            String accessToken = (String) resultMap.get("accessToken");
            String refreshToken = (String) resultMap.get("refreshToken");

            List<String> tokenRoles = jwtTokenProvider.getAuth(accessToken);
            List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
            for (String role : tokenRoles) {
                roles.add(new SimpleGrantedAuthority(role));
            }

            Map<String, Object> userDetail = jwtTokenProvider.getUserInfo(accessToken);
            userDetail.put("accessToken", accessToken);
            userDetail.put("refreshToken", refreshToken);

            log.info("권한 : " + roles);
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user_id, "", roles);
            result.setDetails(userDetail);
            return result;
        } else if ("NOAUTH".equals(resultMap.get("result"))) {
            log.info("접근 권한이 없습니다.");
            throw new AccessDeniedException("접근 권한이 없습니다.");
        } else if ("UNUSE".equals(resultMap.get("result"))) {
            log.info("퇴사자 입니다.");
            throw new BadCredentialsException("퇴사자 입니다.");
        } else {
            log.info("사용자 크리덴셜 정보가 틀립니다.");
            throw new BadCredentialsException("아이디 또는 비밀번호를 확인하세요.");
        }
    }
}
