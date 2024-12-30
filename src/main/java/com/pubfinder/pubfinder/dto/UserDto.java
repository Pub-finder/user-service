package com.pubfinder.pubfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {

    private UUID id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private List<UserDto> following;
    private List<UserDto> followers;
}