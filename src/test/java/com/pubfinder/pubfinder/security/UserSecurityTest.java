package com.pubfinder.pubfinder.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.models.enums.Role;
import com.pubfinder.pubfinder.service.UserService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
    "spring.datasource.url=",
    "spring.jpa.database-platform=",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.cache.type=none",
    "bucket4j.enabled=false",
})
@AutoConfigureMockMvc()
public class UserSecurityTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private UserService userService;

  @Autowired
  private AuthenticationService authenticationService;

  @Test
  public void deletePubAdminAuthorizedTest() throws Exception {
    User user = TestUtil.generateMockUser();
    user.setRole(Role.ADMIN);
    String jwtToken = authenticationService.generateToken(user);

    when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

    mockMvc.perform(delete("/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
        .andExpect(status().isNoContent());
  }

  @Test
  public void deleteUserUserAuthorizedTest() throws Exception {
    User user = TestUtil.generateMockUser();
    user.setRole(Role.USER);
    String jwtToken = authenticationService.generateToken(user);

    when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

    mockMvc.perform(delete("/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
        .andExpect(status().isNoContent());
  }

  @Test
  public void deleteUserUnauthenticatedTest() throws Exception {
    mockMvc.perform(delete("/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void editPubAdminAuthorizedTest() throws Exception {
    User user = TestUtil.generateMockUser();
    user.setRole(Role.ADMIN);
    String jwtToken = authenticationService.generateToken(user);

    when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

    mockMvc.perform(put("/user/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
        .andExpect(status().isOk());
  }

  @Test
  public void editUserUserAuthorizedTest() throws Exception {
    User user = TestUtil.generateMockUser();
    user.setRole(Role.USER);
    String jwtToken = authenticationService.generateToken(user);

    when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

    mockMvc.perform(put("/user/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
        .andExpect(status().isOk());
  }

  @Test
  public void editUserUnauthenticatedTest() throws Exception {
    mockMvc.perform(put("/user/edit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isForbidden());
  }

  UserDto userDTO = TestUtil.generateMockUserDTO();
}
