# EPIC 11 — Resultado final (RNF011 a RNF020)

## Ambiente e método

- Volume: 1 usuário com 100.000 transações nos últimos 24 meses, 12 categorias, 3 orçamentos.
- App iniciado com `show-sql=false` e `format_sql=false`.
- Método: 1 chamada de aquecimento + 5 medições por endpoint, via PowerShell (RNF018: 20 requisições).
- "Antes" = banco até a V6, sem índices. "Depois" = com os índices da `V8__cria_indices_desempenho`.
- Cada integrante mediu na própria máquina. Os tempos só podem ser comparados "antes x depois" do mesmo integrante, não entre integrantes.
- Detalhes: `epic11-samuel.md`, `epic11-mayane.md`, `epic11-william.md`.

## Resumo por requisito

| RNF | Meta | Responsável | Resultado medido | Situação |
|---|---|---|---|---|
| RNF011–015 | Usabilidade | William | Evidência ainda não entregue | **Pendente** |
| RNF016 | Grande volume sem degradação perceptível | Mayane / Samuel | Com 100.000 transações, todas as consultas respondem; as únicas próximas do limite são patrimônio e score | Atendido com ressalva (ver RNF017) |
| RNF017 | Consultas em até 2 s | Mayane / Samuel | 10 de 12 consultas sempre abaixo de 2 s; `/patrimonio/evolucao` e `/score` com picos acima | **Parcialmente atendido** |
| RNF018 | Nova transação < 500 ms | William | 2ª amostra: média 33,23 ms, maior 41,27 ms. 1ª amostra: pico isolado de 1.026,98 ms | Atendido com ressalva |
| RNF019 | Tempo real | Mayane | Despesa de R$ 100,00 refletida imediatamente no resumo; sem cache nem dado pré-calculado | Atendido |
| RNF020 | Relatório mensal < 10 s | William | Média 1.306,70 ms, maior 1.365,88 ms | Atendido |

## RNF017 — tempos por endpoint

| Endpoint | Responsável | Antes (média / maior) | Depois (média / maior) | Até 2 s? |
|---|---|---:|---:|---|
| `GET /consulta/resumo` | Mayane | 847,49 / 888,15 ms | 945,62 / 1.491,78 ms | Sim |
| `GET /gastos/por-categoria` | Mayane | 745,08 / 772,22 ms | 706,07 / 722,80 ms | Sim |
| `GET /gastos/por-estabelecimento` | Mayane | 743,36 / 771,26 ms | 744,79 / 772,30 ms | Sim |
| `GET /gastos/por-periodo` | Mayane | 745,46 / 777,07 ms | 740,29 / 763,27 ms | Sim |
| `GET /evolucao/despesas` | Mayane | 829,17 / 867,06 ms | 807,91 / 845,06 ms | Sim |
| `GET /evolucao/receitas` | Mayane | 808,38 / 846,28 ms | 812,85 / 863,75 ms | Sim |
| `GET /evolucao/comparativo` | Mayane | 833,22 / 864,43 ms | 921,27 / 1.243,55 ms | Sim |
| `GET /patrimonio/evolucao` | Samuel | 1610 / 1794 ms | 1793 / 2135 ms | **Parcial** (média ok, pico acima) |
| `GET /score` | Samuel | 2352 / 2733 ms | 1874 / 2392 ms | **Parcial** (média ok, pico acima) |
| `GET /score/classificacao` | Samuel | 1716 / 1926 ms | 1664 / 1833 ms | Sim |
| `GET /insights/aumento-gastos` | Samuel | 1257 / 1444 ms | 1280 / 1428 ms | Sim |
| `GET /metas` | Samuel | 7 / 11 ms | 8 / 11 ms | Sim |

## Outros tempos

| Requisito | Endpoint | Antes (média / maior) | Depois (média / maior) | Meta |
|---|---|---:|---:|---|
| RNF018 | `POST /transacoes` | 43,98 / 302,57 ms | 1ª: 77,47 / 1.026,98 ms — 2ª: 33,23 / 41,27 ms | < 500 ms |
| RNF020 | `GET /relatorios/mensal` | 1.354,11 / 1.405,65 ms | 1.306,70 / 1.365,88 ms | < 10.000 ms |

## Análise

- Os índices da V8 trouxeram pouco ganho. Os services carregam todas as transações do usuário (`findByUsuarioId`) e agregam em Java; como um único usuário concentra as 100.000 linhas, o índice por `usuario_id` tem pouco a filtrar.
- `/score` é o mais lento porque carrega as transações duas vezes: diretamente e por meio de `PatrimonioService.evolucaoPatrimonial`.
- Os picos foram registrados e não descartados, para não esconder variação.

## Pendências para fechar o Epic

1. RNF011–015: evidências de usabilidade (responsável: William).
2. Otimizar `/patrimonio/evolucao` e `/score` (agregar por mês no banco e reaproveitar o resultado no score), em tarefa separada com dono único, conforme o plano do Epic.