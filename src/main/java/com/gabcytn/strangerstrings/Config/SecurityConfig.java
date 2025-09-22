package com.gabcytn.strangerstrings.Config;

import com.gabcytn.strangerstrings.Filter.JwtFilter;
import com.gabcytn.strangerstrings.Service.UserDetailsServiceAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtFilter jwtFilter;
  private final UserDetailsService userDetailsService;
  @Value("${cors.front-end.url}")
  private String FRONTEND_URL;

  public SecurityConfig(UserDetailsServiceAuth userDetailsService, JwtFilter jwtFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    // disable csrf
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    httpSecurity.cors(httpSecurityCorsConfigurer -> {
      httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
    });

    // disable http basic
    httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

    // disable default form login
    httpSecurity.formLogin(AbstractHttpConfigurer::disable);

    httpSecurity.authorizeHttpRequests(
        request -> {
          request
              .requestMatchers("/api/v1/auth/**", "/ws/v1/stranger-strings")
              .permitAll()
              .anyRequest()
              .authenticated();
        });

    // stateless session management
    httpSecurity.sessionManagement(
        httpSecuritySessionManagementConfigurer -> {
          httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
              SessionCreationPolicy.STATELESS);
        });

    // jwt filter
    httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  private UrlBasedCorsConfigurationSource corsConfigurationSource () {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin(FRONTEND_URL);
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    corsConfiguration.addAllowedHeader("Content-Type");
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);

    return source;
  }
}
