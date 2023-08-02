package com.example.mission.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.logging.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    protected SecurityFilterChain sequrityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/store/write").hasAuthority("PARTNER_USER") // 여기에 경로와 권한 설정 추가
                .antMatchers("/api/v1/store/list").permitAll() // /api/v1/store/list 엔드포인트를 토큰 없이도 접근 허용
                .antMatchers("/api/v1/store/list/near").permitAll()
                .antMatchers("/api/v1/store/list/{storeId}").permitAll()
                .antMatchers("/api/v1/store/comment/{storeId}").hasAuthority("USER")
                .antMatchers("/api/v1/store/reservation/{storeId}").hasAuthority("USER")
                .antMatchers("/api/v1/store/reservation/user/check/status/cancel/{reservationId}").hasAuthority("USER")
                .antMatchers("/api/v1/store/reservation/user/check/visit/status/ok/{reservationId}").hasAuthority("USER")
                .antMatchers("/api/v1/store/list/starSort").permitAll()
                .antMatchers("/api/v1/store/update/{storeId}").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/delete/{storeId}").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/delete/{storeId}").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/reservation/partner_user/check/status/ok/{reservationId}").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/reservation/partner/user/reservation/{storeId}/status").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/reservation/partner_user/check/status/cancel/{reservationId}").hasAuthority("PARTNER_USER")
                .antMatchers("/api/v1/store/reservation/partner_user/check/status/final/ok/{reservationId}").hasAuthority("PARTNER_USER")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // authenticationProvider 등 필요한 다른 설정을 추가해야 함

}
