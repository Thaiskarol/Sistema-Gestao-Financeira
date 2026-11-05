package com.bradesco.service;

import com.bradesco.dto.response.CriterioScoreDTO;
import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Service
public class ScoreSaudeFinanceiraService {

    static final String CRITERIO_ECONOMIA = "Percentual de economia";
    static final String CRITERIO_ORCAMENTO = "Estouro de orçamento";
    static final String CRITERIO_PATRIMONIO = "Evolução patrimonial";
    static final String CRITERIO_INVESTIMENTOS = "Constância de investimentos";
    static final String CRITERIO_GASTOS = "Controle de gastos";

    private static final String MSG_SUCESSO = "Score financeiro calculado com sucesso.";
    private static final String MSG_INSUFICIENTE = "Dados insuficientes para calcular o score financeiro.";

    private static final String CATEGORIA_INVESTIMENTOS = "Investimentos";

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal PONTOS_MAXIMOS = BigDecimal.valueOf(20);

    // Mesmo valor do LIMIAR_AUMENTO do AumentoGastosService (lá ele é private).
    // Se um dia mudar lá, mude aqui também.
    private static final BigDecimal LIMIAR_AUMENTO = BigDecimal.valueOf(30);

    private static final int MESES_ORCAMENTO = 3;
    private static final int MESES_INVESTIMENTO = 6;

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PatrimonioService patrimonioService;

