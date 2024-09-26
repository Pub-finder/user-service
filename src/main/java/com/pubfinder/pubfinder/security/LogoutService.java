package com.pubfinder.pubfinder.security;

import com.pubfinder.pubfinder.db.TokenRepository;
import com.pubfinder.pubfinder.models.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * The type Logout service.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null) {
      return;
    }
    String token = authHeader.substring(7);
    Token storedToken = tokenRepository.findByToken(token).orElseThrow();
    tokenRepository.delete(storedToken);
    SecurityContextHolder.clearContext();
  }
}
