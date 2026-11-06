package com.bradesco.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.response.ConsumoOrcamentoDTO;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.TransacaoRepository;

@Service
public class ConsumoOrcamentoService {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);

  private final OrcamentoRepository orcamentoRepository;
  private final TransacaoRepository transacaoRepository;

  public ConsumoOrcamentoService(OrcamentoRepository orcamentoRepository,
          TransacaoRepository transacaoRepository) {
  this.orcamentoRepository = orcamentoRepository;
  this.transacaoRepository = transacaoRepository;
  }

  @Transactional(readOnly = true)
  public ConsumoOrcamentoDTO calcularConsumo(Long orcamentoId) {

    Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
          .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado."));

    Long usuarioId = orcamento.getUsuario().getId();
    Long categoriaId = orcamento.getCategoria().getId();
    YearMonth mesAtual = YearMonth.now();   // CA06 / RN004: só o mês corrente

    BigDecimal valorConsumido = transacaoRepository
          .findByUsuarioIdAndTipo(usuarioId, TipoTransacao.DESPESA).stream()
          .filter(t -> t.getCategoria() != null
                  && t.getCategoria().getId().equals(categoriaId))
          .filter(t -> YearMonth.from(t.getDataTransacao()).equals(mesAtual))
          .map(Transacao::getValor)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .setScale(2, RoundingMode.HALF_UP);

        BigDecimal limite = orcamento.getLimite();

        BigDecimal percentualUtilizado = limite.signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : valorConsumido.multiply(CEM).divide(limite, 2, RoundingMode.HALF_UP);

        BigDecimal saldoRestante = limite.subtract(valorConsumido);

        return new ConsumoOrcamentoDTO(
                orcamento.getId(),
                categoriaId,
                orcamento.getCategoria().getNome(),
                limite,
                valorConsumido,
                percentualUtilizado,
                saldoRestante
        );
  }
}
