package com.bradesco.service;

import com.bradesco.dto.response.DistribuicaoFinanceiraDTO;
import com.bradesco.entity.Renda;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.RendaRepository;
import com.bradesco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DistribuicaoFinanceiraService {

    private static final BigDecimal NECESSIDADES = new BigDecimal("0.50");
    private static final BigDecimal DESEJOS = new BigDecimal("0.30");
    private static final BigDecimal INVESTIMENTOS = new BigDecimal("0.20");

    private final RendaRepository rendaRepository;
    private final UsuarioRepository usuarioRepository;

    public DistribuicaoFinanceiraService(RendaRepository rendaRepository,
                                         UsuarioRepository usuarioRepository) {
        this.rendaRepository = rendaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public DistribuicaoFinanceiraDTO calcularDistribuicao(Long usuarioId) {
        validarUsuario(usuarioId);

        List<Renda> rendas = rendaRepository.findByUsuarioId(usuarioId);

        BigDecimal rendaTotal = rendas.stream()
            .map(Renda::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal necessidades = rendaTotal.multiply(NECESSIDADES)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal desejos = rendaTotal.multiply(DESEJOS)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal investimentos = rendaTotal.multiply(INVESTIMENTOS)
            .setScale(2, RoundingMode.HALF_UP);

        return new DistribuicaoFinanceiraDTO(
            rendaTotal.setScale(2, RoundingMode.HALF_UP),
            necessidades, desejos, investimentos);
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }
}
