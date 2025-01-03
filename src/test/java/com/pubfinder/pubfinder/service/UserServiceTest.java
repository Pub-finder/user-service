package com.pubfinder.pubfinder.service;

import com.pubfinder.pubfinder.db.TokenRepository;
import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.FollowDto;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.BadRequestException;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.mapper.Mapper;
import com.pubfinder.pubfinder.models.Token;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.util.TestUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.cache.type=none",
        "bucket4j.enabled=false",
        "spring.datasource.url=",
        "spring.jpa.database-platform=",
        "spring.jpa.hibernate.ddl-auto=none"
})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private HttpServletRequest request;

    @Test
    public void registerUserTest() throws BadRequestException {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);
        when(tokenRepository.save(any())).thenReturn(new Token());

        userService.registerUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void registerUserTestBadRequest() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));
        assertThrows(BadRequestException.class, () -> userService.registerUser(user));
    }

    @Test
    public void deleteUserTest() throws ResourceNotFoundException {
        doNothing().when(userRepository).delete(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
        doNothing().when(tokenRepository).delete(token);

        userService.delete(user);
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).delete(any());
        verify(tokenRepository, times(1)).findAllTokensByUser(any());
        verify(tokenRepository, times(1)).delete(any());
    }

    @Test
    public void deleteUserTestResourceNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(user));
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void editUserTest() throws BadRequestException, ResourceNotFoundException {
        User editedUser = TestUtil.generateMockUser(UUID.randomUUID());
        editedUser.setUsername("Something else");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(editedUser);

        when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
        doNothing().when(tokenRepository).delete(token);

        UserDto result = userService.edit(editedUser);
        assertEquals(Mapper.INSTANCE.entityToDto(editedUser), result);
        verify(userRepository, times(1)).save(editedUser);
    }

    @Test
    public void editUserTestBadRequest() {
        assertThrows(BadRequestException.class, () -> userService.edit(null));
    }

    @Test
    public void editUserTestResourceNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.edit(user));
    }

    @Test
    public void revokeUserAccessTest() throws ResourceNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(tokenRepository.findAllTokensByUser(user.getId())).thenReturn(List.of(token));
        doNothing().when(tokenRepository).delete(token);

        userService.revokeUserAccess(user.getId());

        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    public void revokeUserAccessTestResourceNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.revokeUserAccess(user.getId()));
    }

    @Test
    public void getUserTest() throws ResourceNotFoundException {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(user.getId());
        assertEquals(result, Mapper.INSTANCE.entityToDto(user));
    }

    @Test
    public void getUserTest_NotFound() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    public void followTest() throws ResourceNotFoundException {
        User utf = TestUtil.generateMockUser(UUID.randomUUID());
        FollowDto followDto = TestUtil.generateFollowDto(user, utf);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(utf.getId())).thenReturn(Optional.of(utf));

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(utf)).thenReturn(utf);

        userService.follow(followDto);

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(utf);
    }

    @Test
    public void followTest_NotFound() {
        User user = TestUtil.generateMockUser(UUID.randomUUID());
        User utf = TestUtil.generateMockUser(UUID.randomUUID());

        FollowDto followDto = FollowDto.builder()
                .userId(user.getId())
                .followId(utf.getId())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.follow(followDto));
    }

    @Test
    public void getFollowers() throws ResourceNotFoundException {
        User user = TestUtil.generateMockUser(UUID.randomUUID());

        for (int i = 0; i < 10; i++) {
            user.getFollowers().add(TestUtil.generateMockUser(UUID.randomUUID()));
        }
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        List<UserDto> followers = userService.getFollowers(user.getId());
        assertEquals(10, followers.size());
        verify(userRepository).findById(user.getId());
    }

    @Test
    public void getFollowers_NotFound() {
        User user = TestUtil.generateMockUser(UUID.randomUUID());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getFollowers(user.getId()));
    }

    @Test
    public void unFollowers() throws ResourceNotFoundException {
        User utuf = TestUtil.generateMockUser(UUID.randomUUID());
        FollowDto followDto = TestUtil.generateFollowDto(user, utuf);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findById(utuf.getId())).thenReturn(Optional.of(utuf));

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(utuf)).thenReturn(utuf);

        userService.unfollow(followDto);

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(utuf);
    }

    private final User user = TestUtil.generateMockUser(UUID.randomUUID());
    private final Token token = TestUtil.generateMockToken(user);
}
