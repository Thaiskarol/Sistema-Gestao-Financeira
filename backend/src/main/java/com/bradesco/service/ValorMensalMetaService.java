package com.bradesco.service;

import com.bradesco.dto.response.ValorMensalMetaDTO;
import com.bradesco.entity.Meta;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.MetaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Service
public class ValorMensalMetaService {
  
      private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    public ValorMensalMetaService(MetaRepository metaRepository,
                                  UsuarioRepository usuarioRepository) {
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Calculado na hora, a partir dos dados atuais; nada fica salvo (CA06)
    @Transactional(readOnly = true)
    public ValorMensalMetaDTO consultarValorMensal(Long metaId, Long usuarioId) {

        validarUsuario(usuarioId);

        Meta meta = metaRepository.findByIdAndUsuarioId(metaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada."));

        return new ValorMensalMetaDTO(
                meta.getId(),
                meta.getNome(),
                meta.getValorObjetivo(),
                meta.getValorAcumulado(),
                calcularValorRestante(meta),
                meta.getDataDesejada(),
                calcularMesesRestantes(meta),
                calcularValorMensal(meta));
    }

    public BigDecimal calcularValorRestante(Meta meta) {
        BigDecimal restante = meta.getValorObjetivo().subtract(meta.getValorAcumulado());

        // Negativo = meta já atingida
        return restante.signum() < 0 ? BigDecimal.ZERO : restante;
    }

    public int calcularMesesRestantes(Meta meta) {
        long meses = ChronoUnit.MONTHS.between(
                YearMonth.now(), YearMonth.from(meta.getDataDesejada()));

        // Mínimo 1: data no mês atual ou já passada vence "agora" e evita divisão por zero
        return (int) Math.max(1, meses);
    }

    public BigDecimal calcularValorMensal(Meta meta) {
        BigDecimal restante = calcularValorRestante(meta);

        if (restante.signum() == 0) {
            return BigDecimal.ZERO;
        }

        // UP: guardando esse valor todo mês, o usuário chega na meta (nunca faltam centavos)
        return restante.divide(
                BigDecimal.valueOf(calcularMesesRestantes(meta)), 2, RoundingMode.UP);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
