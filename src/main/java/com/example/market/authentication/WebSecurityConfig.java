package com.example.market.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration

public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean  // 메소드의 결과를 Bean 객체로 등록해주는 어노테이션
    public SecurityFilterChain securityFilterChain(
            // DI 자동으로 설정됨, 빌더 패턴 처럼 쓴다.
            HttpSecurity http
    )
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(
                        authHttp -> authHttp
                                .requestMatchers("/token/issue",
                                        "/token/register").anonymous()
                                .requestMatchers(
                                        HttpMethod.GET, "/items", "/items/{id}", "/items/{itemId}/comments"
                                ).permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        jwtTokenFilter,
                        AuthorizationFilter.class
                );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
