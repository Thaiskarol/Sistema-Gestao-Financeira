package com.bradesco.service;

import org.springframework.stereotype.Service;

import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bradesco.dto.response.ComparativoMensalDTO;
import com.bradesco.dto.response.ValorMensalDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.entity.Transacao;

@Service
public class EvolucaoService {

  public ComparativoMensalDTO compararMeses(
        Long usuarioId,
        String mesA,
        String mesB) {

    validarUsuario(usuarioId);

    List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);

    Map<YearMonth, BigDecimal> valoresPorMes = transacoes.stream()
            .filter(transacao -> transacao.getTipo() == TipoTransacao.DESPESA)
            .collect(Collectors.groupingBy(
                    transacao -> YearMonth.from(transacao.getDataTransacao()),
                    Collectors.reducing(
                            BigDecimal.ZERO,
                            Transacao::getValor,
                            BigDecimal::add
                    )
            ));

    BigDecimal valorA = valoresPorMes.getOrDefault(
            YearMonth.parse(mesA),
            BigDecimal.ZERO
    );

    BigDecimal valorB = valoresPorMes.getOrDefault(
            YearMonth.parse(mesB),
            BigDecimal.ZERO
    );

    BigDecimal variacao = valorB.subtract(valorA);

    return new ComparativoMensalDTO(
            mesA,
            valorA,
            mesB,
            valorB,
            variacao
    );
}

  public List<ValorMensalDTO> evolucaoMensalReceitas(Long usuarioId) {

    validarUsuario(usuarioId);

    List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);

    Map<YearMonth, BigDecimal> valoresPorMes = transacoes.stream()
            .filter(transacao -> transacao.getTipo() == TipoTransacao.RECEITA)
            .collect(Collectors.groupingBy(
                    transacao -> YearMonth.from(transacao.getDataTransacao()),
                    Collectors.reducing(
                            BigDecimal.ZERO,
                            Transacao::getValor,
                            BigDecimal::add
                    )
            ));

    return valoresPorMes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new ValorMensalDTO(
                    entry.getKey().toString(),
                    entry.getValue()
            ))
            .toList();
}

  public List<ValorMensalDTO> evolucaoMensalDespesas(Long usuarioId) {

    validarUsuario(usuarioId);

    List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);

    Map<YearMonth, BigDecimal> valoresPorMes = transacoes.stream()
            .filter(transacao -> transacao.getTipo() == TipoTransacao.DESPESA)
            .collect(Collectors.groupingBy(
                    transacao -> YearMonth.from(transacao.getDataTransacao()),
                    Collectors.reducing(
                            BigDecimal.ZERO,
                            Transacao::getValor,
                            BigDecimal::add
                    )
            ));

    return valoresPorMes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new ValorMensalDTO(
                    entry.getKey().toString(),
                    entry.getValue()
            ))
            .toList();
}

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public EvolucaoService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuário não encontrado.");
        }
    }
}