
package com.bradesco.controller;

import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.exception.CredenciaisInvalidasException;
import com.bradesco.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private String jsonLogin(String email, String senha) {
        return """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(email, senha);
    }

    @Test
    void deveRetornar200QuandoLoginValido() throws Exception {
        when(usuarioService.login(any())).thenReturn(
                new UsuarioResponseDTO(
                        1L,
                        "William",
                        "william@email.com",
                        LocalDate.of(2000, 5, 10),
                        null
                )
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin("william@email.com", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso."))
                .andExpect(jsonPath("$.data.nome").value("William"))
                .andExpect(jsonPath("$.data.email").value("william@email.com"));
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin("email-invalido", "senha123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        when(usuarioService.login(any()))
                .thenThrow(new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin("william@email.com", "senhaerrada")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("E-mail ou senha inválidos."));
    }
}
