package com.pubfinder.pubfinder.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pubfinder.pubfinder.db.UserRepository;
import com.pubfinder.pubfinder.exception.ResourceNotFoundException;
import com.pubfinder.pubfinder.models.User;
import com.pubfinder.pubfinder.service.UserService;
import com.pubfinder.pubfinder.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(properties = {
        "spring.datasource.url=",
        "spring.jpa.database-platform=",
        "spring.jpa.hibernate.ddl-auto=none",
})
public class UserServiceCacheTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testGetUser_CacheMiss() throws ResourceNotFoundException {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userRepository.findById(id1)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser()));
        when(userRepository.findById(id2)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser()));

        userService.getUser(id1);
        userService.getUser(id2);

        verify(userRepository, times(1)).findById(id1);
        verify(userRepository, times(1)).findById(id2);
    }

    @Test
    public void testGetUser_CacheHit() throws ResourceNotFoundException {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(
                Optional.ofNullable(TestUtil.generateMockUser()));

        User result1 = userService.getUser(id);
        User result2 = userService.getUser(id);

        assertEquals(result1, result2);

        verify(userRepository, times(1)).findById(id);
    }

}