    public ScoreSaudeFinanceiraService(UsuarioRepository usuarioRepository,
                                       TransacaoRepository transacaoRepository,
                                       OrcamentoRepository orcamentoRepository,
                                       PatrimonioService patrimonioService) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.patrimonioService = patrimonioService;
    }

    @Transactional(readOnly = true)
    public ScoreSaudeFinanceiraDTO consultarScore(Long usuarioId) {

        validarUsuario(usuarioId);

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuarioId);
        List<Orcamento> orcamentos = orcamentoRepository.findByUsuarioId(usuarioId);
        YearMonth mesAtual = YearMonth.now();

        List<CriterioScoreDTO> criterios = List.of(
                percentualEconomia(transacoes, mesAtual),
                estouroOrcamento(orcamentos, transacoes, mesAtual),
                evolucaoPatrimonial(usuarioId),
                constanciaInvestimentos(transacoes, mesAtual),
                controleGastos(transacoes, mesAtual));

        boolean algumCriterioComDado = criterios.stream()
                .anyMatch(CriterioScoreDTO::dadosSuficientes);

        if (!algumCriterioComDado) {
            return new ScoreSaudeFinanceiraDTO(0, false, MSG_INSUFICIENTE, criterios);
        }

        // Cada critério vale no máximo 20, então o máximo é 100 por construção
        int score = criterios.stream().mapToInt(CriterioScoreDTO::pontos).sum();

        return new ScoreSaudeFinanceiraDTO(score, true, MSG_SUCESSO, criterios);
    }

    // 1) (receitas - despesas) * 100 / receitas, do mês atual; limitado entre 0 e 20
    private CriterioScoreDTO percentualEconomia(List<Transacao> transacoes, YearMonth mesAtual) {
        BigDecimal receitas = somar(transacoes, TipoTransacao.RECEITA, mesAtual);
        BigDecimal despesas = somar(transacoes, TipoTransacao.DESPESA, mesAtual);

        if (receitas.signum() == 0) {
            return semDados(CRITERIO_ECONOMIA);
        }

        BigDecimal percentual = receitas.subtract(despesas)
                .multiply(CEM)
                .divide(receitas, 2, RoundingMode.HALF_UP);

        BigDecimal pontos = percentual.max(BigDecimal.ZERO).min(PONTOS_MAXIMOS);

        return new CriterioScoreDTO(CRITERIO_ECONOMIA, arredondar(pontos), true);
    }

    // 2) Para cada orçamento, nos últimos 3 meses: gasto da categoria > limite = estourado
    private CriterioScoreDTO estouroOrcamento(List<Orcamento> orcamentos,
                                              List<Transacao> transacoes,
                                              YearMonth mesAtual) {
        if (orcamentos.isEmpty()) {
            return semDados(CRITERIO_ORCAMENTO);
        }

        int totalVerificacoes = orcamentos.size() * MESES_ORCAMENTO;
        int estourados = 0;

        for (Orcamento orcamento : orcamentos) {
            for (int i = 0; i < MESES_ORCAMENTO; i++) {
                YearMonth mes = mesAtual.minusMonths(i);
                BigDecimal gasto = gastoDaCategoriaNoMes(
                        transacoes, orcamento.getCategoria().getId(), mes);

                if (gasto.compareTo(orcamento.getLimite()) > 0) {
                    estourados++;
                }
            }
        }

        // 20 x (1 - estourados / total) = 20 x (total - estourados) / total
        BigDecimal pontos = PONTOS_MAXIMOS
                .multiply(BigDecimal.valueOf(totalVerificacoes - estourados))
                .divide(BigDecimal.valueOf(totalVerificacoes), 0, RoundingMode.HALF_UP);

        return new CriterioScoreDTO(CRITERIO_ORCAMENTO, pontos.intValue(), true);
    }

    // 3) Compara os dois últimos itens da evolução: cresceu 20, igual 10, caiu 0
    private CriterioScoreDTO evolucaoPatrimonial(Long usuarioId) {
        List<EvolucaoPatrimonialDTO> evolucao = patrimonioService.evolucaoPatrimonial(usuarioId);

        if (evolucao.size() < 2) {
            return semDados(CRITERIO_PATRIMONIO);
        }

        BigDecimal anterior = evolucao.get(evolucao.size() - 2).patrimonioAcumulado();
        BigDecimal atual = evolucao.get(evolucao.size() - 1).patrimonioAcumulado();

        int comparacao = atual.compareTo(anterior);
        int pontos = comparacao > 0 ? 20 : (comparacao == 0 ? 10 : 0);

        return new CriterioScoreDTO(CRITERIO_PATRIMONIO, pontos, true);
    }

    // 4) Meses distintos, nos últimos 6, com despesa em "Investimentos": 20 x meses / 6
    private CriterioScoreDTO constanciaInvestimentos(List<Transacao> transacoes, YearMonth mesAtual) {
        YearMonth inicio = mesAtual.minusMonths(MESES_INVESTIMENTO - 1);

        List<Transacao> ultimosMeses = transacoes.stream()
                .filter(t -> {
                    YearMonth mes = YearMonth.from(t.getDataTransacao());
                    return !mes.isBefore(inicio) && !mes.isAfter(mesAtual);
                })
                .toList();

        // Sem nenhuma transação no período, não há base para avaliar
        if (ultimosMeses.isEmpty()) {
            return semDados(CRITERIO_INVESTIMENTOS);
        }

        long mesesComInvestimento = ultimosMeses.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .filter(t -> t.getCategoria() != null
                        && CATEGORIA_INVESTIMENTOS.equalsIgnoreCase(t.getCategoria().getNome()))
                .map(t -> YearMonth.from(t.getDataTransacao()))
                .distinct()
                .count();

        BigDecimal pontos = PONTOS_MAXIMOS
                .multiply(BigDecimal.valueOf(mesesComInvestimento))
                .divide(BigDecimal.valueOf(MESES_INVESTIMENTO), 0, RoundingMode.HALF_UP);

        return new CriterioScoreDTO(CRITERIO_INVESTIMENTOS, pontos.intValue(), true);
    }

    // 5) Variação das despesas (mês atual x anterior): <= 0 dá 20; >= 30% dá 0; no meio, proporcional
    private CriterioScoreDTO controleGastos(List<Transacao> transacoes, YearMonth mesAtual) {
        BigDecimal despesasAtual = somar(transacoes, TipoTransacao.DESPESA, mesAtual);
        BigDecimal despesasAnterior = somar(transacoes, TipoTransacao.DESPESA, mesAtual.minusMonths(1));

        if (despesasAnterior.signum() == 0) {
            return semDados(CRITERIO_GASTOS);
        }

        BigDecimal variacao = despesasAtual.subtract(despesasAnterior)
                .multiply(CEM)
                .divide(despesasAnterior, 2, RoundingMode.HALF_UP);

        int pontos;
        if (variacao.signum() <= 0) {
            pontos = 20;
        } else if (variacao.compareTo(LIMIAR_AUMENTO) >= 0) {
            pontos = 0;
        } else {
            // 20 x (1 - variacao / 30) = 20 x (30 - variacao) / 30
            pontos = PONTOS_MAXIMOS
                    .multiply(LIMIAR_AUMENTO.subtract(variacao))
                    .divide(LIMIAR_AUMENTO, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        return new CriterioScoreDTO(CRITERIO_GASTOS, pontos, true);
    }

    private BigDecimal somar(List<Transacao> transacoes, TipoTransacao tipo, YearMonth mes) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .filter(t -> YearMonth.from(t.getDataTransacao()).equals(mes))
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal gastoDaCategoriaNoMes(List<Transacao> transacoes, Long categoriaId, YearMonth mes) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .filter(t -> t.getCategoria() != null && categoriaId.equals(t.getCategoria().getId()))
                .filter(t -> YearMonth.from(t.getDataTransacao()).equals(mes))
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CriterioScoreDTO semDados(String nome) {
        return new CriterioScoreDTO(nome, 0, false);
    }

    private int arredondar(BigDecimal pontos) {
        return pontos.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
