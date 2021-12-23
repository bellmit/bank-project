package com.epam.bank.operatorinterface.util;

import com.epam.bank.operatorinterface.domain.UserDetailsAuthImpl;
import com.epam.bank.operatorinterface.entity.Role;
import com.epam.bank.operatorinterface.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil testingJwtUtil;
    private User testUserEntity;
    private String testSecretKey;
    private String testToken;

    @BeforeEach
    public void setUp() {
        testingJwtUtil = new JwtUtil();
        testUserEntity = new User(
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
        testSecretKey = "testSecret";
        testToken = generateToken(new UserDetailsAuthImpl(testUserEntity));

        Class jwtUtilClass = testingJwtUtil.getClass();
        try {
            Field secretKeyField = jwtUtilClass.getDeclaredField("secretKey");
            secretKeyField.setAccessible(true);
            secretKeyField.set(testingJwtUtil, testSecretKey);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void generateTokenShouldReturnNewToken() {
        String result = testingJwtUtil.generateToken(new UserDetailsAuthImpl(testUserEntity));

        Assertions.assertEquals(testToken, result);
    }

    private String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SignatureAlgorithm.HS512, testSecretKey)
            .compact();
    }
}