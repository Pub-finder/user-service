package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.TokenRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Token;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The type User service.
 */
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * Register user authentication response.
   *
   * @param user the user
   * @throws BadRequestException user with the same email or/and username already exists
   */
  public UUID registerUser(User user) throws BadRequestException {
    if (userRepository.findByEmail(user.getEmail()).isPresent() || userRepository.findByUsername(
        user.getUsername()).isPresent()) {
      throw new BadRequestException();
    }

    user.setRole(Role.ADMIN);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    User savedUser = userRepository.save(user);
    return savedUser.getId();
  }

  /**
   * Delete user, tokens, reviews and visits.
   *
   * @param user    the user
   * @throws ResourceNotFoundException the resource not found exception
   */
  public void delete(User user) throws ResourceNotFoundException {
    // isRequestAllowed(user, request);
    User foundUser = userRepository.findById(user.getId()).orElseThrow(
        () -> new ResourceNotFoundException("User with id: " + user.getId() + " was not found"));

    deleteAllUserTokens(foundUser);
    // TODO: delete all user activity see below
    // deleteAllUserVisits(foundUser);
    // deleteAllUserReviews(foundUser);
    userRepository.delete(foundUser);
  }

  /**
   * Edit user. Only admin or the user themselves can edit the object
   *
   * @param user    the user
   * @return the user dto
   * @throws BadRequestException       the user param is empty exception
   * @throws ResourceNotFoundException the user not found exception
   */
  public UserDto edit(User user)
      throws BadRequestException, ResourceNotFoundException {
    if (user == null) {
      throw new BadRequestException();
    }

    User foundUser = userRepository.findById(user.getId()).orElseThrow(
        () -> new ResourceNotFoundException(
            "User with the id: " + user.getId() + " was not found"));

    // isRequestAllowed(foundUser, request);

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    deleteAllUserTokens(foundUser);
    User editedUser = userRepository.save(user);
    return Mapper.INSTANCE.entityToDto(editedUser);
  }

  /**
   * Revoke user access.
   *
   * @param id the user id
   */
  public void revokeUserAccess(UUID id) throws ResourceNotFoundException {
    User user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User with id: " + id + " was not found")
    );
    deleteAllUserTokens(user);
  }

  /**
   * Gets user.
   *
   * @param id the users id
   * @return the user
   * @throws ResourceNotFoundException the user not found exception
   */
  @Cacheable(value = "getUser")
  public User getUser(UUID id) throws ResourceNotFoundException {
    return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " was not found"));
  }

  private void deleteAllUserTokens(User user) {
    List<Token> tokens = tokenRepository.findAllTokensByUser(user.getId());
    tokens.forEach((token -> tokenRepository.delete(token)));
  }

  // TODO: move to another microservice
  /*
  private void isRequestAllowed(User user, HttpServletRequest request) {
    String jwt = request.getHeader("Authorization").substring(7);
    // String id = authenticationService.extractUserId(jwt);

    if (!id.equals(user.getId().toString())) {
      Optional<User> userDetails = userRepository.findById(user.getId());
      if (userDetails.isEmpty() || !userDetails.get().getRole().equals(Role.ADMIN)) {
        throw new BadCredentialsException("Only admin or the user themselves can delete or edit a user");
      }
    }
  }
  */
}