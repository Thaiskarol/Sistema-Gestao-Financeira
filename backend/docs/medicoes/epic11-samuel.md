# Epic 11 — Medições do Samuel (RNF016, RNF017, RNF019)

- Volume: 1 usuário, 100.000 transações (24 meses), 12 categorias, 3 orçamentos, 3 metas
- Data: 2026-11-12
- Máquina: Intel(R) Core(TM) i5-7200U CPU @ 2.50GHz, 8 GB RAM, PostgreSQL 18.6, Java 24.0.2 (compilado em modo 21)
- Método: app com show-sql=false; 1 chamada de aquecimento descartada; 5 medições por endpoint (Measure-Command no PowerShell)

| Endpoint | Meta (ms) | Antes da V7 (média / maior) | Depois da V7 (média / maior) | Passou? |
| --- | --- | --- | --- | --- |
| `GET /patrimonio/evolucao` | 2000 | 1610 / 1794 | 1793 / 2135 | Não |
| `GET /score` | 2000 | 2352 / 2733 | 1874 / 2392 | Não |
| `GET /score/classificacao` | 2000 | 1716 / 1926 | 1664 / 1833 | Sim |
| `GET /insights/aumento-gastos` | 2000 | 1257 / 1444 | 1280 / 1428 | Sim |
| `GET /metas` | 2000 | 7 / 11 | 8 / 11 | Sim |

## Observações

- Patrimônio, score e insights carregam todas as transações do usuário (findByUsuarioId) e agregam em memória; por isso o índice da V7 tem efeito limitado nesses endpoints.
- O score carrega as transações duas vezes (direto e via evolução patrimonial).
- RNF019 (tempo real): atendido pelo desenho do sistema — tudo é calculado no momento da consulta, nada fica armazenado.
- Tempos individuais de cada medição: ver samuel-antes.csv e samuel-depois.csv nesta pasta.