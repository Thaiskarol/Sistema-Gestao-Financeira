package com.bradesco.service;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AumentoGastosService {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);

  private static final BigDecimal LIMIAR_AUMENTO = BigDecimal.valueOf(30);

  private final TransacaoRepository transacaoRepository;
  private final UsuarioRepository usuarioRepository;

  public AumentoGastosService(TransacaoRepository transacaoRepository,
                  UsuarioRepository usuarioRepository) {
    this.transacaoRepository = transacaoRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional(readOnly = true)
  public Optional<InsightDTO> analisarAumento(Long usuarioId) {

      validarUsuario(usuarioId);

      Map<YearMonth, BigDecimal> despesasPorMes = transacaoRepository
            .findByUsuarioId(usuarioId).stream()
            .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
            .collect(Collectors.groupingBy(
                  t -> YearMonth.from(t.getDataTransacao()),
                  Collectors.reducing(BigDecimal.ZERO, Transacao::getValor, BigDecimal::add)));

      YearMonth mesAtual = YearMonth.now();
      BigDecimal atual = despesasPorMes.getOrDefault(mesAtual, BigDecimal.ZERO);
      BigDecimal anterior = despesasPorMes.getOrDefault(mesAtual.minusMonths(1), BigDecimal.ZERO);

      if (anterior.signum() == 0) {
          return Optional.empty();
      }

      BigDecimal percentual = atual.subtract(anterior)
              .multiply(CEM)
              .divide(anterior, 2, RoundingMode.HALF_UP);

      if (percentual.compareTo(LIMIAR_AUMENTO) <= 0) {
          return Optional.empty();
      }

      return Optional.of(new InsightDTO(
              "AUMENTO_GASTOS",
              "Geral",
              percentual,
              "Seus gastos aumentaram " + percentual.toPlainString()
                      + "% em relação ao mês anterior."));
  }

  private void validarUsuario(Long usuarioId) {
      if (!usuarioRepository.existsById(usuarioId)) {
          throw new ResourceNotFoundException("Usuário não encontrado.");
      }
  }
}
