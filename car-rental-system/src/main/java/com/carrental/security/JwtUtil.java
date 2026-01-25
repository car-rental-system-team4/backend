package com.carrental.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.carrental.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	@Value("${jwt.token.secret}")
	private String secret;
	@Value("${jwt.token.expiration.millis}")
	private long expiration;
	private Key signinKey;

	@PostConstruct
	public void init() {
		// Convert secret string to Key object
		signinKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String createToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		String username = user.getEmail();
		String roles = user.getRole().name();

		return Jwts.builder().setSubject(username).claim("roles", roles).claim("userId", user.getId())
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(signinKey, SignatureAlgorithm.HS256).compact();
	}

	public String generateToken(User user) {
		String username = user.getEmail();
		String roles = user.getRole().name();

		return Jwts.builder().setSubject(username).claim("roles", roles).claim("userId", user.getId())
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(signinKey, SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// GENERIC CLAIM EXTRACTOR
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(signinKey).build().parseClaimsJws(token).getBody();
	}

	// VALIDATE TOKEN (Checks signature & expiration)
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());

	}
}
