package com.bradesco.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.request.OrcamentoRequestDTO;
import com.bradesco.dto.response.OrcamentoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public OrcamentoService(OrcamentoRepository orcamentoRepository,
                            UsuarioRepository usuarioRepository,
                            CategoriaRepository categoriaRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public OrcamentoResponseDTO criar(OrcamentoRequestDTO dados) {

        Usuario usuario = validarUsuario(dados.usuarioId());

        Categoria categoria = categoriaRepository.findById(dados.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Orcamento orcamento = new Orcamento();
        orcamento.setLimite(dados.limite());
        orcamento.setCategoria(categoria);
        orcamento.setUsuario(usuario);

        Orcamento salvo = orcamentoRepository.save(orcamento);

        return toResponse(salvo);
    }

    private Usuario validarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private OrcamentoResponseDTO toResponse(Orcamento orcamento) {
        return new OrcamentoResponseDTO(
                orcamento.getId(),
                orcamento.getLimite(),
                orcamento.getCategoria().getId(),
                orcamento.getCategoria().getNome(),
                orcamento.getUsuario().getId()
        );
    }
}
