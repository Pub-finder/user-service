package com.pubfinder.pubfinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubfinder.pubfinder.dto.FollowDto;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.service.UserService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerUserTest() throws Exception {
        when(userService.registerUser(any())).thenReturn(user.getId());

        mockMvc.perform(post("/user/register", user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-User-Id"));
    }

    @Test
    public void deleteUserTest() throws Exception {
        doNothing().when(userService).delete(any());
        mockMvc.perform(delete("/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void editUserTest() throws Exception {
        when(userService.edit(any())).thenReturn(TestUtil.generateMockUserDTO());
        mockMvc.perform(put("/user/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void revokeUserAccessTest() throws Exception {
        doNothing().when(userService).revokeUserAccess(any());
        mockMvc.perform(delete("/user/revokeUserAccess/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserTest() throws Exception {
        when(userService.getUser(user.getId())).thenReturn(TestUtil.generateMockUserDTO());
        mockMvc.perform(get("/user/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void followTest() throws Exception {
        User u = TestUtil.generateMockUser(UUID.randomUUID());
        User utf = TestUtil.generateMockUser(UUID.randomUUID());
        FollowDto followDto = TestUtil.generateFollowDto(u, utf);

        when(userService.follow(followDto)).thenReturn(user);
        mockMvc.perform(post("/user/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void unfollowTest() throws Exception {
        User u = TestUtil.generateMockUser(UUID.randomUUID());
        User utf = TestUtil.generateMockUser(UUID.randomUUID());
        FollowDto followDto = TestUtil.generateFollowDto(u, utf);

        doNothing().when(userService).unfollow(any());
        mockMvc.perform(post("/user/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(followDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getFollowersTest() throws Exception {
        UserDto utfDto = TestUtil.generateMockUserDTO();
        when(userService.getFollowers(user.getId())).thenReturn(List.of(utfDto));
        mockMvc.perform(get("/user/{id}/followers", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    UserDto user = TestUtil.generateMockUserDTO();
}
