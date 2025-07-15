package com.gabcytn.shortnotice.Controller;

import com.gabcytn.shortnotice.DTO.*;
import com.gabcytn.shortnotice.Exception.AuthenticationException;
import com.gabcytn.shortnotice.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto registerRequestDto)
      throws AuthenticationException {
    authenticationService.signup(registerRequestDto);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
      throws Exception {
    JwtResponseDto responseDto = authenticationService.authenticate(loginRequestDto);
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
}
