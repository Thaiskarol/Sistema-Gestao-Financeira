package com.bradesco.service;

import com.bradesco.dto.response.GrupoValorDTO;
import com.bradesco.dto.response.RelatorioCategoriaDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DistribuicaoCategoriaService {
  
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final GastosService gastosService;

    public DistribuicaoCategoriaService(GastosService gastosService) {
        this.gastosService = gastosService;
    }

    public List<RelatorioCategoriaDTO> distribuicaoPorCategoria(Long usuarioId) {

        // Já vem validado, agrupado e ordenado do maior para o menor gasto
        List<GrupoValorDTO> gastos = gastosService.gastosPorCategoria(usuarioId);

        BigDecimal total = gastos.stream()
                .map(GrupoValorDTO::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Sem gastos: lista vazia, sem dividir por zero
        if (total.signum() == 0) {
            return List.of();
        }

        // O stream preserva a ordem, então a posição na lista é o ranking
        return gastos.stream()
                .map(g -> new RelatorioCategoriaDTO(
                        g.rotulo(),
                        g.valor(),
                        g.valor().multiply(CEM).divide(total, 2, RoundingMode.HALF_UP)))
                .toList();
    }
}
