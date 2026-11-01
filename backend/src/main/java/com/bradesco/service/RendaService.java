package com.bradesco.service;

import com.bradesco.dto.request.RendaRequestDTO;
import com.bradesco.dto.response.RendaResponseDTO;
import com.bradesco.entity.Renda;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.RendaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RendaService {

    private final RendaRepository rendaRepository;
    private final UsuarioRepository usuarioRepository;

    public RendaService(RendaRepository rendaRepository, UsuarioRepository usuarioRepository) {
        this.rendaRepository = rendaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public RendaResponseDTO cadastrar(RendaRequestDTO dados) {
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Renda renda = new Renda();
        renda.setDescricao(dados.descricao());
        renda.setValor(dados.valor());
        renda.setTipo(dados.tipo());
        renda.setUsuario(usuario);

        Renda salva = rendaRepository.save(renda);
        return toResponse(salva);
    }

    public RendaResponseDTO toResponse(Renda renda) {
        return new RendaResponseDTO(
                renda.getId(),
                renda.getDescricao(),
                renda.getValor(),
                renda.getTipo(),
                renda.getUsuario().getId()
        );
    }
}
