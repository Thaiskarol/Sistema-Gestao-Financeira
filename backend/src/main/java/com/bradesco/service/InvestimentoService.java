package com.bradesco.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.request.InvestimentoRequestDTO;
import com.bradesco.dto.response.CarteiraInvestimentoDTO;
import com.bradesco.dto.response.InvestimentoResponseDTO;
import com.bradesco.dto.response.ItemCarteiraDTO;
import com.bradesco.entity.Investimento;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.TipoInvestimento;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.InvestimentoRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class InvestimentoService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final InvestimentoRepository investimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public InvestimentoService(InvestimentoRepository investimentoRepository,
                               UsuarioRepository usuarioRepository) {
        this.investimentoRepository = investimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Decisão da PO: o investimento NÃO gera transação e NÃO altera patrimônio nem score.
    @Transactional
    public InvestimentoResponseDTO registrar(InvestimentoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Investimento investimento = new Investimento();
        investimento.setNome(dto.nome().trim());
        investimento.setTipo(dto.tipo());
        investimento.setValor(dto.valor());
        investimento.setDataRegistro(LocalDate.now());
        investimento.setUsuario(usuario);

        return toResponse(investimentoRepository.save(investimento));
    }

    @Transactional(readOnly = true)
    public List<InvestimentoResponseDTO> listar(Long usuarioId) {
        validarUsuario(usuarioId);
        return investimentoRepository
                .findByUsuarioIdOrderByDataRegistroDescIdDesc(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CarteiraInvestimentoDTO carteira(Long usuarioId) {
        validarUsuario(usuarioId);

        Map<TipoInvestimento, BigDecimal> totais = new EnumMap<>(TipoInvestimento.class);
        investimentoRepository.findByUsuarioIdOrderByDataRegistroDescIdDesc(usuarioId)
                .forEach(i -> totais.merge(i.getTipo(), i.getValor(), BigDecimal::add));

        BigDecimal total = totais.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ItemCarteiraDTO> itens = totais.entrySet().stream()
                .map(e -> new ItemCarteiraDTO(
                        e.getKey(),
                        e.getKey().getDescricao(),
                        e.getValue(),
                        total.signum() == 0
                                ? BigDecimal.ZERO.setScale(2)
                                : e.getValue().multiply(CEM).divide(total, 2, RoundingMode.HALF_UP)))
                .sorted(Comparator.comparing(ItemCarteiraDTO::valor).reversed())
                .toList();

        return new CarteiraInvestimentoDTO(itens, total);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        validarUsuario(usuarioId);
        Investimento investimento = investimentoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Investimento não encontrado."));
        investimentoRepository.delete(investimento);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }

    private InvestimentoResponseDTO toResponse(Investimento i) {
        return new InvestimentoResponseDTO(
                i.getId(),
                i.getNome(),
                i.getTipo(),
                i.getTipo().getDescricao(),
                i.getValor(),
                i.getDataRegistro());
    }
}