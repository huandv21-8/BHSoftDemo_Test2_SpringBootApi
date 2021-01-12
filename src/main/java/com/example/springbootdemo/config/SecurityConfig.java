package com.example.springbootdemo.config;

import com.example.springbootdemo.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // phân quyền
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()   // premilAll() là ko cần xác thực
                .antMatchers(HttpMethod.GET, "/api/subreddit/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/post/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
                        "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest().authenticated();  // tất cá các api khác phải xác thực trước khi thao tác

        // Thêm một lớp Filter kiểm tra jwt
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // cung cấp username và pass cho spring security
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    // mã hóa password
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
