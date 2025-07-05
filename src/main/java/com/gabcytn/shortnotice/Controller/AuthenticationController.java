package com.gabcytn.shortnotice.Controller;

import com.gabcytn.shortnotice.DTO.UserRegisterDTO;
import com.gabcytn.shortnotice.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController
{
	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService)
	{
		this.authenticationService = authenticationService;
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
}
