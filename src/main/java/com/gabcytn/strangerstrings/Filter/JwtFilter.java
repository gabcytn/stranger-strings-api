package com.gabcytn.strangerstrings.Filter;

import com.gabcytn.strangerstrings.Service.JwtService;
import com.gabcytn.strangerstrings.Service.UserDetailsServiceAuth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtFilter extends OncePerRequestFilter {
  private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtFilter(
          HandlerExceptionResolver handlerExceptionResolver,
          JwtService jwtService,
          UserDetailsServiceAuth userDetailsServiceAuth) {
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsServiceAuth;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      LOG.info("No auth header / doesn't start with Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String token = authorizationHeader.substring(7);
      final String username = jwtService.extractUsername(token);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // early return if email is invalid or already authenticated
      if (username == null || authentication != null) {
        LOG.info("user email is null OR authentication is NOT NULL");
        filterChain.doFilter(request, response);
        return;
      }

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // early return if token is invalid
      if (!jwtService.isTokenValid(token, userDetails)) {
        LOG.info("Token is invalid");
        filterChain.doFilter(request, response);
        return;
      }

      UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      LOG.error("Error in jwt filter");
      LOG.error(e.getMessage());
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }

}
