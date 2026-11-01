package com.bradesco.service;

import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.MetaSugeridaDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class PatrimonioService {
  
    private static final BigDecimal MESES_RESERVA = BigDecimal.valueOf(6);
    private static final BigDecimal PERCENTUAL_INVESTIMENTO = new BigDecimal("0.20");
    private static final BigDecimal MESES_NO_ANO = BigDecimal.valueOf(12);

    private final TransacaoRepository transacaoRepository;
    private final RendaRepository rendaRepository;
    private final UsuarioRepository usuarioRepository;

    public PatrimonioService(TransacaoRepository transacaoRepository,
                             RendaRepository rendaRepository,
                             UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.rendaRepository = rendaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Feature 03: patrimônio acumulado mês a mês
    @Transactional(readOnly = true)
    public List<EvolucaoPatrimonialDTO> evolucaoPatrimonial(Long usuarioId) {

        validarUsuario(usuarioId);

        // Saldo de cada mês (receitas - despesas), em ordem crescente de mês
        Map<YearMonth, BigDecimal> saldoPorMes = transacaoRepository
                .findByUsuarioId(usuarioId).stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getDataTransacao()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, this::valorComSinal, BigDecimal::add)));

        List<EvolucaoPatrimonialDTO> evolucao = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;

        for (Map.Entry<YearMonth, BigDecimal> mes : saldoPorMes.entrySet()) {
            acumulado = acumulado.add(mes.getValue());
            evolucao.add(new EvolucaoPatrimonialDTO(mes.getKey().toString(), acumulado));
        }

        return evolucao;
    }

    // Feature 04: metas sugeridas a partir da renda
    @Transactional(readOnly = true)
    public List<MetaSugeridaDTO> metasSugeridas(Long usuarioId) {

        validarUsuario(usuarioId);

        BigDecimal rendaMensal = rendaRepository.findByUsuarioId(usuarioId).stream()
                .map(Renda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Sem renda não há base para sugerir metas
        if (rendaMensal.signum() <= 0) {
            return List.of();
        }

        BigDecimal reservaEmergencia = rendaMensal
                .multiply(MESES_RESERVA)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal investimentoAnual = rendaMensal
                .multiply(PERCENTUAL_INVESTIMENTO)
                .multiply(MESES_NO_ANO)
                .setScale(2, RoundingMode.HALF_UP);

        return List.of(
                new MetaSugeridaDTO("Reserva de emergência (6 meses)", reservaEmergencia),
                new MetaSugeridaDTO("Investimento anual", investimentoAnual));
    }

    private BigDecimal valorComSinal(Transacao transacao) {
        return transacao.getTipo() == TipoTransacao.RECEITA
                ? transacao.getValor()
                : transacao.getValor().negate();
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
