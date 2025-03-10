package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.InstagramApplication;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Usado para manipular o persistence context e garantir que as operações de
    // update sejam refletidas nas consultas
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Método auxiliar para criação de instâncias de UserEntity.
     */
    private UserEntity criarUsuario(String fullName, String email, String username, String encryptedPassword) {
        UserEntity usuario = new UserEntity();
        usuario.setFullName(fullName);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setEncryptedPassword(encryptedPassword);
        return usuario;
    }

    @Test
    @DisplayName("Teste findByUsername - usuário existente")
    public void testFindByUsernameFound() {
        // Configuração: cria e persiste um usuário
        UserEntity usuario = criarUsuario("João Silva", "joao@example.com", "joaosilva", "senha123");
        usuario = userRepository.save(usuario);
        entityManager.flush();
        entityManager.clear();

        // Ação: busca o usuário pelo username
        Optional<UserEntity> encontrado = userRepository.findByUsername("joaosilva");

        // Validação: verifica se o usuário foi encontrado e os dados estão corretos
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("joao@example.com");
    }

    @Test
    @DisplayName("Teste findByUsername - usuário inexistente")
    public void testFindByUsernameNotFound() {
        Optional<UserEntity> encontrado = userRepository.findByUsername("inexistente");
        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Teste findAll - retorno de todos os usuários")
    public void testFindAll() {
        UserEntity usuario1 = criarUsuario("João Silva", "joao@example.com", "joaosilva", "senha123");
        UserEntity usuario2 = criarUsuario("Maria Souza", "maria@example.com", "mariasouza", "senha456");
        userRepository.save(usuario1);
        userRepository.save(usuario2);
        entityManager.flush();
        entityManager.clear();

        List<UserEntity> usuarios = userRepository.findAll();
        assertThat(usuarios).hasSize(2);
    }

    @Test
    @DisplayName("Teste updatePartialUser - atualização completa")
    public void testUpdatePartialUserAllFields() {
        UserEntity usuario = criarUsuario("João Silva", "joao@example.com", "joaosilva", "senha123");
        usuario = userRepository.save(usuario);
        Long id = usuario.getId();

        int linhasAfetadas = userRepository.updatePartialUser("João Atualizado", "joao.atualizado@example.com",
                "joaonovo", "novaSenha", id);

        entityManager.flush();
        entityManager.clear();

        assertThat(linhasAfetadas).isEqualTo(1);
        Optional<UserEntity> atualizado = userRepository.findById(id);
        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getFullName()).isEqualTo("João Atualizado");
        assertThat(atualizado.get().getEmail()).isEqualTo("joao.atualizado@example.com");
        assertThat(atualizado.get().getUsername()).isEqualTo("joaonovo");
        assertThat(atualizado.get().getEncryptedPassword()).isEqualTo("novaSenha");
    }

    @Test
    @DisplayName("Teste updatePartialUser - atualização parcial (alguns campos nulos)")
    public void testUpdatePartialUserPartialFields() {
        UserEntity usuario = criarUsuario("João Silva", "joao@example.com", "joaosilva", "senha123");
        usuario = userRepository.save(usuario);
        Long id = usuario.getId();

        int linhasAfetadas = userRepository.updatePartialUser(null, "novo.joao@example.com",
                "novojoao", null, id);

        entityManager.flush();
        entityManager.clear();

        assertThat(linhasAfetadas).isEqualTo(1);
        Optional<UserEntity> atualizado = userRepository.findById(id);
        assertThat(atualizado).isPresent();
        assertThat(atualizado.get().getFullName()).isEqualTo("João Silva");
        assertThat(atualizado.get().getEmail()).isEqualTo("novo.joao@example.com");
        assertThat(atualizado.get().getUsername()).isEqualTo("novojoao");
        assertThat(atualizado.get().getEncryptedPassword()).isEqualTo("senha123");
    }

    @Test
    @DisplayName("Teste updatePartialUser - ID inexistente")
    public void testUpdatePartialUserInvalidId() {
        int linhasAfetadas = userRepository.updatePartialUser("Nome", "email@example.com",
                "username", "senha", 999L);
        assertThat(linhasAfetadas).isEqualTo(0);
    }
}