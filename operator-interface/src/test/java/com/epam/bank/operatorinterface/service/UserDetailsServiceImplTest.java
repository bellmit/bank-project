package com.epam.bank.operatorinterface.service;

import com.epam.bank.operatorinterface.domain.UserDetailsAuthImpl;
import com.epam.bank.operatorinterface.entity.Role;
import com.epam.bank.operatorinterface.entity.User;
import com.epam.bank.operatorinterface.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImplMock;

    private User userEntity;

    @BeforeEach
    public void setUp() {
        this.userEntity = new User(
            1L,
            RandomStringUtils.random(5),
            RandomStringUtils.random(5),
            RandomStringUtils.randomNumeric(9),
            RandomStringUtils.random(6),
            RandomStringUtils.random(5),
            RandomStringUtils.random(5),
            Collections.emptyList(),
            true,
            0,
            Set.of(new Role(1L, "ROLE_ADMIN"))
        );
    }

    @Test
    public void getUserByEmailShouldReturnUserIfEmailIsCorrect() {
        String userEmail = userEntity.getEmail();
        Mockito
            .when(userRepositoryMock.getUserByEmail(userEmail))
            .thenReturn(Optional.of(userEntity));

        Optional<User> optionalUser = userRepositoryMock.getUserByEmail(userEmail);

        Assertions.assertEquals(userEntity, optionalUser.get());
    }

    @Test
    public void getUserByEmailShouldReturnNullIfEmailIsIncorrect() {
        String userEmail = RandomStringUtils.random(4);
        Mockito
            .when(userRepositoryMock.getUserByEmail(userEmail))
            .thenReturn(Optional.empty());

        Optional<User> optionalUser = userRepositoryMock.getUserByEmail(userEmail);

        Assertions.assertTrue(!optionalUser.isPresent());
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsIfEmailIsCorrect() {
        String userEmail = userEntity.getEmail();
        Mockito
            .when(userRepositoryMock.getUserByEmail(userEmail))
            .thenReturn(Optional.of(userEntity));


        UserDetails testUserDetails = new UserDetailsAuthImpl(
            userEntity.getPassword(),
            userEntity.getEmail(),
            userEntity.getRoles(),
            userEntity.isEnabled());
        UserDetails result = userDetailsServiceImplMock.loadUserByUsername(userEmail);

        Assertions.assertEquals(testUserDetails.getUsername(), result.getUsername());
    }

    @Test
    public void loadUseByUsernameShouldThrowsExceptionIfEmailIsNotCorrect() {
        String userEmail = RandomStringUtils.random(4);
        Mockito
            .when(userRepositoryMock.getUserByEmail(userEmail))
            .thenReturn(Optional.empty());

        UsernameNotFoundException notFoundException = Assertions.assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsServiceImplMock.loadUserByUsername(userEmail));

        Assertions.assertEquals(
            String.format("User with Email %s not found", userEmail),
            notFoundException.getMessage());
    }
}