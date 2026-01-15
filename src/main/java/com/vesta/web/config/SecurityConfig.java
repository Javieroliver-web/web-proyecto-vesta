package com.vesta.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                // Permitir acceso a recursos estáticos y páginas públicas
                                                .requestMatchers("/", "/login-page", "/register", "/css/**",
                                                                "/images/**", "/js/**",
                                                                "/select-recovery-method")
                                                .permitAll()
                                                // Permitir endpoint de login propio (POST)
                                                .requestMatchers("/login", "/login/**").permitAll()
                                                // Todo lo demás se permite (la seguridad se maneja manualmente en
                                                // Controllers)
                                                .anyRequest().permitAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login-page") // Usar nuestra template personalizada
                                                .defaultSuccessUrl("/oauth2/success-handler", true) // Redirigir a un
                                                                                                    // controlador
                                                                                                    // intermedio
                                                                                                    // nuestro
                                )
                                .csrf(csrf -> csrf.disable()); // Desactivar CSRF por simplicidad en desarrollo

                return http.build();
        }
}
