package com.gabcytn.shortnotice.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabcytn.shortnotice.DTO.*;
import com.gabcytn.shortnotice.Entity.User;
import com.gabcytn.shortnotice.Exception.AuthenticationException;
import com.gabcytn.shortnotice.Exception.RefreshTokenException;
import com.gabcytn.shortnotice.DAO.RedisCacheDao;
import com.gabcytn.shortnotice.DAO.UserDAO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final UserDAO userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RedisCacheDao redisCacheRepository;
  private final ObjectMapper objectMapper;
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final Long oneWeek = 60L * 60 * 24 * 7;

  public AuthenticationService(
          UserDAO userDAO,
          PasswordEncoder passwordEncoder,
          AuthenticationManager authenticationManager,
          JwtService jwtService,
          RedisCacheDao redisCacheDao,
          ObjectMapper objectMapper, HttpServletRequest request,
          HttpServletResponse response) {
    this.userRepository = userDAO;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.redisCacheRepository = redisCacheDao;
    this.objectMapper = objectMapper;
    this.request = request;
    this.response = response;
  }

  public void signup(UserRegisterDTO user) throws AuthenticationException {
    try {
      User userToSave = new User();
      userToSave.setEmail(user.getEmail());
      userToSave.setUsername(user.getUsername());
      userToSave.setPassword(passwordEncoder.encode(user.getPassword()));

      userRepository.save(userToSave);
    } catch (Exception e) {
      System.err.println("Error signing up user: " + user.getEmail());
      System.err.println(e.getMessage());
      throw new AuthenticationException(
              "User with email of: " + user.getEmail() + " fails to be inserted in the DB.");
    }
  }

  public JwtResponseDto authenticate(UserLoginDTO user)
          throws Exception{
    Authentication authToken =
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);

    if (!authentication.isAuthenticated()) throw new AuthenticationException("User not found");

    String token = jwtService.generateToken(user.getUsername());
    // for future validation of a refresh token
    RefreshTokenValidatorDto tokenValidatorDto =
            new RefreshTokenValidatorDto(user.getUsername(), user.getDeviceName());
    String tokenValidatorAsString = objectMapper.writeValueAsString(tokenValidatorDto);
    if (!hasRefreshToken(request))
      jwtService.generateRefreshToken(response, tokenValidatorAsString, oneWeek);
    return new JwtResponseDto(token, jwtService.getExpirationTime());
  }

  public JwtResponseDto newJwt(String refreshToken, String deviceName)
          throws RefreshTokenException {
    try {
      Optional<CacheData> cacheData = redisCacheRepository.findById(refreshToken);
      if (cacheData.isEmpty()) throw new RefreshTokenException("Refresh token is invalid.");

      String tokenValidatorAsString = cacheData.get().getValue();
      TypeReference<RefreshTokenValidatorDto> mapType = new TypeReference<>() {};
      RefreshTokenValidatorDto tokenValidatorDto =
              objectMapper.readValue(tokenValidatorAsString, mapType);
      if (!deviceName.equals(tokenValidatorDto.deviceName()))
        throw new RefreshTokenException("Stored device name does not match request's device name");
      String jwt = jwtService.generateToken(tokenValidatorDto.username());

      // delete old refresh token
      redisCacheRepository.delete(cacheData.get());
      jwtService.generateRefreshToken(response, tokenValidatorAsString, oneWeek);
      return new JwtResponseDto(jwt, jwtService.getExpirationTime());
    } catch (Exception e) {
      System.err.println("Error generating new JWT");
      System.err.println(e.getMessage());
      throw new RefreshTokenException(e.getMessage());
    }
  }

  private boolean hasRefreshToken(HttpServletRequest request) {
    Cookie[] requestCookies = request.getCookies();
    if (requestCookies == null) return false;

    for (Cookie requestCookie : requestCookies)
      if ("X-REFRESH-TOKEN".equals(requestCookie.getName())) return true;

    return false;
  }
}
