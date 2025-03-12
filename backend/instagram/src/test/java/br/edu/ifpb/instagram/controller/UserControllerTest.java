package br.edu.ifpb.instagram.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.model.response.UserDetailsResponse;
import br.edu.ifpb.instagram.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        // garantindo que os mocks sejam inicializados 🙌
    }

    @Test
    void testBuscarUsuarios() {
        //toda preparação dos usuários é aqui 👌
        UserDto userDto1 = new UserDto(1L, "Usuário Um", "usuario1", "usuario1@example.com", "senha123", null);
        UserDto userDto2 = new UserDto(2L, "Usuário Dois", "usuario2", "usuario2@example.com", "senha123", null);
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);
        
        when(userService.findAll()).thenReturn(userDtos);

        List<UserDetailsResponse> response = userController.getUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Usuário Um", response.get(0).fullName());
        assertEquals("usuario1@example.com", response.get(0).email());
    }

    @Test
    void testBuscarUsuario() {
        UserDto userDto = new UserDto(1L, "Usuário Um", "usuario1", "usuario1@example.com", "senha123", null);
        when(userService.findById(1L)).thenReturn(userDto);

        UserDetailsResponse response = userController.getUser(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Usuário Um", response.fullName());
        assertEquals("usuario1", response.username());
    }

    @Test
    void testAtualizarUsuario() {
        UserDetailsRequest request = new UserDetailsRequest(1L, "Usuário Atualizado", "usuarioatualizado", "atualizado@example.com", "novasenha");
        UserDto userDto = new UserDto(1L, "Usuário Atualizado", "usuarioatualizado", "atualizado@example.com", "novasenha", null);
        when(userService.updateUser(any(UserDto.class))).thenReturn(userDto);

        
        UserDetailsResponse response = userController.updateUser(request);

        assertNotNull(response);
        assertEquals("Usuário Atualizado", response.fullName());
        assertEquals("usuarioatualizado", response.username());
    }

    @Test
    void testDeletarUsuario() {
        doNothing().when(userService).deleteUser(1L);

        String response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals("user was deleted!", response);

        verify(userService, times(1)).deleteUser(1L);
    }
}
