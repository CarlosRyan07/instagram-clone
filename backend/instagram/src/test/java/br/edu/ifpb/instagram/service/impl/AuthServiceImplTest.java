package br.edu.ifpb.instagram.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testAuthenticateSuccess() {
        // Cenário: autenticação bem-sucedida
        String username = "user123";
        String password = "pass123";
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Cria um mock para a interface Authentication
        Authentication authentication = mock(Authentication.class);

        // Configura o comportamento do authenticationManager para retornar a
        // autenticação mockada
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // Configura o jwtUtils para gerar um token a partir da autenticação
        String expectedToken = "encodedToken123";
        when(jwtUtils.generateToken(authentication)).thenReturn(expectedToken);

        // Executa o método sob teste
        String actualToken = authService.authenticate(loginRequest);

        // Valida que o token retornado é o esperado
        assertEquals(expectedToken, actualToken);

        // Verifica se o AuthenticationManager foi chamado com o token contendo as
        // credenciais corretas
        ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor = ArgumentCaptor
                .forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
        assertEquals(username, capturedToken.getPrincipal());
        assertEquals(password, capturedToken.getCredentials());
    }

    @Test
    public void testAuthenticateFailure() {
        // Cenário: falha na autenticação (credenciais inválidas)
        String username = "user123";
        String password = "wrongPassword";
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Configura o authenticationManager para lançar uma exceção quando as
        // credenciais forem inválidas
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Verifica que a exceção é lançada e que o jwtUtils nunca é chamado
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(loginRequest);
        });
        verify(jwtUtils, never()).generateToken(any(Authentication.class));
    }
}
