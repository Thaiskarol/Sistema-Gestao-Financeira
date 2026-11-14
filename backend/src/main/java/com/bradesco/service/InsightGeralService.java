package com.bradesco.service;

import com.bradesco.dto.response.InsightDTO;
import com.bradesco.entity.Orcamento;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class InsightGeralService {

    private final UsuarioRepository usuarioRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final AumentoGastosService aumentoGastosService;
    private final QuedaRendaService quedaRendaService;
    private final InsightOrcamentoService insightOrcamentoService;

    public InsightGeralService(UsuarioRepository usuarioRepository,
                               OrcamentoRepository orcamentoRepository,
                               AumentoGastosService aumentoGastosService,
                               QuedaRendaService quedaRendaService,
                               InsightOrcamentoService insightOrcamentoService) {
        this.usuarioRepository = usuarioRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.aumentoGastosService = aumentoGastosService;
        this.quedaRendaService = quedaRendaService;
        this.insightOrcamentoService = insightOrcamentoService;
    }

    @Transactional(readOnly = true)
    public List<InsightDTO> listarInsights(Long usuarioId) {

        validarUsuario(usuarioId);

        List<InsightDTO> insights = new ArrayList<>();

        aumentoGastosService.analisarAumento(usuarioId).ifPresent(insights::add);
        quedaRendaService.analisarQueda(usuarioId).ifPresent(insights::add);

        List<Orcamento> orcamentos = orcamentoRepository.findByUsuarioId(usuarioId).stream()
                .sorted(Comparator.comparing(Orcamento::getId,
                        Comparator.nullsLast(Comparator.<Long>naturalOrder())))
                .toList();

        for (Orcamento orcamento : orcamentos) {
            insightOrcamentoService.orcamentoProximoDoLimite(orcamento.getId()).ifPresent(insights::add);
            insightOrcamentoService.orcamentoExcedido(orcamento.getId()).ifPresent(insights::add);
        }

        return insights;
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
