package com.pubfinder.pubfinder.controller;


import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.service.UserService;
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
    public ResponseEntity<Void> registerUser(@RequestBody UserDto registerRequest)
            throws BadRequestException {
        UUID userId = userService.registerUser(Mapper.INSTANCE.dtoToEntity(registerRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-User-Id", userId.toString())
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody UserDto user)
            throws ResourceNotFoundException {
        userService.delete(Mapper.INSTANCE.dtoToEntity(user));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit")
    public ResponseEntity<UserDto> edit(@RequestBody UserDto userDTO)
            throws BadRequestException, ResourceNotFoundException {
        return ResponseEntity.ok()
                .body(userService.edit(Mapper.INSTANCE.dtoToEntity(userDTO)));
    }

    @DeleteMapping("/revokeUserAccess/{id}")
    public ResponseEntity<Void> revokeUserAccess(@PathVariable UUID id) throws ResourceNotFoundException {
        userService.revokeUserAccess(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) throws ResourceNotFoundException {
        User user = userService.getUser(id);
        return ResponseEntity.ok(Mapper.INSTANCE.entityToDto(user));
    }

}