package com.patientRecTransferApp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds}")
	private long jwtExpirationDate;

	public String generateToken(Authentication authentication) {
		String username;
		List<String> roles;

		Object principal = authentication.getPrincipal();

		// Extract username and roles based on principal type
		if (principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			username = userDetails.getUsername();
			roles = userDetails.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
		} else {
			username = authentication.getName();
			roles = authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
		}

		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

		logger.info("Generating token for user: " + username + " with roles: " + roles);

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);

		return Jwts.builder()
				.setClaims(claims)  // Set claims first
				.setSubject(username)
				.setIssuedAt(currentDate)
				.setExpiration(expireDate)
				.signWith(key())
				.compact();
	}
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	public String getUsername(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key())
					.build()
					.parse(token);
			return true;
		} catch (Exception e) {
			logger.error("Token validation failed: " + e.getMessage());
			return false;
		}
	}

	public List<String> getRoles(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key())
					.build()
					.parseClaimsJws(token)
					.getBody();

			List<String> roles = claims.get("roles", List.class);
			if (roles == null) {
				roles = new ArrayList<>();
			}

			logger.info("Retrieved roles from token: " + roles);
			return roles;
		} catch (Exception e) {
			logger.error("Error retrieving roles from token: " + e.getMessage());
			return new ArrayList<>();
		}
	}
//=========================================================================


}
