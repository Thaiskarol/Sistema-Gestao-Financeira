package com.bradesco.service;

import com.bradesco.dto.response.MetaSugeridaDTO;
import com.bradesco.dto.response.RecomendacaoGeralDTO;
import com.bradesco.dto.response.RecomendacaoInvestimentoDTO;
import com.bradesco.dto.response.ReservaEmergenciaDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecomendacaoGeralService {

    private final RecomendacaoInvestimentoService recomendacaoInvestimentoService;
    private final PatrimonioService patrimonioService;

    public RecomendacaoGeralService(
            RecomendacaoInvestimentoService recomendacaoInvestimentoService,
            PatrimonioService patrimonioService) {

        this.recomendacaoInvestimentoService = recomendacaoInvestimentoService;
        this.patrimonioService = patrimonioService;
    }

    @Transactional(readOnly = true)
    public RecomendacaoGeralDTO gerarRecomendacoes(Long usuarioId) {

        RecomendacaoInvestimentoDTO investimento =
                recomendacaoInvestimentoService.recomendarPercentualMinimo(usuarioId);

        ReservaEmergenciaDTO reserva =
                recomendacaoInvestimentoService.calcularReservaEmergencia(usuarioId);

        List<MetaSugeridaDTO> metasSugeridas =
                patrimonioService.metasSugeridas(usuarioId);

        String insight =
                "Com sua renda atual, recomenda-se investir pelo menos R$ "
                        + investimento.valorRecomendado()
                        + " por mês para construir sua reserva de emergência.";

        return new RecomendacaoGeralDTO(
                investimento.rendaIdentificada(),
                investimento,
                reserva,
                metasSugeridas,
                insight
        );
    }
}