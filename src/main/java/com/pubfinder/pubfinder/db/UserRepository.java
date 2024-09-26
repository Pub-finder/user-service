package com.pubfinder.pubfinder.db;

import com.pubfinder.pubfinder.models.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

}