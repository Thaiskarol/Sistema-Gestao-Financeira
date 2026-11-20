package com.bradesco.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.request.EsqueciSenhaDTO;
import com.bradesco.dto.request.RedefinirSenhaDTO;
import com.bradesco.entity.RedefinicaoSenha;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.TokenInvalidoException;
import com.bradesco.repository.RedefinicaoSenhaRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class RecuperacaoSenhaService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacaoSenhaService.class);
    private static final String MSG_TOKEN_INVALIDO = "Link inválido ou expirado.";

    private final UsuarioRepository usuarioRepository;
    private final RedefinicaoSenhaRepository redefinicaoSenhaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String frontendUrl;
    private final long validadeMinutos;

    public RecuperacaoSenhaService(UsuarioRepository usuarioRepository,
                                   RedefinicaoSenhaRepository redefinicaoSenhaRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService,
                                   @Value("${app.frontend-url}") String frontendUrl,
                                   @Value("${app.reset-senha.validade-minutos}") long validadeMinutos) {
        this.usuarioRepository = usuarioRepository;
        this.redefinicaoSenhaRepository = redefinicaoSenhaRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
        this.validadeMinutos = validadeMinutos;
    }

    // Nunca revela se o e-mail existe: se não existir, simplesmente não faz nada.
    @Transactional
    public void solicitar(EsqueciSenhaDTO dto) {
        String email = dto.email().trim().toLowerCase();

        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            redefinicaoSenhaRepository.findByUsuarioIdAndUsadoFalse(usuario.getId())
                    .forEach(anterior -> anterior.setUsado(true));

            String token = gerarToken();
            LocalDateTime agora = LocalDateTime.now();

            RedefinicaoSenha redefinicao = new RedefinicaoSenha();
            redefinicao.setTokenHash(hash(token));
            redefinicao.setCriadoEm(agora);
            redefinicao.setExpiraEm(agora.plusMinutes(validadeMinutos));
            redefinicao.setUsado(false);
            redefinicao.setUsuario(usuario);
            redefinicaoSenhaRepository.save(redefinicao);

            enviarEmail(usuario, token);
        });
    }

    @Transactional
    public void redefinir(RedefinirSenhaDTO dto) {
        RedefinicaoSenha redefinicao = redefinicaoSenhaRepository
                .findByTokenHash(hash(dto.token().trim()))
                .orElseThrow(() -> new TokenInvalidoException(MSG_TOKEN_INVALIDO));

        if (redefinicao.isUsado() || redefinicao.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new TokenInvalidoException(MSG_TOKEN_INVALIDO);
        }

        Usuario usuario = redefinicao.getUsuario();
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);

        redefinicao.setUsado(true);
        redefinicaoSenhaRepository.findByUsuarioIdAndUsadoFalse(usuario.getId())
                .forEach(outra -> outra.setUsado(true));
    }

    private void enviarEmail(Usuario usuario, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;
        String texto = "Olá, " + usuario.getNome() + "!\n\n"
                + "Recebemos um pedido para redefinir a sua senha. "
                + "Acesse o link abaixo (válido por " + validadeMinutos + " minutos):\n\n"
                + link + "\n\n"
                + "Se você não fez esse pedido, ignore este e-mail.";
        try {
            emailService.enviar(usuario.getEmail(), "Redefinição de senha", texto);
        } catch (MailException e) {
            // Falha no envio não pode revelar nada ao usuário: só registra
            log.error("Falha ao enviar e-mail de redefinição de senha.", e);
        }
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível.", e);
        }
    }
}