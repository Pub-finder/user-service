package com.pubfinder.pubfinder.controller;

import com.pubfinder.pubfinder.dto.AuthenticationResponse;
import com.pubfinder.pubfinder.dto.LoginRequest;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody UserDto registerRequest)
      throws BadRequestException {
    AuthenticationResponse response = userService.registerUser(
        Mapper.INSTANCE.dtoToEntity(registerRequest));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(@RequestBody UserDto user, HttpServletRequest request)
      throws ResourceNotFoundException {
    userService.delete(Mapper.INSTANCE.dtoToEntity(user), request);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/edit")
  public ResponseEntity<UserDto> edit(@RequestBody UserDto userDTO, HttpServletRequest request)
      throws BadRequestException, ResourceNotFoundException {
    return ResponseEntity.ok()
        .body(userService.edit(Mapper.INSTANCE.dtoToEntity(userDTO), request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest)
      throws ResourceNotFoundException {
    return ResponseEntity.ok(userService.login(loginRequest));
  }

  @PostMapping("/revokeUserAccess")
  public ResponseEntity<Void> revokeUserAccess(@PathVariable String email) {
    userService.revokeUserAccess(email);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID id) throws ResourceNotFoundException {
    User user = userService.getUser(id);
    return ResponseEntity.ok(Mapper.INSTANCE.entityToDto(user));
  }

}