package com.pubfinder.pubfinder.mapper;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest(properties = {
    "spring.cache.type=none",
    "bucket4j.enabled=false",
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class MapperTest {

  @Test
  public void mapUserEntityToDtoTest() {
    User user = TestUtil.generateMockUser(UUID.randomUUID());
    for (int i=0;i<10;i++) {
      user.addFollowing(TestUtil.generateMockUser(UUID.randomUUID()));
    }
    UserDto userDTO = Mapper.INSTANCE.entityToDto(user);
    checkUser(userDTO, user);
    assertEquals(userDTO.getFollowers().size(), user.getFollowers().size());
  }


  @Test
  public void mapUserDtoToEntityTest() {
    UserDto userDTO = TestUtil.generateMockUserDTO();
    User user = Mapper.INSTANCE.dtoToEntity(userDTO);
    checkUser(userDTO, user);
  }

  private void checkUser(UserDto userDTO, User user) {
    assertEquals(userDTO.getFirstname(), user.getFirstname());
    assertEquals(userDTO.getLastname(), user.getLastname());
    assertEquals(userDTO.getEmail(), user.getEmail());
    assertEquals(userDTO.getUsername(), user.getUsername());
    assertEquals(userDTO.getPassword(), user.getPassword());
  }
}
