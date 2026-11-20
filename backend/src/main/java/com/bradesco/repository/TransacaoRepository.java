package com.bradesco.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);

    List<Transacao> findByUsuarioIdAndDataTransacaoBetween(
        Long usuarioId,
        LocalDate dataInicio,
        LocalDate dataFim
);
    // Últimas transações do usuário (o limite vem no Pageable)
    List<Transacao> findByUsuarioIdOrderByDataTransacaoDesc(Long usuarioId, Pageable pageable);

     boolean existsByCategoriaId(Long categoriaId);

}
