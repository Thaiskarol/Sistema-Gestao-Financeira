Medicao 'depois' (media / maior)

| Endpoint | Meta (ms) | Media / Maior | Requisicoes validas (HTTP 200) |
|---|---:|---:|---:|
| `GET /consulta/resumo` | 2000 | 945,62 ms / 1.491,78 ms | 5 de 5 |
| `GET /gastos/por-categoria` | 2000 | 706,07 ms / 722,80 ms | 5 de 5 |
| `GET /gastos/por-estabelecimento` | 2000 | 744,79 ms / 772,30 ms | 5 de 5 |
| `GET /gastos/por-periodo` | 2000 | 740,29 ms / 763,27 ms | 5 de 5 |
| `GET /evolucao/despesas` | 2000 | 807,91 ms / 845,06 ms | 5 de 5 |
| `GET /evolucao/receitas` | 2000 | 812,85 ms / 863,75 ms | 5 de 5 |
| `GET /evolucao/comparativo` | 2000 | 921,27 ms / 1.243,55 ms | 5 de 5 |

Tabela no modelo do grupo

| Endpoint | Meta (ms) | Antes da V7 (mÃ©dia / maior) | Depois da V7 (mÃ©dia / maior) | Passou? |
|---|---:|---:|---:|:---:|
| `GET /consulta/resumo` | 2000 | 847,49 ms / 888,15 ms | 945,62 ms / 1.491,78 ms | Sim |
| `GET /gastos/por-categoria` | 2000 | 745,08 ms / 772,22 ms | 706,07 ms / 722,80 ms | Sim |
| `GET /gastos/por-estabelecimento` | 2000 | 743,36 ms / 771,26 ms | 744,79 ms / 772,30 ms | Sim |
| `GET /gastos/por-periodo` | 2000 | 745,46 ms / 777,07 ms | 740,29 ms / 763,27 ms | Sim |
| `GET /evolucao/despesas` | 2000 | 829,17 ms / 867,06 ms | 807,91 ms / 845,06 ms | Sim |
| `GET /evolucao/receitas` | 2000 | 808,38 ms / 846,28 ms | 812,85 ms / 863,75 ms | Sim |
| `GET /evolucao/comparativo` | 2000 | 833,22 ms / 864,43 ms | 921,27 ms / 1.243,55 ms | Sim |
