package com.bradesco.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bradesco.repository.RedefinicaoSenhaRepository;
import com.bradesco.repository.UsuarioRepository;

public class RecuperacaoSenhaServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private RedefinicaoSenhaRepository redefinicaoRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private EmailService emailService;

  private RecuperacaoSenhaService recuperacaoSenhaService;

  @BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);

    recuperacaoSenhaService = new RecuperacaoSenhaService(
        usuarioRepository,
        redefinicaoRepository,
        passwordEncoder,
        emailService,
        "http://localhost:5173",
        30
    );
}
}
