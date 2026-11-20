package com.bradesco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bradesco.entity.Investimento;

public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {

    List<Investimento> findByUsuarioIdOrderByDataRegistroDescIdDesc(Long usuarioId);

    Optional<Investimento> findByIdAndUsuarioId(Long id, Long usuarioId);
}
