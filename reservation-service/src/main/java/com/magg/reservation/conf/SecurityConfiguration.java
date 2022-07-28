package com.magg.reservation.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration
{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        // We don't need CSRF for this example
        httpSecurity
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            ;

        httpSecurity.csrf()
            .disable();
        return httpSecurity.build();
    }



}
