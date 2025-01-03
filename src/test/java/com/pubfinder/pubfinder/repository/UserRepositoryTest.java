package com.pubfinder.pubfinder.repository;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@DataJpaTest
@TestPropertySource(properties = {
    "spring.test.database.replace=none",
    "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
})
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Transactional
  @Test
  public void saveAndGetUserWithFollowingTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));
    for (int i = 0; i<3;i++) {
      User mockUser = TestUtil.generateMockUser(null);
      userRepository.save(mockUser);
      savedUser.addFollowing(mockUser);
    }
    userRepository.save(savedUser);
    Optional<User> foundUser = userRepository.findById(savedUser.getId());

    assertTrue(foundUser.isPresent());
    assertEquals(savedUser.getFirstname(), foundUser.get().getFirstname());
    assertEquals(savedUser.getLastname(), foundUser.get().getLastname());
    assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    assertEquals(savedUser.getPassword(), foundUser.get().getPassword());
    assertEquals(savedUser.getRole(), foundUser.get().getRole());
    assertEquals(savedUser.getFollowing().size(), foundUser.get().getFollowing().size());
    assertEquals(savedUser.getFollowers().size(), foundUser.get().getFollowers().size());
  }

  @Test
  public void saveAndGetUserByUsernameTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));
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
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));
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
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));

    userRepository.delete(savedUser);
    Optional<User> user = userRepository.findById(savedUser.getId());
    assertTrue(user.isEmpty());
  }

  @Test
  public void followAndDeleteUserTest() {
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));
    User savedUser2 = userRepository.save(TestUtil.generateMockUser(null));

    savedUser.addFollowing(savedUser2);
    userRepository.save(savedUser);   // Save updated user with following relationship
    userRepository.save(savedUser2);  // Save savedUser2 to reflect the followers correctly

    Optional<User> u1 = userRepository.findById(savedUser2.getId());
    assertTrue(u1.isPresent());
    assertEquals(1, u1.get().getFollowers().size());

    userRepository.delete(savedUser);
    Optional<User> userAfterDeletion = userRepository.findById(savedUser.getId());
    assertTrue(userAfterDeletion.isEmpty(), "SavedUser should be deleted");

    Optional<User> u2 = userRepository.findById(savedUser2.getId());
    assertTrue(u2.isPresent(), "Expected user2 to still be present after user deletion");
    assertEquals(0, u2.get().getFollowers().size(), "User2 should have 0 followers after user1 is deleted");
  }

  @Test
  public void editUser() {
    User savedUser = userRepository.save(TestUtil.generateMockUser(null));
    savedUser.setFirstname("something else");
    User editedUser = userRepository.save(savedUser);
    assertEquals(savedUser, editedUser);
  }

}
