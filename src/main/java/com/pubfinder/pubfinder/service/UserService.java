package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.TokenRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.FollowDto;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.BadRequestException;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Token;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new BadRequestException("An account with the email address '" + user.getEmail() + "' already exists. Please use a different email.");
    }

    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      throw new BadRequestException("The username '" + user.getUsername() + "' is already taken. Please choose a different username.");
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
      throws HttpClientErrorException.BadRequest, ResourceNotFoundException {
    if (user == null) {
      throw new BadRequestException();
    }

    User foundUser = userRepository.findById(user.getId()).orElseThrow(
        () -> new ResourceNotFoundException(
            "User with the id: " + user.getId() + " was not found"));

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
  public UserDto getUser(UUID id) throws ResourceNotFoundException {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " was not found"));
    return Mapper.INSTANCE.entityToDto(user);
  }

  private void deleteAllUserTokens(User user) {
    List<Token> tokens = tokenRepository.findAllTokensByUser(user.getId());
    tokens.forEach((token -> tokenRepository.delete(token)));
  }

  public UserDto follow(FollowDto followDto) throws ResourceNotFoundException {
    User user = userRepository.findById(followDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User with id: " + followDto.getUserId() + " was not found"));
    User userToFollow = userRepository.findById(followDto.getFollowId()).orElseThrow(() -> new ResourceNotFoundException("User with id: " + followDto.getUserId() + " was not found"));
    user.addFollowing(userToFollow);

    userRepository.save(userToFollow);
    return Mapper.INSTANCE.entityToDto(userRepository.save(user));
  }

  // TODO: add a cache maybe
  public List<UserDto> getFollowers(UUID id) throws ResourceNotFoundException {
    User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " was not found"));
    return user.getFollowers().stream().map(Mapper.INSTANCE::entityToDto).toList();
  }

  public void unfollow(FollowDto followDto) throws ResourceNotFoundException {
    User user = userRepository.findById(followDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User with id: " + followDto.getUserId() + " was not found"));
    User userToUnfollow = userRepository.findById(followDto.getFollowId()).orElseThrow(() -> new ResourceNotFoundException("User with id: " + followDto.getUserId() + " was not found"));
    user.removeFollowing(userToUnfollow);

    userRepository.save(user);
    userRepository.save(userToUnfollow);
  }
}