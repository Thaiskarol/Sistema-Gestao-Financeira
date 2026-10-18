package com.bradesco.service;

import com.bradesco.dto.request.TransacaoRequestDTO;
import com.bradesco.dto.request.TransacaoUpdateDTO;
import com.bradesco.dto.response.TransacaoResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Transacao;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public TransacaoResponseDTO editar(Long id, TransacaoUpdateDTO dados) {

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        if (!transacao.getUsuario().getId().equals(dados.usuarioId())) {
            throw new ResourceNotFoundException("Transação não encontrada para este usuário.");
        }

        Categoria categoria = categoriaRepository.findById(dados.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        transacao.setDescricao(dados.descricao());
        transacao.setRecorrencia(dados.recorrencia());
        transacao.setCategoria(categoria);

        Transacao atualizada = transacaoRepository.save(transacao);
        return toResponse(atualizada);
    }

    private TransacaoResponseDTO toResponse(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getRecorrencia(),
                transacao.getTipo(),
                transacao.getUsuario().getId(),
                transacao.getCategoria().getId());
    }
}