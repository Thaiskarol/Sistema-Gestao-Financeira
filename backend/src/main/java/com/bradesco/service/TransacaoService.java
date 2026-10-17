package com.bradesco.service;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository) {

        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Transacao cadastrar(TransacaoRequestDTO dados) {

        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Categoria categoria = categoriaRepository.findById(dados.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        Transacao transacao = new Transacao();

        transacao.setDescricao(dados.descricao());
        transacao.setValor(dados.valor());
        transacao.setDataTransacao(dados.dataTransacao());
        transacao.setRecorrencia(dados.recorrencia());
        transacao.setTipo(dados.tipo());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        return transacaoRepository.save(transacao);
    }

    public List<TransacaoResponseDTO> listarPorUsuario(Long usuarioId) {

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return transacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransacaoResponseDTO toResponse(Transacao t) {
        return new TransacaoResponseDTO(
                t.getId(),
                t.getDescricao(),
                t.getValor(),
                t.getDataTransacao(),
                t.getRecorrencia(),
                t.getTipo(),
                t.getCategoria().getId(),
                t.getUsuario().getId()
        );
    }
}
