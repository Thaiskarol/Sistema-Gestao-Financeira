
package com.bradesco.service;

import com.bradesco.dto.request.LoginRequestDTO;
import com.bradesco.dto.request.UsuarioCadastroDTO;
import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ConflitoException;
import com.bradesco.exception.CredenciaisInvalidasException;
import com.bradesco.repository.UsuarioRepository;
import com.bradesco.dto.request.UsuarioAtualizacaoDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.dto.request.SenhaAlteracaoDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioCadastroDTO novoCadastro() {
        return new UsuarioCadastroDTO(
                "William",
                "12345678901",
                LocalDate.of(2000, 5, 10),
                "william@email.com",
                "senha123"
        );
    }

    private Usuario usuarioExistente() {
        Usuario usuario = new Usuario();

        ReflectionTestUtils.setField(usuario, "id", 1L);

        usuario.setNome("William");
        usuario.setCpf("12345678901");
        usuario.setDataNascimento(LocalDate.of(2000, 5, 10));
        usuario.setEmail("william@email.com");
        usuario.setSenha("hash-bcrypt");
        usuario.setDataCadastro(LocalDate.now());

        return usuario;
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        UsuarioCadastroDTO dto = novoCadastro();

        when(usuarioRepository.existsByEmail("william@email.com"))
                .thenReturn(false);
        when(usuarioRepository.existsByCpf("12345678901"))
                .thenReturn(false);
        when(passwordEncoder.encode("senha123"))
                .thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> {
                    Usuario usuario = invocacao.getArgument(0);
                    ReflectionTestUtils.setField(usuario, "id", 1L);
                    return usuario;
                });

        UsuarioResponseDTO resultado = usuarioService.cadastrar(dto);

        assertEquals(1L, resultado.id());
        assertEquals("William", resultado.nome());
        assertEquals("william@email.com", resultado.email());
        assertEquals(LocalDate.of(2000, 5, 10), resultado.dataNascimento());

        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(usuarioRepository.existsByEmail("william@email.com"))
                .thenReturn(true);

        assertThrows(ConflitoException.class,
                () -> usuarioService.cadastrar(novoCadastro()));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        when(usuarioRepository.existsByEmail("william@email.com"))
                .thenReturn(false);
        when(usuarioRepository.existsByCpf("12345678901"))
                .thenReturn(true);

        assertThrows(ConflitoException.class,
                () -> usuarioService.cadastrar(novoCadastro()));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveRealizarLoginComCredenciaisValidas() {
        Usuario usuario = usuarioExistente();

        when(usuarioRepository.findByEmail("william@email.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hash-bcrypt"))
                .thenReturn(true);

        UsuarioResponseDTO resultado = usuarioService.login(
                new LoginRequestDTO("william@email.com", "senha123")
        );

        assertEquals(1L, resultado.id());
        assertEquals("William", resultado.nome());
        assertEquals("william@email.com", resultado.email());

        verify(passwordEncoder).matches("senha123", "hash-bcrypt");
    }

    @Test
    void deveLancarExcecaoQuandoSenhaEstiverIncorreta() {
        Usuario usuario = usuarioExistente();

        when(usuarioRepository.findByEmail("william@email.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaerrada", "hash-bcrypt"))
                .thenReturn(false);

        CredenciaisInvalidasException erro = assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.login(
                        new LoginRequestDTO("william@email.com", "senhaerrada")
                )
        );

        assertEquals("E-mail ou senha inválidos.", erro.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExistir() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        CredenciaisInvalidasException erro = assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.login(
                        new LoginRequestDTO("inexistente@email.com", "senha123")
                )
        );

        assertEquals("E-mail ou senha inválidos.", erro.getMessage());

        verify(passwordEncoder, never()).matches(any(), any());
    }


      @Test
      void deveBuscarUsuarioPorId() {
          Usuario usuario = usuarioExistente();

          when(usuarioRepository.findById(1L))
                  .thenReturn(Optional.of(usuario));

          UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

          assertEquals(1L, resultado.id());
          assertEquals("William", resultado.nome());
          assertEquals("william@email.com", resultado.email());

          verify(usuarioRepository).findById(1L);
      }

      @Test
      void deveLancarExcecaoQuandoUsuarioNaoExistirNaBusca() {
          when(usuarioRepository.findById(99L))
                  .thenReturn(Optional.empty());

          assertThrows(
                  ResourceNotFoundException.class,
                  () -> usuarioService.buscarPorId(99L)
          );
      }

      @Test
      void deveAtualizarDadosDoPerfil() {
          Usuario usuario = usuarioExistente();

          UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO(
                  "William Atualizado",
                  "NOVO@email.com",
                  "81999999999",
                  LocalDate.of(2000, 6, 15)
          );

          when(usuarioRepository.findById(1L))
                  .thenReturn(Optional.of(usuario));
          when(usuarioRepository.existsByEmail("novo@email.com"))
                  .thenReturn(false);
          when(usuarioRepository.save(any(Usuario.class)))
                  .thenAnswer(invocacao -> invocacao.getArgument(0));

          UsuarioResponseDTO resultado = usuarioService.atualizar(1L, dto);

          assertEquals("William Atualizado", resultado.nome());
          assertEquals("novo@email.com", resultado.email());
          assertEquals("81999999999", resultado.telefone());
          assertEquals(LocalDate.of(2000, 6, 15), resultado.dataNascimento());

          assertEquals("12345678901", usuario.getCpf());
          assertEquals("hash-bcrypt", usuario.getSenha());

          verify(usuarioRepository).save(usuario);
      }

      @Test
      void devePermitirManterOProprioEmailNaAtualizacao() {
          Usuario usuario = usuarioExistente();

          UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO(
                  "William Atualizado",
                  "william@email.com",
                  "81999999999",
                  LocalDate.of(2000, 6, 15)
          );

          when(usuarioRepository.findById(1L))
                  .thenReturn(Optional.of(usuario));
          when(usuarioRepository.save(any(Usuario.class)))
                  .thenAnswer(invocacao -> invocacao.getArgument(0));

          UsuarioResponseDTO resultado = usuarioService.atualizar(1L, dto);

          assertEquals("william@email.com", resultado.email());

          verify(usuarioRepository, never()).existsByEmail(any());
          verify(usuarioRepository).save(usuario);
      }

      @Test
      void deveLancarExcecaoQuandoEmailJaPertencerAOutroUsuario() {
          Usuario usuario = usuarioExistente();

          UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO(
                  "William",
                  "outro@email.com",
                  "81999999999",
                  LocalDate.of(2000, 6, 15)
          );

          when(usuarioRepository.findById(1L))
                  .thenReturn(Optional.of(usuario));
          when(usuarioRepository.existsByEmail("outro@email.com"))
                  .thenReturn(true);

          assertThrows(
                  ConflitoException.class,
                  () -> usuarioService.atualizar(1L, dto)
          );

          verify(usuarioRepository, never()).save(any());
      }


        @Test
        void deveAlterarSenhaComSenhaAtualCorreta() {
        Usuario usuario = usuarioExistente();

        SenhaAlteracaoDTO dto = new SenhaAlteracaoDTO(
                "senha123",
                "novaSenha456"
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hash-bcrypt"))
                .thenReturn(true);
        when(passwordEncoder.encode("novaSenha456"))
                .thenReturn("novo-hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        assertDoesNotThrow(() -> usuarioService.alterarSenha(1L, dto));

        assertEquals("novo-hash-bcrypt", usuario.getSenha());

        verify(passwordEncoder).encode("novaSenha456");
        verify(usuarioRepository).save(usuario);
        }

        @Test
        void deveLancarExcecaoQuandoSenhaAtualEstiverIncorreta() {
        Usuario usuario = usuarioExistente();

        SenhaAlteracaoDTO dto = new SenhaAlteracaoDTO(
                "senhaerrada",
                "novaSenha456"
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaerrada", "hash-bcrypt"))
                .thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> usuarioService.alterarSenha(1L, dto)
        );

        verify(passwordEncoder, never()).encode(any());
        verify(usuarioRepository, never()).save(any());
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoExistirNaAlteracaoDeSenha() {
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        SenhaAlteracaoDTO dto = new SenhaAlteracaoDTO(
                "senha123",
                "novaSenha456"
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.alterarSenha(99L, dto)
        );

        verify(passwordEncoder, never()).matches(any(), any());
        verify(usuarioRepository, never()).save(any());
        }

}
