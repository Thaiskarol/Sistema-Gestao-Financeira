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

import org.springframework.data.domain.PageRequest;
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

       Categoria categoria = buscarCategoria(dados.categoriaId(), dados.usuarioId());

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

    // Últimas N transações do usuário, da mais recente para a mais antiga
    public List<TransacaoResponseDTO> listarUltimas(Long usuarioId, int limite) {

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        // "Últimas 0 transações" (ou menos) é uma lista vazia
        if (limite < 1) {
            return List.of();
        }

        return transacaoRepository
                .findByUsuarioIdOrderByDataTransacaoDesc(usuarioId, PageRequest.of(0, limite))
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

        Categoria categoria = buscarCategoria(dados.categoriaId(), dados.usuarioId());

        transacao.setDescricao(dados.descricao());
        transacao.setRecorrencia(dados.recorrencia());
        transacao.setCategoria(categoria);

        Transacao atualizada = transacaoRepository.save(transacao);
        return toResponse(atualizada);
    }

    @Transactional
    public TransacaoResponseDTO classificar(Long transacaoId, Long categoriaId) {

        Transacao transacao = transacaoRepository.findById(transacaoId)
            .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        Categoria categoria = buscarCategoria(categoriaId, donoDa(transacao));

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

        Categoria categoria = buscarCategoria(item.categoriaId(), dados.usuarioId());

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

        return TransacaoResponseDTO.from(transacao);
    }

        private Long donoDa(Transacao transacao) {
        return transacao.getUsuario().getId();
    }

    @Transactional
    public TransacaoResponseDTO alterarCategoria(Long transacaoId, Long novaCategoriaId) {

        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

       Categoria novaCategoria = buscarCategoria(novaCategoriaId, donoDa(transacao));

        transacao.setCategoria(novaCategoria);

        return toResponse(transacaoRepository.save(transacao));
    }

        // Aceita categoria padrão ou do próprio usuário. Categoria de outro usuário -> 404.
    private Categoria buscarCategoria(Long categoriaId, Long usuarioId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (categoria.getUsuario() != null
                && !categoria.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Categoria não encontrada.");
        }
        return categoria;
    }
}

