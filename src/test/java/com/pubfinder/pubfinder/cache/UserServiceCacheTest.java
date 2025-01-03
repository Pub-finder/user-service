package com.pubfinder.pubfinder.cache;

import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.dto.UserDto;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.service.UserService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.jpa.database-platform=",
        "spring.jpa.hibernate.ddl-auto=none",
})
public class UserServiceCacheTest {
    @Autowired
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testGetUser_CacheMiss() throws ResourceNotFoundException {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userRepository.findById(id1)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser(id1)));
        when(userRepository.findById(id2)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser(id2)));

        userService.getUser(id1);
        userService.getUser(id2);

        verify(userRepository, times(1)).findById(id1);
        verify(userRepository, times(1)).findById(id2);
    }

    @Test
    public void testGetUser_CacheHit() throws ResourceNotFoundException {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser(id)));

        UserDto result1 = userService.getUser(id);
        UserDto result2 = userService.getUser(id);

        assertEquals(result1, result2);

        verify(userRepository, times(1)).findById(id);
    }

}
