package com.gabcytn.shortnotice.Controller;

import com.gabcytn.shortnotice.DTO.UserLoginDTO;
import com.gabcytn.shortnotice.DTO.UserRegisterDTO;
import com.gabcytn.shortnotice.Service.AuthenticationService;
import com.gabcytn.shortnotice.Service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthenticationController(
      AuthenticationService authenticationService,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {
    this.authenticationService = authenticationService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
    try {
      authenticationService.register(userRegisterDTO);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      System.err.println("Error in user registration");
      System.err.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/login")
  public String login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
    try {
      Authentication authenticationToken =
          new UsernamePasswordAuthenticationToken(
              userLoginDTO.getUsername(), userLoginDTO.getPassword());
      Authentication authentication = authenticationManager.authenticate(authenticationToken);

      if (authentication.isAuthenticated())
        return jwtService.generateToken(userLoginDTO.getUsername());

      return "Failed";
    } catch (Exception e) {
      System.err.println("Exception while trying to log in...");
      System.err.println(e.getMessage());
      return "Error";
    }
  }
}
