package com.selfaxiom.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/v1/health",
                "/api/v1/auth/login",
                "/api/v1/auth/register",
                "/api/v1/auth/refresh",
                "/api/v1/auth/logout")
            .permitAll()
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(this::writeUnauthorized)
            .accessDeniedHandler((request, response, accessDeniedException) -> writeForbidden(request, response)))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, Exception ex)
      throws IOException {
    writeError(response, request, HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required");
  }

  private void writeForbidden(HttpServletRequest request, HttpServletResponse response) throws IOException {
    writeError(response, request, HttpStatus.FORBIDDEN, "Forbidden", "Access denied");
  }

  private void writeError(HttpServletResponse response, HttpServletRequest request, HttpStatus status,
      String error,
      String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json");
    String escapedMessage = message.replace("\"", "\\\"");
    String escapedError = error.replace("\"", "\\\"");
    String escapedPath = request.getRequestURI().replace("\"", "\\\"");
    String body = String.format(
        "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
        Instant.now(),
        status.value(),
        escapedError,
        escapedMessage,
        escapedPath);
    response.getWriter().write(body);
  }
}
