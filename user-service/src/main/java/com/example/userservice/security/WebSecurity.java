package com.example.userservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.example.userservice.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
   // private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
        //this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/users/**").permitAll();
        
        http.headers().frameOptions().disable(); //h2-console 프레임 안보이는 오류

//        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
//        http.authorizeRequests().antMatchers("/health_check/**").permitAll();
//        http.authorizeRequests().antMatchers("/**")
//                .hasIpAddress(env.getProperty("gateway.ip")) // <- IP 변경
//                .and()
//                .addFilter(getAuthenticationFilter());
//
////        http.authorizeRequests().antMatchers("/users")
////                .hasIpAddress(env.getProperty("gateway.ip")) // <- IP 변경
////                .and()
////                .addFilter(getAuthenticationFilter());
////
////        http.authorizeRequests().anyRequest().denyAll();
//
//        http.headers().frameOptions().disable();
    }

//    private AuthenticationFilter getAuthenticationFilter() throws Exception {
//        AuthenticationFilter authenticationFilter =
//                new AuthenticationFilter(authenticationManager(), userService, env);
//
//        return authenticationFilter;
//    }
//
//    // select pwd from users where email=?
//    // db_pwd(encrypted) == input_pwd(encrypted)
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
//    }
}