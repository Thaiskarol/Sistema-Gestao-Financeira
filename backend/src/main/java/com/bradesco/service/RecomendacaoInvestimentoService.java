package com.bradesco.service;

import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import com.bradesco.entity.Renda;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.RendaRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecomendacaoInvestimentoService {

    // Percentual mínimo guardado como 20 (para exibir "20%"),
    // alinhado com a regra 50/30/20 do Epic 07 (os 20% são de investimentos/reserva).
    private static final BigDecimal PERCENTUAL_MINIMO = new BigDecimal("20");
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final int MESES_RESERVA = 6;

    private final RendaRepository rendaRepository;
    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecomendacaoInvestimentoService(RendaRepository rendaRepository,
                                           TransacaoRepository transacaoRepository,
                                           UsuarioRepository usuarioRepository) {
        this.rendaRepository = rendaRepository;
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Feature 01: percentual mínimo de investimento
    @Transactional(readOnly = true)
    public RecomendacaoInvestimentoDTO recomendarPercentualMinimo(Long usuarioId) {
        validarUsuario(usuarioId);

        BigDecimal rendaTotal = rendaRepository.findByUsuarioId(usuarioId).stream()
            .map(Renda::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorRecomendado = rendaTotal
            .multiply(PERCENTUAL_MINIMO)
            .divide(CEM, 2, RoundingMode.HALF_UP);

        return new RecomendacaoInvestimentoDTO(rendaTotal, PERCENTUAL_MINIMO, valorRecomendado);
    }

    // Feature 02: valor ideal de reserva de emergência (6 meses de despesas)
    @Transactional(readOnly = true)
    public ReservaEmergenciaDTO calcularReservaEmergencia(Long usuarioId) {
        validarUsuario(usuarioId);

        // Total de despesas de cada mês que teve despesa
        Map<YearMonth, BigDecimal> despesasPorMes = transacaoRepository
            .findByUsuarioIdAndTipo(usuarioId, TipoTransacao.DESPESA).stream()
            .collect(Collectors.groupingBy(
                t -> YearMonth.from(t.getDataTransacao()),
                Collectors.reducing(BigDecimal.ZERO, Transacao::getValor, BigDecimal::add)));

        // Sem despesas não há base de cálculo: valor zero, mantendo os 6 meses
        if (despesasPorMes.isEmpty()) {
            BigDecimal zero = BigDecimal.ZERO.setScale(2);
            return new ReservaEmergenciaDTO(zero, MESES_RESERVA, zero);
        }

        BigDecimal totalDespesas = despesasPorMes.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesaMediaMensal = totalDespesas
            .divide(BigDecimal.valueOf(despesasPorMes.size()), 2, RoundingMode.HALF_UP);

        BigDecimal valorIdealReserva = despesaMediaMensal
            .multiply(BigDecimal.valueOf(MESES_RESERVA))
            .setScale(2, RoundingMode.HALF_UP);

        return new ReservaEmergenciaDTO(despesaMediaMensal, MESES_RESERVA, valorIdealReserva);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
