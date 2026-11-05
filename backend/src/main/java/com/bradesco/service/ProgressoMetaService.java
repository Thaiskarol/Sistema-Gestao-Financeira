
package com.bradesco.service;

import com.bradesco.dto.response.PercentualMetaDTO;
import com.bradesco.dto.response.TempoEstimadoMetaDTO;
import com.bradesco.entity.Meta;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.MetaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

@Service
public class ProgressoMetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValorMensalMetaService valorMensalMetaService;

    public ProgressoMetaService(
            MetaRepository metaRepository,
            UsuarioRepository usuarioRepository,
            ValorMensalMetaService valorMensalMetaService) {
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
        this.valorMensalMetaService = valorMensalMetaService;
    }

    @Transactional(readOnly = true)
    public PercentualMetaDTO calcularPercentualAtingido(
            Long metaId, Long usuarioId) {

        validarUsuario(usuarioId);

        Meta meta = buscarMeta(metaId, usuarioId);

        BigDecimal valorObjetivo = meta.getValorObjetivo();
        BigDecimal valorAcumulado = meta.getValorAcumulado();

        BigDecimal percentual = valorAcumulado
                .multiply(BigDecimal.valueOf(100))
                .divide(valorObjetivo, 2, RoundingMode.HALF_UP);

        // O percentual exibido não ultrapassa 100%.
        percentual = percentual.min(new BigDecimal("100.00"));

        boolean metaAtingida =
                valorAcumulado.compareTo(valorObjetivo) >= 0;

        return new PercentualMetaDTO(
                meta.getId(),
                meta.getNome(),
                valorObjetivo,
                valorAcumulado,
                percentual,
                metaAtingida
        );
    }

    @Transactional(readOnly = true)
    public TempoEstimadoMetaDTO calcularTempoEstimado(
            Long metaId, Long usuarioId, BigDecimal aporteMensal) {

        validarUsuario(usuarioId);

        Meta meta = buscarMeta(metaId, usuarioId);

        BigDecimal valorRestante =
                valorMensalMetaService.calcularValorRestante(meta);

        BigDecimal valorMensalConsiderado;

        if (aporteMensal != null && aporteMensal.signum() > 0) {
            valorMensalConsiderado = aporteMensal;
        } else {
            valorMensalConsiderado =
                    valorMensalMetaService.calcularValorMensal(meta);
        }

        int mesesEstimados = 0;

        if (valorRestante.signum() > 0) {
            mesesEstimados = valorRestante
                    .divide(valorMensalConsiderado, 0, RoundingMode.CEILING)
                    .intValue();
        }

        String previsaoConclusao = YearMonth.now()
                .plusMonths(mesesEstimados)
                .toString();

        return new TempoEstimadoMetaDTO(
                meta.getId(),
                meta.getNome(),
                valorRestante,
                valorMensalConsiderado,
                mesesEstimados,
                previsaoConclusao
        );
    }

    private Meta buscarMeta(Long metaId, Long usuarioId) {
        return metaRepository.findByIdAndUsuarioId(metaId, usuarioId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Meta não encontrada."));
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "Usuário não encontrado.");
        }
    }
}
