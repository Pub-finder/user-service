package com.pubfinder.pubfinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

  private String username;
  String password;
}