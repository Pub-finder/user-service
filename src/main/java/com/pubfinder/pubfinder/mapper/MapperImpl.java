package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;

public class MapperImpl implements Mapper {


  @Override
  public UserDto entityToDto(User entity) {
    if (entity == null) {
      return null;
    }
    return UserDto.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .firstname(entity.getFirstname())
        .lastname(entity.getLastname())
        .email(entity.getEmail())
        .password(entity.getPassword())
        .build();
  }


  @Override
  public User dtoToEntity(UserDto dto) {
    if (dto == null) {
      return null;
    }
    return User.builder()
        .id(dto.getId())
        .username(dto.getUsername())
        .firstname(dto.getFirstname())
        .lastname(dto.getLastname())
        .email(dto.getEmail())
        .password(dto.getPassword())
        .role(Role.USER)
        .build();
  }
}
