# EPIC 11 — Evidências de Mayane

Medição das consultas: **RNF016** (grande volume), **RNF017** (consultas em até 2 segundos) e **RNF019** (informação em tempo real).

## Ambiente de medição

- **Data:** [13/11/2026]
- **Volume de dados:** 100.000 transações (1 usuário de carga, 12 categorias, 3 orçamentos)
- **Processador:** [Intel(R) Core(TM) i5-9400F CPU @ 2.90GHz (2.90 GHz)]
- **Memória RAM reconhecida pelo sistema:** [16,0 GB]
- **Banco de dados:** PostgreSQL [18] — banco `financas_medicao`
- **Aplicação:** Spring Boot 4.1.1 / Java 21
- **Como o app foi iniciado:** com `--spring.jpa.show-sql=false` e `--spring.jpa.properties.hibernate.format_sql=false`
- **Método:** 1 chamada de aquecimento (descartada da média) + 5 chamadas medidas por endpoint, via PowerShell (`medir-consultas.ps1`). Só entram na média chamadas com HTTP 200.

## RNF017 — Consultas respondem em até 2 segundos

**Critério:** cada consulta responde em menos de 2.000 ms.

### 1. Antes da V7 (banco migrado até a V6)

| Endpoint | Meta (ms) | Média / Maior | Passou? |
|---|---:|---:|---|
| `GET /consulta/resumo` | 2000 | [847,49 ms] / [888,15 ms] | [Sim] |
| `GET /gastos/por-categoria` | 2000 | [745,08 ms] / [772,22 ms] | [Sim] |
| `GET /gastos/por-estabelecimento` | 2000 | [743,36 ms] / [771,26 ms] | [Sim] |
| `GET /gastos/por-periodo` | 2000 | [745,46 ms] / [777,07 ms] | [Sim] |
| `GET /evolucao/despesas` | 2000 | [829,17 ms] / [867,06 ms] | [Sim] |
| `GET /evolucao/receitas` | 2000 | [808,38 ms] / [846,28 m] | [Sim] |
| `GET /evolucao/comparativo` | 2000 | [833,22 ms] / [864,43 ms] | [Sim] |

### 2. Depois da V7 (banco migrado até a V8)

| Endpoint | Meta (ms) | Média / Maior | Passou? |
|---|---:|---:|---|
| `GET /consulta/resumo` | 2000 | 945,62 ms / 1.491,78 ms | Sim |
| `GET /gastos/por-categoria` | 2000 | 706,07 ms / 722,80 ms | Sim |
| `GET /gastos/por-estabelecimento` | 2000 | 744,79 ms / 772,30 ms | Sim |
| `GET /gastos/por-periodo` | 2000 | 740,29 ms / 763,27 ms | Sim |
| `GET /evolucao/despesas` | 2000 | 740,29 ms / 763,27 ms | Sim |
| `GET /evolucao/receitas` | 2000 | 812,85 ms / 863,75 ms | Sim |
| `GET /evolucao/comparativo` | 2000 | 921,27 ms / 1.243,55 ms | Sim |

### 3. Comparação (modelo do Epic)

| Endpoint | Meta (ms) | Antes da V7 (média / maior) | Depois da V7 (média / maior) | Passou? |
|---|---:|---:|---:|---|
| `GET /consulta/resumo` | 2000 | 847,49 ms / 888,15 ms | 945,62 ms / 1.491,78 ms | Sim |
| `GET /gastos/por-categoria` | 2000 | 745,08 ms / 772,22 ms | 706,07 ms / 722,80 ms | Sim |
| `GET /gastos/por-estabelecimento` | 2000 | 743,36 ms / 771,26 ms | 744,79 ms / 772,30 ms | Sim |
| `GET /gastos/por-periodo` | 2000 | 745,46 ms / 777,07 ms | 740,29 ms / 763,27 ms | Sim |
| `GET /evolucao/despesas` | 2000 | 829,17 ms / 867,06 ms | 807,91 ms / 845,06 ms | Sim |
| `GET /evolucao/receitas` | 2000 | 808,38 ms / 846,28 ms | 812,85 ms / 863,75 ms | Sim |
| `GET /evolucao/comparativo` | 2000 | 833,22 ms / 864,43 ms | 921,27 ms / 1.243,55 ms | Sim |

### 4. Primeira chamada (a frio)

A primeira chamada de cada endpoint foi descartada da média. Na medição "depois", a primeira chamada do endpoint `GET /consulta/resumo` registrou 1.491,78 ms (e o comparativo atingiu 1.243,55 ms na 3ª amostra), refletindo o aquecimento inicial da aplicação e carregamento de contexto, mantendo-se ainda confortavelmente abaixo da meta de 2.000 ms.

## RNF016 — Grande volume de transações sem degradação perceptível

**Critério adotado (proposta ao PO):** com 100.000 transações, as consultas continuam dentro do limite de 2 segundos.

| Endpoint | 100 transações (média / maior) | 100.000 transações (média / maior) |
|---|---:|---:|
| `GET /consulta/resumo` | *N/A (opcional)* | 945,62 ms / 1.491,78 ms |
| `GET /evolucao/despesas` | *N/A (opcional)* | 807,91 ms / 845,06 ms |

Todas as consultas ficaram abaixo de 2.000 ms com 100.000 transações.

## RNF019 — Informação em tempo real

**Critério:** os dados exibidos refletem imediatamente o que foi cadastrado.

**Fundamento no código:** nenhum serviço usa cache (`@Cacheable`) nem tarefa agendada (`@Scheduled`); cada consulta lê as transações no banco e calcula o resultado no momento da chamada. Nenhum total é salvo previamente.

**Teste prático:**

1. `GET /consulta/resumo?usuarioId=1` → `totalDespesas` = 23448926.68
2. `POST /transacoes` de uma despesa de R$ 100,00 para o mesmo usuário → HTTP 200
3. `GET /consulta/resumo?usuarioId=1` imediatamente depois → `totalDespesas` = 23449026.68
4. Diferença: 100.00 (esperado: 100,00)

**Resultado:** Atendido

## Observações técnicas

- Nenhuma chamada foi descartada por falha (5 de 5 requisições retornaram HTTP 200 em todas as etapas).
- Houve um pico isolado na primeira medição de resumo (1.491,78 ms) e no comparativo (1.243,55 ms), o que é normal devido à inicialização de conexões e execução inicial pelo motor do banco.
- As medições representam o tempo de resposta observado pelo PowerShell em chamadas locais à API.
- Todos os endpoints medidos carregam as transações do usuário na memória e agregam em Java (`findByUsuarioId` / `findByUsuarioIdAndTipo`). Com um único usuário concentrando as 100.000 linhas, o índice por `usuario_id` tem pouco a filtrar, mantendo o comportamento estável antes e depois da V7.

## Conclusão

Nas medições válidas, todas as consultas ficaram abaixo do limite de 2 segundos, com 100.000 transações, tanto antes quanto depois da V7. O RNF017 foi atendido nas condições do ambiente de medição utilizado. O RNF016 foi atendido pelo mesmo critério. O RNF019 foi atendido, pois as consultas são calculadas no momento da chamada, sem cache nem dados pré-calculados.