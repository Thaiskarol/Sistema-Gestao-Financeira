package com.bradesco.service;

import com.bradesco.dto.request.MetaRequestDTO;
import com.bradesco.dto.request.MetaValorAcumuladoDTO;
import com.bradesco.dto.response.MetaResponseDTO;
import com.bradesco.entity.Meta;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.MetaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    public MetaService(MetaRepository metaRepository, UsuarioRepository usuarioRepository) {
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public MetaResponseDTO criar(MetaRequestDTO dados) {
        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Meta meta = new Meta();
        meta.setNome(dados.nome());
        meta.setValorObjetivo(dados.valorObjetivo());
        meta.setValorAcumulado(dados.valorAcumulado());
        meta.setDataDesejada(dados.dataDesejada());
        meta.setDataCriacao(LocalDate.now());
        meta.setUsuario(usuario);

        Meta salva = metaRepository.save(meta);
        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<MetaResponseDTO> listarPorUsuario(Long usuarioId) {
        validarUsuario(usuarioId);

        return metaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MetaResponseDTO buscar(Long metaId, Long usuarioId) {
        validarUsuario(usuarioId);
        return toResponse(buscarMeta(metaId, usuarioId));
    }

    @Transactional
    public MetaResponseDTO atualizarValorAcumulado(Long metaId, MetaValorAcumuladoDTO dados) {
        validarUsuario(dados.usuarioId());

        Meta meta = buscarMeta(metaId, dados.usuarioId());
        meta.setValorAcumulado(dados.valorAcumulado());

        Meta salva = metaRepository.save(meta);
        return toResponse(salva);
    }

    public MetaResponseDTO toResponse(Meta meta) {
        return new MetaResponseDTO(
                meta.getId(),
                meta.getNome(),
                meta.getValorObjetivo(),
                meta.getValorAcumulado(),
                meta.getDataDesejada(),
                meta.getDataCriacao(),
                meta.getUsuario().getId()
        );
    }

    private Meta buscarMeta(Long metaId, Long usuarioId) {
        return metaRepository.findByIdAndUsuarioId(metaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada."));
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
