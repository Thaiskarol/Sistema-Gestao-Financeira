package com.bradesco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bradesco.dto.response.EvolucaoRelatorioDTO;
import com.bradesco.enums.TipoTransacao;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.entity.Transacao;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

class EvolucaoRelatorioServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private EvolucaoRelatorioService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new EvolucaoRelatorioService(
                transacaoRepository,
                usuarioRepository
        );
    }

    @Test
    void deveIdentificarAumentoDeGastos() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao despesaAnterior =
                criarTransacao(
                        BigDecimal.valueOf(100),
                        YearMonth.now().minusMonths(1)
                );

        Transacao despesaAtual =
                criarTransacao(
                        BigDecimal.valueOf(150),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(despesaAnterior, despesaAtual));

        EvolucaoRelatorioDTO resultado =
                service.evolucaoMensal(usuarioId);

        assertEquals(
                0,
                BigDecimal.valueOf(50)
                        .compareTo(resultado.variacaoPercentual())
        );

        assertEquals(
                "Gastos 50.00% maiores que o mês anterior.",
                resultado.descricao()
        );
    }

    @Test
    void deveIdentificarQuedaDeGastos() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao despesaAnterior =
                criarTransacao(
                        BigDecimal.valueOf(150),
                        YearMonth.now().minusMonths(1)
                );

        Transacao despesaAtual =
                criarTransacao(
                        BigDecimal.valueOf(100),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(despesaAnterior, despesaAtual));

        EvolucaoRelatorioDTO resultado =
                service.evolucaoMensal(usuarioId);

        System.out.println("Variação calculada: " + resultado.variacaoPercentual());
System.out.println("Descrição: " + resultado.descricao());

        assertEquals(
                0,
                BigDecimal.valueOf(-33.33)
                        .compareTo(resultado.variacaoPercentual())
        );

        assertEquals(
                "Gastos 33.33% menores que o mês anterior.",
                resultado.descricao()
        );
    }

    @Test
    void deveRetornarSemBaseDeComparacaoQuandoMesAnteriorForZero() {

        Long usuarioId = 1L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(true);

        Transacao despesaAtual =
                criarTransacao(
                        BigDecimal.valueOf(100),
                        YearMonth.now()
                );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(despesaAtual));

        EvolucaoRelatorioDTO resultado =
                service.evolucaoMensal(usuarioId);

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        resultado.variacaoPercentual()
                )
        );

        assertEquals(
                "Sem base de comparação do mês anterior.",
                resultado.descricao()
        );
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {

        Long usuarioId = 999L;

        when(usuarioRepository.existsById(usuarioId))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.evolucaoMensal(usuarioId)
        );
    }

    @Test
void deveIgnorarReceitasNoCalculoDaEvolucao() {

    Long usuarioId = 1L;

    when(usuarioRepository.existsById(usuarioId))
            .thenReturn(true);

    Transacao despesaAnterior =
            criarTransacao(
                    BigDecimal.valueOf(100),
                    YearMonth.now().minusMonths(1)
            );

    Transacao despesaAtual =
            criarTransacao(
                    BigDecimal.valueOf(150),
                    YearMonth.now()
            );

    Transacao receitaAtual =
            criarTransacao(
                    BigDecimal.valueOf(1000),
                    YearMonth.now()
            );

    receitaAtual.setTipo(TipoTransacao.RECEITA);

    when(transacaoRepository.findByUsuarioId(usuarioId))
            .thenReturn(List.of(
                    despesaAnterior,
                    despesaAtual,
                    receitaAtual
            ));

    EvolucaoRelatorioDTO resultado =
            service.evolucaoMensal(usuarioId);

    assertEquals(
            0,
            BigDecimal.valueOf(150)
                    .compareTo(resultado.totalMesAtual())
    );

    assertEquals(
            0,
            BigDecimal.valueOf(100)
                    .compareTo(resultado.totalMesAnterior())
    );

    assertEquals(
            0,
            BigDecimal.valueOf(50)
                    .compareTo(resultado.variacaoPercentual())
    );
}

    private Transacao criarTransacao(
            BigDecimal valor,
            YearMonth mes) {

        Transacao transacao = new Transacao();

        transacao.setValor(valor);

        transacao.setDataTransacao(
                LocalDate.of(
                        mes.getYear(),
                        mes.getMonth(),
                        10
                )
        );

        transacao.setTipo(TipoTransacao.DESPESA);

        return transacao;
    }
}