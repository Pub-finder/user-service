package com.pubfinder.pubfinder.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubfinder.pubfinder.dto.AuthenticationResponse;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.service.UserService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @MockBean
  private UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void registerUserTest() throws Exception {
    UserDto userDTO = TestUtil.generateMockUserDTO();
    AuthenticationResponse response = TestUtil.generateMockAuthenticationResponse();
    when(userService.registerUser(TestUtil.generateMockUser())).thenReturn(response);

    mockMvc.perform(post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isCreated());
  }

  @Test
  public void deleteUserTest() throws Exception {
    doNothing().when(userService).delete(any(), any());
    mockMvc.perform(delete("/user/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void editUserTest() throws Exception {
    when(userService.edit(any(), any())).thenReturn(TestUtil.generateMockUserDTO());
    mockMvc.perform(put("/user/edit", TestUtil.generateMockUserDTO())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk());
  }

  @Test
  public void loginTest() throws Exception {
    AuthenticationResponse response = TestUtil.generateMockAuthenticationResponse();
    when(userService.login(any())).thenReturn(response);
    mockMvc.perform(post("/user/login", response)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isOk());
  }

  UserDto user = TestUtil.generateMockUserDTO();
}
