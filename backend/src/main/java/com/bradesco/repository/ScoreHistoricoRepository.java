package com.bradesco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bradesco.entity.ScoreHistorico;

public interface ScoreHistoricoRepository extends JpaRepository<ScoreHistorico, Long> {

    Optional<ScoreHistorico> findByUsuarioIdAndMes(Long usuarioId, String mes);

    List<ScoreHistorico> findTop12ByUsuarioIdOrderByMesDesc(Long usuarioId);
}