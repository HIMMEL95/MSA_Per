package com.util.msa_frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean // login 시 실행되는 메소드
    public AuthenticationProvider authenticationProvider() {
        return new LoginAuthenticationProvider();
    }
}
