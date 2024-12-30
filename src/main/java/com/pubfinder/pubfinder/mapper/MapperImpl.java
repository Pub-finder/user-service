package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;

import java.util.List;

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
        .following(entity.getFollowing().stream().map(this::entityToDtoWithoutRelationship).toList())
        .followers(entity.getFollowers().stream().map(this::entityToDtoWithoutRelationship).toList())
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

  private UserDto entityToDtoWithoutRelationship(User entity) {
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
            .following(List.of())
            .followers(List.of())
            .build();
  }
}
