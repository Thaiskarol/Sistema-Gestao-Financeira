package com.bradesco.repository;

import com.bradesco.entity.Renda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RendaRepository extends JpaRepository<Renda, Long> {

    List<Renda> findByUsuarioId(Long usuarioId);
}
