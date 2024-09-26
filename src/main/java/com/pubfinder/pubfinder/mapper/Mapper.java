package com.pubfinder.pubfinder.mapper;

import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface Mapper {

  Mapper INSTANCE = Mappers.getMapper(Mapper.class);

  UserDto entityToDto(User entity);

  User dtoToEntity(UserDto dto);

}