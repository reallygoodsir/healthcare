package com.really.good.sir.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        prePostEnabled = true
)
@ComponentScan(basePackages = {
        "com.really.good.sir"
})
public class SecurityConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(SecurityConfiguration.class);

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager() {
        LOGGER.info("======================================================authmanager1");
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        CustomAuthenticationFilter customFilter = new CustomAuthenticationFilter(authManager);
        LOGGER.info("======================================================filterchain1");

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/authorization/**").permitAll()
                .antMatchers("/api/doctors").authenticated()
                .antMatchers("/api/appointments").authenticated()
                .antMatchers("/api/patients").authenticated()
                .antMatchers("/api/services").authenticated()
                .antMatchers("/api/specializations").authenticated()
                .antMatchers("/api/patient-appointments").authenticated()
                .antMatchers("/api/doctor-schedules").authenticated()

                .and()
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        LOGGER.info("======================================================filterchain2");
        return http.build();
    }
}