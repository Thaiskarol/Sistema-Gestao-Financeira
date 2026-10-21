package com.bradesco.repository;

import com.bradesco.entity.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    List<Orcamento> findByUsuarioId(Long usuarioId);

    Optional<Orcamento> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);
}
