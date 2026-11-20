package com.bradesco.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.response.ScoreHistoricoDTO;
import com.bradesco.dto.response.ScoreSaudeFinanceiraDTO;
import com.bradesco.entity.ScoreHistorico;
import com.bradesco.entity.Usuario;
import com.bradesco.enums.NivelSaudeFinanceira;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.ScoreHistoricoRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class ScoreHistoricoService {

    private final ScoreHistoricoRepository scoreHistoricoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClassificacaoScoreService classificacaoScoreService;

    public ScoreHistoricoService(ScoreHistoricoRepository scoreHistoricoRepository,
                                 UsuarioRepository usuarioRepository,
                                 ClassificacaoScoreService classificacaoScoreService) {
        this.scoreHistoricoRepository = scoreHistoricoRepository;
        this.usuarioRepository = usuarioRepository;
        this.classificacaoScoreService = classificacaoScoreService;
    }

    // Cria ou atualiza a "foto" do score do mês corrente. Sem dados suficientes, não grava.
    @Transactional
    public void registrar(Long usuarioId, ScoreSaudeFinanceiraDTO score) {
        if (!score.dadosSuficientes()) {
            return;
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        String mes = YearMonth.now().toString();
        NivelSaudeFinanceira nivel = classificacaoScoreService.classificar(score.score());

        ScoreHistorico historico = scoreHistoricoRepository
                .findByUsuarioIdAndMes(usuarioId, mes)
                .orElseGet(() -> {
                    ScoreHistorico novo = new ScoreHistorico();
                    novo.setUsuario(usuario);
                    novo.setMes(mes);
                    return novo;
                });

        historico.setScore(score.score());
        historico.setNivel(nivel);
        historico.setCalculadoEm(LocalDateTime.now());
        scoreHistoricoRepository.save(historico);
    }

    // Últimos 12 meses, do mais antigo para o mais recente (pronto para o gráfico)
    @Transactional(readOnly = true)
    public List<ScoreHistoricoDTO> consultar(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        List<ScoreHistorico> historico =
                new ArrayList<>(scoreHistoricoRepository.findTop12ByUsuarioIdOrderByMesDesc(usuarioId));
        Collections.reverse(historico);

        return historico.stream()
                .map(h -> new ScoreHistoricoDTO(
                        h.getMes(), h.getScore(), h.getNivel(), h.getNivel().getDescricao()))
                .toList();
    }
}