package com.pubfinder.pubfinder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.test.database.replace=none",
    "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
})
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;


  @Test
  public void saveAndGetUserTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser());
    Optional<User> foundUser = userRepository.findById(savedUser.getId());

    assertTrue(foundUser.isPresent());
    assertEquals(savedUser.getFirstname(), foundUser.get().getFirstname());
    assertEquals(savedUser.getLastname(), foundUser.get().getLastname());
    assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    assertEquals(savedUser.getPassword(), foundUser.get().getPassword());
    assertEquals(savedUser.getRole(), foundUser.get().getRole());
  }

  @Test
  public void saveAndGetUserByUsernameTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser());
    Optional<User> foundUser = userRepository.findByUsername(savedUser.getUsername());

    assertTrue(foundUser.isPresent());
    assertEquals(savedUser.getFirstname(), foundUser.get().getFirstname());
    assertEquals(savedUser.getLastname(), foundUser.get().getLastname());
    assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    assertEquals(savedUser.getPassword(), foundUser.get().getPassword());
    assertEquals(savedUser.getRole(), foundUser.get().getRole());
  }

  @Test
  public void saveAndGetUserByEmailTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser());
    Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

    assertTrue(foundUser.isPresent());
    assertEquals(savedUser.getFirstname(), foundUser.get().getFirstname());
    assertEquals(savedUser.getLastname(), foundUser.get().getLastname());
    assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    assertEquals(savedUser.getPassword(), foundUser.get().getPassword());
    assertEquals(savedUser.getRole(), foundUser.get().getRole());
  }

  @Test
  public void deleteUserTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser());

    userRepository.delete(savedUser);
    Optional<User> user = userRepository.findById(savedUser.getId());
    assertTrue(user.isEmpty());
  }

  @Test
  public void editUser() {
    User savedUser = userRepository.save(TestUtil.generateMockUser());
    savedUser.setFirstname("something else");
    User editedUser = userRepository.save(savedUser);
    assertEquals(savedUser, editedUser);
  }

}
