package com.bradesco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bradesco.dto.request.InvestimentoRequestDTO;
import com.bradesco.dto.response.CarteiraInvestimentoDTO;
import com.bradesco.dto.response.InvestimentoResponseDTO;
import com.bradesco.entity.Investimento;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoInvestimento;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.InvestimentoRepository;
import com.bradesco.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class InvestimentoServiceTest {

    @Mock
    private InvestimentoRepository investimentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private InvestimentoService investimentoService;

    // ---------- helpers ----------

    private Investimento investimentoDe(Long id, String nome, TipoInvestimento tipo, String valor) {
        Investimento investimento = new Investimento();
        ReflectionTestUtils.setField(investimento, "id", id);
        investimento.setNome(nome);
        investimento.setTipo(tipo);
        investimento.setValor(new BigDecimal(valor));
        investimento.setDataRegistro(LocalDate.now());
        return investimento;
    }

    // ---------- registrar ----------

    @Test
    void deveRegistrarInvestimentoComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(investimentoRepository.save(any(Investimento.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        InvestimentoRequestDTO dto = new InvestimentoRequestDTO(
                "  Tesouro Selic  ", TipoInvestimento.RENDA_FIXA, new BigDecimal("3000.00"), 1L);

        InvestimentoResponseDTO resultado = investimentoService.registrar(dto);

        assertEquals(LocalDate.now(), resultado.dataRegistro());
        assertEquals("Tesouro Selic", resultado.nome());
        assertEquals(new BigDecimal("3000.00"), resultado.valor());
        assertEquals(TipoInvestimento.RENDA_FIXA, resultado.tipo());

        ArgumentCaptor<Investimento> captor = ArgumentCaptor.forClass(Investimento.class);
        verify(investimentoRepository).save(captor.capture());
        assertEquals("Tesouro Selic", captor.getValue().getNome());
        assertEquals(LocalDate.now(), captor.getValue().getDataRegistro());
    }

    @Test
    void deveLancarExcecaoAoRegistrarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        InvestimentoRequestDTO dto = new InvestimentoRequestDTO(
                "Tesouro Selic", TipoInvestimento.RENDA_FIXA, new BigDecimal("3000.00"), 99L);

        assertThrows(ResourceNotFoundException.class, () -> investimentoService.registrar(dto));

        verify(investimentoRepository, never()).save(any());
    }

    // ---------- listar ----------

    @Test
    void deveListarInvestimentosDoUsuarioJaMapeados() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(investimentoRepository.findByUsuarioIdOrderByDataRegistroDescIdDesc(1L)).thenReturn(List.of(
                investimentoDe(2L, "Fundo DI", TipoInvestimento.FUNDOS, "1000.00"),
                investimentoDe(1L, "CDB", TipoInvestimento.RENDA_FIXA, "3000.00")));

        List<InvestimentoResponseDTO> resultado = investimentoService.listar(1L);

        assertEquals(2, resultado.size());

        assertEquals(2L, resultado.get(0).id());
        assertEquals("Fundo DI", resultado.get(0).nome());
        assertEquals(TipoInvestimento.FUNDOS, resultado.get(0).tipo());
        assertEquals("Fundos", resultado.get(0).tipoDescricao());
        assertEquals(new BigDecimal("1000.00"), resultado.get(0).valor());

        assertEquals(1L, resultado.get(1).id());
        assertEquals("Renda fixa", resultado.get(1).tipoDescricao());
    }

    // ---------- carteira ----------

    @Test
    void deveCalcularCarteiraComExemploDeReferencia() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(investimentoRepository.findByUsuarioIdOrderByDataRegistroDescIdDesc(1L)).thenReturn(List.of(
                investimentoDe(1L, "CDB", TipoInvestimento.RENDA_FIXA, "3000.00"),
                investimentoDe(2L, "Tesouro", TipoInvestimento.RENDA_FIXA, "1000.00"),
                investimentoDe(3L, "Fundo DI", TipoInvestimento.FUNDOS, "1000.00")));

        CarteiraInvestimentoDTO carteira = investimentoService.carteira(1L);

        assertEquals(new BigDecimal("5000.00"), carteira.total());
        assertEquals(2, carteira.itens().size());

        // RENDA_FIXA aparece primeiro (maior valor)
        assertEquals(TipoInvestimento.RENDA_FIXA, carteira.itens().get(0).tipo());
        assertEquals("Renda fixa", carteira.itens().get(0).nome());
        assertEquals(new BigDecimal("4000.00"), carteira.itens().get(0).valor());
        assertEquals(new BigDecimal("80.00"), carteira.itens().get(0).percentual());

        // FUNDOS vem depois
        assertEquals(TipoInvestimento.FUNDOS, carteira.itens().get(1).tipo());
        assertEquals(new BigDecimal("1000.00"), carteira.itens().get(1).valor());
        assertEquals(new BigDecimal("20.00"), carteira.itens().get(1).percentual());
    }

    @Test
    void deveRetornarCarteiraVaziaQuandoUsuarioNaoTemInvestimentos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(investimentoRepository.findByUsuarioIdOrderByDataRegistroDescIdDesc(1L))
                .thenReturn(List.of());

        CarteiraInvestimentoDTO carteira = investimentoService.carteira(1L);

        assertTrue(carteira.itens().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(carteira.total()));
    }

    // ---------- excluir ----------

    @Test
    void deveLancarExcecaoAoExcluirInvestimentoDeOutroUsuario() {
        when(usuarioRepository.existsById(2L)).thenReturn(true);
        // o investimento 10 pertence a outro usuário, então a busca por (id, usuarioId) volta vazia
        when(investimentoRepository.findByIdAndUsuarioId(10L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> investimentoService.excluir(10L, 2L));

        verify(investimentoRepository, never()).delete(any());
    }

    @Test
    void deveExcluirInvestimentoDoProprioUsuario() {
        Investimento investimento = investimentoDe(10L, "CDB", TipoInvestimento.RENDA_FIXA, "3000.00");
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(investimentoRepository.findByIdAndUsuarioId(10L, 1L)).thenReturn(Optional.of(investimento));

        investimentoService.excluir(10L, 1L);

        verify(investimentoRepository).delete(investimento);
    }
}
