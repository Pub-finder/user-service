package com.pubfinder.pubfinder.util;

import com.pubfinder.pubfinder.dto.FollowDto;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.Token;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;
import com.pubfinder.pubfinder.models.enums.TokenType;
import java.util.List;
import java.util.UUID;

public class TestUtil {

  public static User generateMockUser(UUID id) {
    return User.builder()
        .id(id)
        .firstname("firstName")
        .lastname("lastName")
        .email("email")
        .username("username")
        .password("password")
        .role(Role.USER)
        .build();
  }

  public static UserDto generateMockUserDTO() {
    return UserDto.builder()
        .id(UUID.randomUUID())
        .firstname("firstName")
        .lastname("lastName")
        .email("email")
        .username("username")
        .password("password")
        .followers(List.of())
        .following(List.of())
        .build();
  }

  public static Token generateMockToken(User user) {
    return Token.builder()
        .id(null)
        .token("token")
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .user(user)
        .build();
  }

  public static List<Token> generateListOfMockedTokens(User user) {
    Token token1 = generateMockToken(user);
    token1.setToken("token1");

    Token token2 = generateMockToken(user);
    token2.setToken("token2");

    Token token3 = generateMockToken(user);
    token3.setToken("token3");

    return List.of(token1, token2, token3);
  }

  public static FollowDto generateFollowDto(User user, User utf) {
    return FollowDto.builder()
            .userId(user.getId())
            .followId(utf.getId())
            .build();
  }

}
