package com.bradesco.service;

import com.bradesco.dto.response.CriterioScoreDTO;
import com.bradesco.dto.response.EvolucaoPatrimonialDTO;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Orcamento;
import com.bradesco.entity.Transacao;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static com.bradesco.service.ScoreSaudeFinanceiraService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreSaudeFinanceiraServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private PatrimonioService patrimonioService;

    @InjectMocks
    private ScoreSaudeFinanceiraService scoreService;

    private static final YearMonth M0 = YearMonth.now();
    private static final YearMonth M1 = M0.minusMonths(1);
    private static final YearMonth M2 = M0.minusMonths(2);
    private static final YearMonth M6 = M0.minusMonths(6);

    private final Categoria outros = categoria(1L, "Outros");
    private final Categoria moradia = categoria(2L, "Moradia");
    private final Categoria alimentacao = categoria(3L, "Alimentação");
    private final Categoria lazer = categoria(4L, "Lazer");
    private final Categoria transporte = categoria(5L, "Transporte");
    private final Categoria investimentos = categoria(6L, "Investimentos");

    private static Categoria categoria(Long id, String nome) {
        Categoria c = new Categoria();
        ReflectionTestUtils.setField(c, "id", id);
        c.setNome(nome);
        return c;
    }

    private Transacao transacao(TipoTransacao tipo, Categoria categoria, String valor, YearMonth mes) {
        Transacao t = new Transacao();
        t.setTipo(tipo);
        t.setCategoria(categoria);
        t.setValor(new BigDecimal(valor));
        t.setDataTransacao(mes.atDay(10));
        return t;
    }

    private Transacao receita(String valor, YearMonth mes) {
        return transacao(TipoTransacao.RECEITA, outros, valor, mes);
    }

    private Transacao despesa(Categoria categoria, String valor, YearMonth mes) {
        return transacao(TipoTransacao.DESPESA, categoria, valor, mes);
    }

    private Orcamento orcamento(Categoria categoria, String limite) {
        Orcamento o = new Orcamento();
        o.setCategoria(categoria);
        o.setLimite(new BigDecimal(limite));
        return o;
    }

    private List<EvolucaoPatrimonialDTO> evolucao(String anterior, String atual) {
        return List.of(
                new EvolucaoPatrimonialDTO(M1.toString(), new BigDecimal(anterior)),
                new EvolucaoPatrimonialDTO(M0.toString(), new BigDecimal(atual)));
    }

    private void usuarioComTransacoes(Transacao... transacoes) {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(transacaoRepository.findByUsuarioId(1L)).thenReturn(List.of(transacoes));
    }

    private CriterioScoreDTO criterio(ScoreSaudeFinanceiraDTO score, String nome) {
        return score.criterios().stream()
                .filter(c -> c.nome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void deveCalcularScoreDoExemploDeReferencia() {
        usuarioComTransacoes(
                // Mês atual: receitas 5000.00, despesas 4250.00 (economia de 15%)
                receita("5000.00", M0),
                despesa(moradia, "2000.00", M0),
                despesa(alimentacao, "900.00", M0),
                despesa(lazer, "400.00", M0),
                despesa(investimentos, "500.00", M0),
                despesa(transporte, "450.00", M0),
                // Mês anterior: despesas 4000.00 (alimentação estoura o limite de 1000)
                despesa(moradia, "2000.00", M1),
                despesa(alimentacao, "1200.00", M1),
                despesa(lazer, "300.00", M1),
                despesa(investimentos, "500.00", M1),
                // Dois meses atrás
                despesa(alimentacao, "800.00", M2),
                despesa(lazer, "200.00", M2),
                despesa(investimentos, "500.00", M2));

        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                orcamento(alimentacao, "1000.00"),
                orcamento(lazer, "500.00")));

        // Patrimônio igual nos dois últimos meses
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(evolucao("1000.00", "1000.00"));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        assertEquals(15, criterio(resultado, CRITERIO_ECONOMIA).pontos());        // 15%
        assertEquals(17, criterio(resultado, CRITERIO_ORCAMENTO).pontos());       // 20 x (1 - 1/6)
        assertEquals(10, criterio(resultado, CRITERIO_PATRIMONIO).pontos());      // igual
        assertEquals(10, criterio(resultado, CRITERIO_INVESTIMENTOS).pontos());   // 3 de 6 meses
        assertEquals(16, criterio(resultado, CRITERIO_GASTOS).pontos());          // +6,25% de gastos

        assertEquals(68, resultado.score());
        assertTrue(resultado.dadosSuficientes());
        assertEquals("Score financeiro calculado com sucesso.", resultado.mensagem());
        assertEquals(5, resultado.criterios().size());
        assertTrue(resultado.criterios().stream().allMatch(CriterioScoreDTO::dadosSuficientes));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> scoreService.consultarScore(99L));

        verifyNoInteractions(transacaoRepository, orcamentoRepository, patrimonioService);
    }

    @Test
    void deveRetornarScoreZeroQuandoNaoHaNenhumDado() {
        usuarioComTransacoes();

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        assertEquals(0, resultado.score());
        assertFalse(resultado.dadosSuficientes());
        assertEquals("Dados insuficientes para calcular o score financeiro.", resultado.mensagem());
        assertEquals(5, resultado.criterios().size());
        assertTrue(resultado.criterios().stream().noneMatch(CriterioScoreDTO::dadosSuficientes));
        assertTrue(resultado.criterios().stream().allMatch(c -> c.pontos() == 0));
    }

    @Test
    void criterioSemDadoValeZeroPontosEFicaMarcadoComoSemDados() {
        // Só há uma receita no mês atual: economia de 100% (20 pontos)
        usuarioComTransacoes(receita("1000.00", M0));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        CriterioScoreDTO economia = criterio(resultado, CRITERIO_ECONOMIA);
        assertEquals(20, economia.pontos());
        assertTrue(economia.dadosSuficientes());

        CriterioScoreDTO orcamento = criterio(resultado, CRITERIO_ORCAMENTO);   // sem orçamento
        assertEquals(0, orcamento.pontos());
        assertFalse(orcamento.dadosSuficientes());

        CriterioScoreDTO gastos = criterio(resultado, CRITERIO_GASTOS);          // sem despesas no mês anterior
        assertEquals(0, gastos.pontos());
        assertFalse(gastos.dadosSuficientes());

        assertEquals(20, resultado.score());
        assertTrue(resultado.dadosSuficientes());
    }

    @Test
    void deveDar17PontosQuandoUmDosSeisMesesOrcamentoEstoura() {
        usuarioComTransacoes(despesa(alimentacao, "1200.00", M1));   // estoura só 1 de 6

        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(
                orcamento(alimentacao, "1000.00"),
                orcamento(lazer, "500.00")));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        CriterioScoreDTO orcamento = criterio(resultado, CRITERIO_ORCAMENTO);
        assertEquals(17, orcamento.pontos());   // 20 x (1 - 1/6) = 16,67
        assertTrue(orcamento.dadosSuficientes());
    }

    @Test
    void gastoIgualAoLimiteNaoContaComoEstouro() {
        usuarioComTransacoes(despesa(alimentacao, "1000.00", M0));

        when(orcamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(orcamento(alimentacao, "1000.00")));

        assertEquals(20, criterio(scoreService.consultarScore(1L), CRITERIO_ORCAMENTO).pontos());
    }

    @Test
    void deveDar20PontosQuandoPatrimonioCresceu() {
        usuarioComTransacoes();
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(evolucao("1000.00", "1500.00"));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        assertEquals(20, criterio(resultado, CRITERIO_PATRIMONIO).pontos());
        assertEquals(20, resultado.score());
    }

    @Test
    void deveDar10PontosQuandoPatrimonioFicouIgual() {
        usuarioComTransacoes();
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(evolucao("1000.00", "1000.00"));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        assertEquals(10, criterio(resultado, CRITERIO_PATRIMONIO).pontos());
        assertEquals(10, resultado.score());
    }

    @Test
    void deveDarZeroPontosQuandoPatrimonioCaiu() {
        usuarioComTransacoes();
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(evolucao("1000.00", "800.00"));

        ScoreSaudeFinanceiraDTO resultado = scoreService.consultarScore(1L);

        CriterioScoreDTO patrimonio = criterio(resultado, CRITERIO_PATRIMONIO);
        assertEquals(0, patrimonio.pontos());
        assertTrue(patrimonio.dadosSuficientes());   // tem dado, só que a nota é zero
        assertTrue(resultado.dadosSuficientes());
    }

    @Test
    void patrimonioComMenosDeDoisMesesNaoTemDados() {
        usuarioComTransacoes();
        when(patrimonioService.evolucaoPatrimonial(1L)).thenReturn(List.of(
                new EvolucaoPatrimonialDTO(M0.toString(), new BigDecimal("1000.00"))));

        CriterioScoreDTO patrimonio = criterio(scoreService.consultarScore(1L), CRITERIO_PATRIMONIO);

        assertEquals(0, patrimonio.pontos());
        assertFalse(patrimonio.dadosSuficientes());
    }

    @Test
    void economiaNegativaDaZeroPontos() {
        usuarioComTransacoes(
                receita("1000.00", M0),
                despesa(moradia, "1200.00", M0));   // economia de -20%

        CriterioScoreDTO economia = criterio(scoreService.consultarScore(1L), CRITERIO_ECONOMIA);

        assertEquals(0, economia.pontos());
        assertTrue(economia.dadosSuficientes());
    }

    @Test
    void gastosQueDiminuemDao20Pontos() {
        usuarioComTransacoes(
                despesa(moradia, "1000.00", M1),
                despesa(moradia, "800.00", M0));   // -20%

        assertEquals(20, criterio(scoreService.consultarScore(1L), CRITERIO_GASTOS).pontos());
    }

    @Test
    void aumentoDe30PorCentoNosGastosDaZeroPontos() {
        usuarioComTransacoes(
                despesa(moradia, "1000.00", M1),
                despesa(moradia, "1300.00", M0));   // +30,00%

        assertEquals(0, criterio(scoreService.consultarScore(1L), CRITERIO_GASTOS).pontos());
    }

    @Test
    void investimentosIgnoramMaiusculasEMesesForaDaJanelaDeSeis() {
        Categoria investimentosMaiusculo = categoria(7L, "INVESTIMENTOS");

        usuarioComTransacoes(
                despesa(investimentosMaiusculo, "300.00", M0),   // conta (ignora maiúsculas)
                despesa(investimentos, "300.00", M6));           // fora dos últimos 6 meses

        CriterioScoreDTO constancia = criterio(scoreService.consultarScore(1L), CRITERIO_INVESTIMENTOS);

        assertEquals(3, constancia.pontos());   // 20 x 1 / 6 = 3,33
        assertTrue(constancia.dadosSuficientes());
    }

    @Test
    void investimentosSemNenhumaTransacaoNosUltimosSeisMesesNaoTemDados() {
        usuarioComTransacoes(despesa(investimentos, "300.00", M6));

        CriterioScoreDTO constancia = criterio(scoreService.consultarScore(1L), CRITERIO_INVESTIMENTOS);

        assertEquals(0, constancia.pontos());
        assertFalse(constancia.dadosSuficientes());
    }
}
