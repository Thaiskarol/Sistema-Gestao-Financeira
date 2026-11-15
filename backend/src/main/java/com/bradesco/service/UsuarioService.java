package com.bradesco.service;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.request.SenhaAlteracaoDTO;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.dto.request.UsuarioAtualizacaoDTO;
import com.bradesco.dto.request.LoginRequestDTO;
import com.bradesco.dto.request.SenhaAlteracaoDTO;
import com.bradesco.dto.request.UsuarioCadastroDTO;
import com.bradesco.dto.response.UsuarioResponseDTO;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ConflitoException;
import com.bradesco.exception.CredenciaisInvalidasException;
import com.bradesco.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {

        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }

        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new ConflitoException("CPF já cadastrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setDataCadastro(LocalDate.now());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return toResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO login(LoginRequestDTO dto) {

        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException(
                        "E-mail ou senha inválidos."
                ));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos."
            );
        }

        return toResponse(usuario);
    }


    @Transactional
    public UsuarioResponseDTO atualizar(Long usuarioId, UsuarioAtualizacaoDTO dto) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado.")
                );

        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        if (!usuario.getEmail().equalsIgnoreCase(email)
                && usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(email);
        usuario.setTelefone(dto.telefone());
        usuario.setDataNascimento(dto.dataNascimento());

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return toResponse(usuarioAtualizado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado.")
                );

        return toResponse(usuario);
    }


    @Transactional
    public void alterarSenha(Long usuarioId, SenhaAlteracaoDTO dto) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado."
                ));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException("Senha atual incorreta.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));

        usuarioRepository.save(usuario);
    }


    private UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataNascimento(),
                usuario.getTelefone()
        );
    }
}