package com.pubfinder.pubfinder.controller;


import com.pubfinder.pubfinder.dto.FollowDto;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

/**
 * The type User controller.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDto registerRequest)
            throws HttpClientErrorException.BadRequest {
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
            throws HttpClientErrorException.BadRequest, ResourceNotFoundException {
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
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping("/follow")
    public ResponseEntity<UserDto> follow(@RequestBody FollowDto followDto) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.follow(followDto));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollow(@RequestBody FollowDto followDto) throws ResourceNotFoundException {
        userService.unfollow(followDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable UUID id) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.getFollowers(id));
    }
}