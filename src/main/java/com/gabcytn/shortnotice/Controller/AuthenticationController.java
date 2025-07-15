package com.gabcytn.shortnotice.Controller;

import com.gabcytn.shortnotice.DTO.*;
import com.gabcytn.shortnotice.Exception.AuthenticationException;
import com.gabcytn.shortnotice.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO)
      throws AuthenticationException {
    authenticationService.signup(userRegisterDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody UserLoginDTO userLoginDTO)
      throws Exception {
    JwtResponseDto responseDto = authenticationService.authenticate(userLoginDTO);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<JwtResponseDto> refreshToken(
      @Valid @RequestBody RefreshTokenRequestDto tokenRequestDto,
      @CookieValue("X-REFRESH-TOKEN") String refreshToken)
      throws Exception {
    JwtResponseDto responseDto =
        authenticationService.newJwt(refreshToken, tokenRequestDto.getDeviceName());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping("/me")
  public String userSpecificDeets() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    return userPrincipal.getUsername();
  }
}
