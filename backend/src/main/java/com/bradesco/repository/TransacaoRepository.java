package com.bradesco.repository;

import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);

}
