package br.edu.ifpb.instagram.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_Success() {
        // Arrange
        UserDto userDto = new UserDto(null, "João Silva", "joaos", "joao@example.com", "role", "senha123");
        UserEntity userEntityToSave = new UserEntity();
        userEntityToSave.setUsername(userDto.username());
        userEntityToSave.setEmail(userDto.email());
        userEntityToSave.setFullName(userDto.fullName());
        userEntityToSave.setEncryptedPassword("encodedSenha");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setUsername(userDto.username());
        savedEntity.setEmail(userDto.email());
        savedEntity.setFullName(userDto.fullName());
        savedEntity.setEncryptedPassword("encodedSenha");

        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedSenha");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Act
        UserDto result = userService.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("João Silva", result.fullName());
        assertEquals("joaos", result.username());
        assertEquals("joao@example.com", result.email());
        verify(passwordEncoder, times(1)).encode(userDto.password());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdateUser_WithPassword_Success() {

        UserDto userDto = new UserDto(1L, "Maria Silva", "marias", "maria@example.com", "novaSenha", null);
        when(passwordEncoder.encode("novaSenha")).thenReturn("encodedNovaSenha");
        when(userRepository.updatePartialUser(anyString(), anyString(), anyString(), any(), anyLong())).thenReturn(1);

        // Act
        UserDto result = userService.updateUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Maria Silva", result.fullName());
        assertEquals("marias", result.username());
        assertEquals("maria@example.com", result.email());
        // Verifica se a senha foi codificada corretamente
        assertEquals("encodedNovaSenha", result.encryptedPassword());
        verify(passwordEncoder, times(1)).encode("novaSenha");
        verify(userRepository, times(1))
            .updatePartialUser("Maria Silva", "maria@example.com", "marias", "encodedNovaSenha", 1L);
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsException() {
        // Arrange
        // Correção: o 5º parâmetro deve ser a senha em texto puro
        UserDto userDto = new UserDto(1L, "João Silva", "joaos", "joao@example.com", "senha123", null);
        when(passwordEncoder.encode("senha123")).thenReturn("encodedSenha123");
        when(userRepository.updatePartialUser(anyString(), anyString(), anyString(), any(), anyLong())).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userDto);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1))
            .updatePartialUser("João Silva", "joao@example.com", "joaos", "encodedSenha123", 1L);
    }


    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = 1L;

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFullName("Paulo Pereira");
        userEntity.setUsername("paulop");
        userEntity.setEmail("paulo@example.com");
        userEntity.setEncryptedPassword("encryptedPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        UserDto result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Paulo Pereira", result.fullName());
        assertEquals("paulop", result.username());
        assertEquals("paulo@example.com", result.email());
        assertEquals("encryptedPass", result.encryptedPassword());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_UserNotFound_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFullName("User One");
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setEncryptedPassword("pass1");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("User Two");
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setEncryptedPassword("pass2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User One", result.get(0).fullName());
        assertEquals("user1", result.get(0).username());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_NoUsers_ThrowsException() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findAll();
        });
        assertEquals("Users not found", exception.getMessage());
        verify(userRepository, times(1)).findAll();
    }
}
