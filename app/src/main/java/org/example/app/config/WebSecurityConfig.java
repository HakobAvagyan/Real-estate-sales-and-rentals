package org.example.app.config;

import org.example.exception.ErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/login","/verify","/reset/password/byEmail", "/verify/password", "/verify/password/reset",  "/","/loginPage", "/home", "/register", "/css/**", "/js/**", "/image/**").permitAll()
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/manager/**").hasAuthority("MANAGER")
                                .requestMatchers("/blocked").hasAnyAuthority("ADMIN", "MANAGER")
                                .requestMatchers("/user/**").hasAuthority("USER")
                                .requestMatchers("/personalPage", "/remove/user/picture", "/change/password").hasAnyAuthority("ADMIN", "MANAGER", "USER")
                                .requestMatchers("/messages", "/messages/**").hasAnyAuthority("ADMIN", "MANAGER", "USER")
                                .requestMatchers("/booking/**", "/payment/**").hasAnyAuthority("ADMIN", "MANAGER", "USER")
                                .requestMatchers("/ws-chat/**").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/home")
                                .loginProcessingUrl("/login")
                                .usernameParameter("email")
                                .failureUrl("/loginPage?msg=" + ErrorCode.USER_LOGIN_NOT_FOUND.format())
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
