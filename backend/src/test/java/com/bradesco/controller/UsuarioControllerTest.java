
package com.bradesco.controller;

import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.exception.ConflitoException;
import com.bradesco.service.UsuarioService;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.exception.CredenciaisInvalidasException;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private String jsonCadastro(String cpf, String email) {
        return """
                {
                  "nome": "William",
                  "cpf": "%s",
                  "dataNascimento": "2000-05-10",
                  "email": "%s",
                  "senha": "senha123"
                }
                """.formatted(cpf, email);
    }


    private String jsonAtualizacao(String email) {
        return """
                {
                  "nome": "William Atualizado",
                  "email": "%s",
                  "telefone": "81999999999",
                  "dataNascimento": "%s"
                }
                """.formatted(email, LocalDate.now().minusYears(26));
    }


    @Test
    void deveRetornar201QuandoCadastroValido() throws Exception {
        when(usuarioService.cadastrar(any())).thenReturn(
                new UsuarioResponseDTO(
                        1L,
                        "William",
                        "william@email.com",
                        LocalDate.of(2000, 5, 10),
                        null
                )
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("12345678901", "william@email.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuário cadastrado com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("William"))
                .andExpect(jsonPath("$.data.email").value("william@email.com"));
    }

    @Test
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("1234567890", "william@email.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("12345678901", "email-invalido")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        when(usuarioService.cadastrar(any()))
                .thenThrow(new ConflitoException("E-mail já cadastrado."));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCadastro("12345678901", "william@email.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado."));
    }


    @Test
    void deveRetornar200QuandoBuscarUsuarioExistente() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(
                new UsuarioResponseDTO(
                        1L,
                        "William",
                        "william@email.com",
                        LocalDate.of(2000, 5, 10),
                        null
                )
        );

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("William"));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistirNaBusca() throws Exception {
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Usuário não encontrado."));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar200QuandoAtualizarPerfilValido() throws Exception {
        when(usuarioService.atualizar(any(), any())).thenReturn(
                new UsuarioResponseDTO(
                        1L,
                        "William Atualizado",
                        "novo@email.com",
                        LocalDate.now().minusYears(26),
                        "81999999999"
                )
        );

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizacao("novo@email.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Perfil atualizado com sucesso."))
                .andExpect(jsonPath("$.data.email").value("novo@email.com"));
    }

    @Test
    void deveRetornar400QuandoAtualizacaoComEmailInvalido() throws Exception {
        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizacao("email-invalido")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar409QuandoAtualizacaoComEmailDuplicado() throws Exception {
        when(usuarioService.atualizar(any(), any()))
                .thenThrow(new ConflitoException("E-mail já cadastrado."));

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizacao("outro@email.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado."));
    }


        @Test
        void deveRetornar200QuandoAlterarSenhaComSucesso() throws Exception {
        String json = """
                {
                "senhaAtual": "senha123",
                "novaSenha": "novaSenha456"
                }
                """;

        mockMvc.perform(patch("/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Senha alterada com sucesso."));
        }

        @Test
        void deveRetornar401QuandoSenhaAtualEstiverIncorreta() throws Exception {
        doThrow(new CredenciaisInvalidasException("Senha atual incorreta."))
                .when(usuarioService).alterarSenha(eq(1L), any());

        String json = """
                {
                "senhaAtual": "senhaerrada",
                "novaSenha": "novaSenha456"
                }
                """;

        mockMvc.perform(patch("/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Senha atual incorreta."));
        }

        @Test
        void deveRetornar404QuandoUsuarioNaoExistirNaAlteracaoDeSenha() throws Exception {
        doThrow(new ResourceNotFoundException("Usuário não encontrado."))
                .when(usuarioService).alterarSenha(eq(99L), any());

        String json = """
                {
                "senhaAtual": "senha123",
                "novaSenha": "novaSenha456"
                }
                """;

        mockMvc.perform(patch("/usuarios/99/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        void deveRetornar400QuandoNovaSenhaForMuitoCurta() throws Exception {
        String json = """
                {
                "senhaAtual": "senha123",
                "novaSenha": "123"
                }
                """;

        mockMvc.perform(patch("/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        }


}
