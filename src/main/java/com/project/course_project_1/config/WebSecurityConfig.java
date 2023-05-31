package com.project.course_project_1.config;

import com.project.course_project_1.entity.User;
import com.project.course_project_1.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/users", "/users/**", "static/styles/**")
                                .hasRole("USER"))

                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/", "/**", "/login", "/registration", "static/styles/**")
                                .permitAll())

                .formLogin((form)->
                        form.loginPage("/login")
                                .defaultSuccessUrl("/users/main"))

                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            User user = userService.findUserByUsername(username);
            if (user != null) return user;
            throw new UsernameNotFoundException("Пользователь ‘" + username + "’ не найден.");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
