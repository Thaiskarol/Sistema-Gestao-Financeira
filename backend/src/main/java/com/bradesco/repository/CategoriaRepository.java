package com.bradesco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bradesco.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Só as padrão (usuario_id nulo)
    List<Categoria> findByUsuarioIsNullOrderByIdAsc();

    // Padrão + as do usuário
    @Query("SELECT c FROM Categoria c LEFT JOIN c.usuario u "
         + "WHERE u IS NULL OR u.id = :usuarioId ORDER BY c.id")
    List<Categoria> findDisponiveis(@Param("usuarioId") Long usuarioId);

    boolean existsByUsuarioIsNullAndNomeIgnoreCase(String nome);

    boolean existsByUsuarioIdAndNomeIgnoreCase(Long usuarioId, String nome);

    boolean existsByUsuarioIdAndNomeIgnoreCaseAndIdNot(Long usuarioId, String nome, Long id);
}