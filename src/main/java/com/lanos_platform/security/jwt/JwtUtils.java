package com.lanos_platform.security.jwt;

import java.security.Key;


import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lanos_platform.security.Repository.BlacklistedTokenRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;



@Component
public class JwtUtils {
	
	private static final Logger Logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;

	@Value("${spring.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	@Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

	public String getJwtFromHeader(HttpServletRequest request) {
	    String bearerToken = request.getHeader("Authorization");
	    Logger.debug("Authorization Header: {}", bearerToken);

	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7); // Remove Bearer prefix
	    }
	    return null;
	}
	public String generateTokenFromUsername(String email) {
	    return Jwts.builder()
	            .subject(email)  // ✅ Fix: Direct email pass karo
	            .issuedAt(new Date())
	            .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.claim("role", "ADMIN")
	            .signWith(key())
	            .compact();
	}
	public String getUserNameFromJwtToken(String token) {
	    return Jwts.parser()
	            .verifyWith((SecretKey) key())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .getSubject();
	}

	private Key key() {
	    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	
	public boolean validateJwtToken(String authToken) {
	    try {
	        System.out.println("Validate");
	        Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
	        
	        if (blacklistedTokenRepository.existsByToken(authToken)) {
                Logger.error("Token is blacklisted: {}", authToken);
                System.out.println("Token blacklisted: " + authToken);
                return false;
	        }
			return true;
	    } catch (MalformedJwtException e) {
	        Logger.error("Invalid JWT token: {}", e.getMessage());
	    } catch (ExpiredJwtException e) {
	        Logger.error("JWT token is expired: {}", e.getMessage());
	    } catch (UnsupportedJwtException e) {
	        Logger.error("JWT token is unsupported: {}", e.getMessage());
	    } catch (IllegalArgumentException e) {
	        Logger.error("JWT claims string is empty: {}", e.getMessage());
	    }
	    return false;
	}


	
}
