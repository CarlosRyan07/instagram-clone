package br.edu.ifpb.instagram.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.model.response.LoginResponse;
import br.edu.ifpb.instagram.model.response.UserDetailsResponse;
import br.edu.ifpb.instagram.service.UserService;
import br.edu.ifpb.instagram.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthControllerTest {

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        
        MockitoAnnotations.openMocks(this);

        authController = new AuthController(authService, userService);
    }

    @Test
    void testSignIn() {
        LoginRequest loginRequest = new LoginRequest("testUser", "password123");
        String fakeToken = "fake-jwt-token"; // Simulando um token falso para o teste

        
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(fakeToken);

       
        ResponseEntity<LoginResponse> response = authController.signIn(loginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().username());
        assertEquals(fakeToken, response.getBody().token());
    }

    @Test
    void testSignUp() {
        UserDetailsRequest userDetailsRequest = new UserDetailsRequest(null, 
                "Test User", "testUser", "test@example.com", "password123");

        UserDto userDto = new UserDto(1L, "Test User", "testUser", "test@example.com", "password123", null);

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        UserDetailsResponse response = authController.signUp(userDetailsRequest);

        assertNotNull(response);
        assertEquals(1L, response.id()); 
        assertEquals("Test User", response.fullName());
        assertEquals("testUser", response.username());
        assertEquals("test@example.com", response.email());
    }
}
