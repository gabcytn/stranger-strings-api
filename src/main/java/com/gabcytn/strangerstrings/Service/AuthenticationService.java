package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.Redis.RefreshTokenCacheDao;
import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.DTO.*;
import com.gabcytn.strangerstrings.Exception.*;
import com.gabcytn.strangerstrings.Model.RefreshTokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);
  private final UserDao userDao;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenCacheDao refreshTokenCacheDao;

  public AuthenticationService(
      UserDao userDao,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      RefreshTokenCacheDao refreshTokenCacheDao) {
    this.userDao = userDao;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenCacheDao = refreshTokenCacheDao;
  }

  public void signup(RegisterRequestDto user) {
    try {
      userDao.save(user.toUserEntity(passwordEncoder));
    } catch (DataIntegrityViolationException e) {
      LOG.error("Username/Email - {} - already exists.", user.getUsername());
      throw new DuplicateUserUniqueConstraintException();
    }
  }

  public JwtResponseDto authenticate(LoginRequestDto user, String refreshToken) {
    Authentication authToken =
        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);

    if (!authentication.isAuthenticated()) throw new UserNotFoundException();
    String token = jwtService.generateToken(user.getUsername());

    if (refreshToken == null) {
      String generatedRefreshToken = jwtService.generateRefreshToken();
      RefreshTokenValidator tokenValidatorDto =
          new RefreshTokenValidator(generatedRefreshToken, user.getUsername(), user.getDeviceName());
      refreshTokenCacheDao.save(tokenValidatorDto);
      jwtService.sendRefreshTokenInResponseCookie(generatedRefreshToken);
    }
    return new JwtResponseDto(token, jwtService.getExpirationTime());
  }

  public JwtResponseDto newJwt(String oldRefreshToken, String deviceName)
      throws RefreshTokenException, RefreshTokenNotFoundException {
    RefreshTokenValidator tokenValidator =
        refreshTokenCacheDao
            .findById(oldRefreshToken)
            .orElseThrow(RefreshTokenNotFoundException::new);
    if (!deviceName.equals(tokenValidator.getDeviceName()))
      throw new RefreshTokenException("Stored device name does not match request's device name");
    String jwt = jwtService.generateToken(tokenValidator.getUsername());

    refreshTokenCacheDao.deleteById(oldRefreshToken);
    String newRefreshToken = jwtService.generateRefreshToken();
    refreshTokenCacheDao.save(new RefreshTokenValidator(newRefreshToken, tokenValidator.getUsername(), tokenValidator.getDeviceName()));
    jwtService.sendRefreshTokenInResponseCookie(newRefreshToken);
    return new JwtResponseDto(jwt, jwtService.getExpirationTime());
  }
}
