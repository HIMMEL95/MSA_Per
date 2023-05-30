package com.util.msa_frontend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.CookieRequestCache;

import com.util.msa_frontend.security.AuthenticationFilterAnotherParam;
import com.util.msa_frontend.security.CustomAuthenticationProvider;
import com.util.msa_frontend.security.CustomAuthenticationSuccessHandler;
import com.util.msa_frontend.security.jwt.JwtAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain resource(HttpSecurity http) throws Exception {
        return http.requestMatchers(matchers -> matchers
                .antMatchers(
                        "/font-awesome/**",
                        "/fonts/**",
                        "/images/**",
                        "/other/**",
                        "/font/**",
                        "/img/**",
                        "/jqgrid/**",
                        "/css/**",
                        "/js/**",
                        "/favicon*",
                        "/nvD3/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(
                http.getSharedObject(AuthenticationConfiguration.class));
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cutomAuthenticationFilter(authenticationManager),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler
                        .accessDeniedPage("/common/accessDenied.do"))
                .requestCache(cache -> cache.requestCache(new CookieRequestCache()))
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(
                                "/login.do",
                                "/api/loginProcess.do",
                                "/login",
                                "/loginFail.do",
                                // "/**",
                                "calendar/calendarEventConviteResult.do")
                        .permitAll() // 로그인 없이 접근
                        .anyRequest().authenticated()) // 이외 요청은 인증필요
                .build();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Bean
    CustomAuthenticationProvider provider() throws Exception {
        return new CustomAuthenticationProvider();
    }

    @Bean
    AuthenticationFilterAnotherParam cutomAuthenticationFilter(AuthenticationManager authenticationManager)
            throws Exception {
        return new AuthenticationFilterAnotherParam(authenticationManager);
    }

    @Bean
    CustomAuthenticationSuccessHandler successHandler() throws Exception {
        CustomAuthenticationSuccessHandler handler = new CustomAuthenticationSuccessHandler();
        return handler;
    }

    @Bean
    BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // SecuredObjectService securedObjectService() throws Exception {
    // return new SecuredObjectService();
    // }

    // @Bean
    // ReloadableFilterInvocationSecurityMetadataSource
    // reloadableFilterInvocationSecurityMetadataSource()
    // throws Exception {
    // UrlResourcesMapFactoryBean requestMap = requestMap();
    // ReloadableFilterInvocationSecurityMetadataSource meta = new
    // ReloadableFilterInvocationSecurityMetadataSource(
    // requestMap.getObject());
    // meta.setSecuredObjectService(securedObjectService());
    // return meta;
    // }

    // @Bean
    // UrlResourcesMapFactoryBean requestMap() throws Exception {
    // UrlResourcesMapFactoryBean requestMap = new UrlResourcesMapFactoryBean();
    // requestMap.setSecuredObjectService(securedObjectService());
    // return requestMap;
    // }

    // @Bean
    // FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
    // FilterSecurityInterceptor filter = new FilterSecurityInterceptor();
    // filter.setAccessDecisionManager(accessDecisionManager());
    // filter.setSecurityMetadataSource(reloadableFilterInvocationSecurityMetadataSource());
    // return filter;
    // }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    AccessDecisionManager accessDecisionManager() throws Exception {
        List<AccessDecisionVoter<?>> roles = new ArrayList<>();
        RoleVoter voter = new RoleVoter();
        voter.setRolePrefix("");
        roles.add(voter);
        AffirmativeBased base = new AffirmativeBased(roles);
        base.setAllowIfAllAbstainDecisions(false);
        return base;
    }
}