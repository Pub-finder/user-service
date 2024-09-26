package com.pubfinder.pubfinder.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

  private UUID id;
  private String username;
  private String firstname;
  private String lastname;
  private String email;
  private String password;
}