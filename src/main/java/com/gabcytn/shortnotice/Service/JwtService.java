package com.gabcytn.shortnotice.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService
{
	private final String SECRET_KEY;

	public JwtService ()
	{
		this.SECRET_KEY = generateSecretKey();
	}

	private String generateSecretKey()
	{
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey secretKey = keyGenerator.generateKey();
			return Base64.getEncoder().encodeToString(secretKey.getEncoded());
		} catch (Exception e) {
			throw new RuntimeException("Error generating secret key: " + e.getMessage());
		}
	}

	public String generateToken(String username)
	{
		Map<String, Object> claims = new HashMap<>();

		return Jwts.builder()
						.claims(claims)
						.subject(username)
						.issuedAt(new Date(System.currentTimeMillis()))
						.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3))
						.signWith(getKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getKey()
	{
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
