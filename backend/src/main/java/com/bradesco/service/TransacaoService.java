package com.bradesco.service;

import com.bradesco.dto.request.ImportacaoRequestDTO;
import com.bradesco.dto.request.TransacaoRequestDTO;
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

    public void excluir(Long id, Long usuarioId) {

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

                if (!transacao.getUsuario().getId().equals(usuarioId)) {
                    throw new ResourceNotFoundException("Transação não encontrada para este usuário.");
                }

                transacaoRepository.delete(transacao);
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
        t.setValor(item.valor());
        t.setDataTransacao(item.dataTransacao());
        t.setRecorrencia(item.recorrencia());
        t.setTipo(item.tipo());
        t.setUsuario(usuario);
        t.setCategoria(categoria);
        salvas.add(transacaoRepository.save(t));
    }

    return salvas.stream().map(this::toResponse).toList();
}
}