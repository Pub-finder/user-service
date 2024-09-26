package com.pubfinder.pubfinder.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * The type Authentication service.
 */
@Component
public class AuthenticationService {

  @Value("${security.jwt-secret}")
  private String SECRET_KEY;
  @Value("${security.jwt-expiration-ms}")
  private long JWT_EXPIRATION;
  @Value("${security.jwt-refresh-expiration-ms}")
  private long REFRESHER_EXPIRATION;

  public String extractUsername(String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Generate access token string.
   *
   * @param userDetails the user details
   * @return the string
   */
  public String generateToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, JWT_EXPIRATION);
  }

  /**
   * Generate refresher token string.
   *
   * @param userDetails the user details
   * @return the string
   */
  public String generateRefresherToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, REFRESHER_EXPIRATION);
  }


  /**
   * Build token string.
   *
   * @param extractClaims the extracted claims
   * @param userDetails   the user details
   * @param exertionTime  the exertion time
   * @return the string
   */
  public String buildToken(Map<String, Object> extractClaims, UserDetails userDetails,
      long exertionTime) {
    return Jwts
        .builder()
        .claims(extractClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + exertionTime))
        .signWith(getSignInKey(), Jwts.SIG.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}