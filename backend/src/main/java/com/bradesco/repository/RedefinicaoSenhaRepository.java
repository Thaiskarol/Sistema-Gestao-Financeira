package com.bradesco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bradesco.entity.RedefinicaoSenha;

public interface RedefinicaoSenhaRepository extends JpaRepository<RedefinicaoSenha, Long> {

    Optional<RedefinicaoSenha> findByTokenHash(String tokenHash);

    List<RedefinicaoSenha> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}
