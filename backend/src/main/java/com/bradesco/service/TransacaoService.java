package com.bradesco.service;

import com.bradesco.dto.request.ImportacaoRequestDTO;
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

import java.util.ArrayList;
import java.util.List;

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
        transacao.setEstabelecimento(dados.estabelecimento());
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

    public void excluir(Long id, Long usuarioId) {

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        if (!transacao.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Transação não encontrada para este usuário.");
        }

        transacaoRepository.delete(transacao);
    }

    public List<TransacaoResponseDTO> importar(ImportacaoRequestDTO dados) {

        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        List<Transacao> salvas = new ArrayList<>();

        for (TransacaoRequestDTO item : dados.transacoes()) {

            if (item.valor() == null || item.valor().signum() <= 0) {
                continue;
            }

            Categoria categoria = categoriaRepository.findById(item.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

            Transacao t = new Transacao();

            t.setDescricao(item.descricao());
            t.setEstabelecimento(item.estabelecimento());
            t.setValor(item.valor());
            t.setDataTransacao(item.dataTransacao());
            t.setRecorrencia(item.recorrencia());
            t.setTipo(item.tipo());
            t.setUsuario(usuario);
            t.setCategoria(categoria);

            salvas.add(transacaoRepository.save(t));
        }

        return salvas.stream()
                .map(this::toResponse)
                .toList();
    }

    private TransacaoResponseDTO toResponse(Transacao transacao) {

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getDataTransacao(),
                transacao.getRecorrencia(),
                transacao.getTipo(),
                transacao.getCategoria().getId(),
                transacao.getUsuario().getId()
        );
    }

    @Transactional
    public TransacaoResponseDTO alterarCategoria(Long transacaoId, Long novaCategoriaId) {

        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        Categoria novaCategoria = categoriaRepository.findById(novaCategoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        transacao.setCategoria(novaCategoria);

        return toResponse(transacaoRepository.save(transacao));
    }
}
