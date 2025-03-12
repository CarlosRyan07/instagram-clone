package br.edu.ifpb.instagram.security;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateToken_Success() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList());
        String token = jwtUtils.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList());
        String token = jwtUtils.generateToken(authentication);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.value";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    void testGetUsernameFromToken_Success() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList());
        String token = jwtUtils.generateToken(authentication);

        String username = jwtUtils.getUsernameFromToken(token);

        assertEquals("testUser", username);
    }
}
