package com.gabcytn.shortnotice.Config;

import com.gabcytn.shortnotice.Filter.JwtFilter;
import com.gabcytn.shortnotice.Service.UserDetailsServiceAuth;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final JwtFilter jwtFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(UserDetailsServiceAuth userDetailsService, JwtFilter jwtFilter) {
    this.userDetailsService = userDetailsService;
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    // disable csrf
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    // disable http basic
    httpSecurity.httpBasic(AbstractHttpConfigurer::disable);

    // disable default form login
    httpSecurity.formLogin(AbstractHttpConfigurer::disable);

    httpSecurity.authorizeHttpRequests(
        request -> {
          request.requestMatchers("/auth/**").permitAll().anyRequest().authenticated();
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
}
