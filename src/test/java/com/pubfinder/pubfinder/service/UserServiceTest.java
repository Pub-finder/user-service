package com.pubfinder.pubfinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pubfinder.pubfinder.db.TokenRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Token;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = {
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private TokenRepository tokenRepository;

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @MockBean
  private HttpServletRequest request;

  @Test
  public void registerUserTest() throws BadRequestException {
    when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    when(userRepository.save(any())).thenReturn(user);
    when(tokenRepository.save(any())).thenReturn(new Token());

    userService.registerUser(user);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  public void registerUserTestBadRequest() throws BadRequestException {
    when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
    assertThrows(BadRequestException.class, () -> userService.registerUser(user));
  }

  @Test
  public void deleteUserTest() throws ResourceNotFoundException {
    doNothing().when(userRepository).delete(user);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
    doNothing().when(tokenRepository).delete(token);

    userService.delete(user, request);
    verify(userRepository, times(1)).findById(any());
    verify(userRepository, times(1)).delete(any());
    verify(tokenRepository, times(1)).findAllTokensByUser(any());
    verify(tokenRepository, times(1)).delete(any());
  }

  @Test
  public void deleteUserTestResourceNotFound() throws ResourceNotFoundException {
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.delete(user, request));
    verify(userRepository, times(1)).findById(any());
  }

  @Test
  public void editUserTest() throws BadRequestException, ResourceNotFoundException {
    User editedUser = TestUtil.generateMockUser();
    editedUser.setUsername("Something else");
    when(userRepository.findById(any())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(editedUser);

    when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
    doNothing().when(tokenRepository).delete(token);

    UserDto result = userService.edit(editedUser, request);
    assertEquals(Mapper.INSTANCE.entityToDto(editedUser), result);
    verify(userRepository, times(1)).save(editedUser);
  }

  @Test
  public void editUserTestBadRequest() {
    assertThrows(BadRequestException.class, () -> userService.edit(null, request));
  }

  @Test
  public void editUserTestResourceNotFound() {
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.edit(user, request));
  }

  @Test
  public void revokeUserAccessTest() throws ResourceNotFoundException {
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
    doNothing().when(tokenRepository).delete(token);

    userService.revokeUserAccess(user.getId());

    verify(tokenRepository, times(1)).delete(token);
  }

  @Test
  public void revokeUserAccessTestResourceNotFound() throws ResourceNotFoundException {
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.revokeUserAccess(user.getId()));
  }

  @Test
  public void getUserTest() throws ResourceNotFoundException {
    when(userRepository.findById(any())).thenReturn(Optional.of(user));

    User result = userService.getUser(user.getId());
    assertEquals(result, user);
  }

  @Test
  public void getUserTest_NotFound() throws ResourceNotFoundException {
    when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUser(user.getId()));
  }

  private final User user = TestUtil.generateMockUser();
  private final Token token = TestUtil.generateMockToken(user);
}
