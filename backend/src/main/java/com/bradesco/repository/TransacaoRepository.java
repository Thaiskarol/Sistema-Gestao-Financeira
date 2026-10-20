package com.bradesco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);
}