# EPIC 11 — Evidências de William

## RNF018 — Tempo de cadastro de transação

**Endpoint:** POST /transacoes  
**Ambiente:** PostgreSQL — banco financas_medicao  
**Aplicação:** Spring Boot  
**Critério:** cadastro em menos de 500 ms.

### 1. Medição antes da V7

- Requisições realizadas: 20
- Requisições com HTTP 201: 20
- Tempo médio: 43,98 ms
- Maior tempo: 302,57 ms
- Resultado: dentro do limite de 500 ms.

### 2. Primeira medição depois da V7

- Requisições realizadas: 20
- Requisições com HTTP 201: 20
- Tempo médio: 77,47 ms
- Maior tempo: 1.026,98 ms
- Resultado: média dentro do limite, mas maior tempo acima de 500 ms.

### 3. Segunda medição depois da V7

- Requisições realizadas: 20
- Requisições com HTTP 201: 20
- Tempo médio: 33,23 ms
- Maior tempo: 41,27 ms
- Resultado: dentro do limite de 500 ms.

### 4. Comparação

| Indicador | Antes da V7 | Depois da V7 - 1ª amostra | Depois da V7 - 2ª amostra |
|---|---:|---:|---:|
| Requisições válidas | 20 | 20 | 20 |
| Média | 43,98 ms | 77,47 ms | 33,23 ms |
| Maior tempo | 302,57 ms | 1.026,98 ms | 41,27 ms |

### 5. Observações técnicas

- Todas as requisições consideradas nas medições retornaram HTTP 201.
- Uma tentativa anterior que retornou HTTP 404 foi descartada.
- A primeira amostra depois da V7 apresentou um pico isolado de 1.026,98 ms.
- Na segunda amostra, o maior tempo foi de 41,27 ms.
- Não é possível afirmar que a V7 causou ou eliminou a variação observada.
- O banco de medição foi criado sem a tabela renda_legado. Portanto, a V7 foi executada, mas não encontrou registros legados para migrar.
- As medições representam o tempo de resposta observado pelo PowerShell em chamadas locais à API.

### 6. Conclusão

Antes da V7, todas as 20 requisições ficaram abaixo de 500 ms.

Depois da V7, a primeira amostra apresentou uma ocorrência acima do limite. Na segunda amostra, todas as 20 requisições ficaram abaixo de 500 ms.

A segunda amostra atende ao critério de desempenho, mas o pico da primeira deve permanecer registrado como evidência da variação observada.

---

## Outros requisitos do EPIC 11

As evidências dos demais requisitos serão acrescentadas após suas respectivas verificações.

---

## RNF020 — Tempo de geração do relatório mensal

**Endpoint:** GET /relatorios/mensal?usuarioId=1  
**Ambiente:** PostgreSQL — banco financas_medicao  
**Aplicação:** Spring Boot  
**Critério:** geração do relatório em menos de 10 segundos.

### 1. Medições

Foram realizadas cinco requisições válidas ao endpoint:

| Medição | Status HTTP | Tempo |
|---|---:|---:|
| 1 | 200 | 1.365,88 ms |
| 2 | 200 | 1.308,02 ms |
| 3 | 200 | 1.262,13 ms |
| 4 | 200 | 1.320,00 ms |
| 5 | 200 | 1.277,45 ms |

### 2. Resultado consolidado

- Requisições válidas: 5
- Respostas HTTP 200: 5
- Tempo médio: 1.306,70 ms
- Menor tempo: 1.262,13 ms
- Maior tempo: 1.365,88 ms
- Limite estabelecido: 10.000 ms (10 segundos).

### 3. Observações técnicas

- Todas as cinco requisições válidas retornaram HTTP 200.
- As chamadas foram realizadas localmente pelo PowerShell.
- O tempo medido corresponde ao tempo de resposta observado pelo cliente HTTP.
- Uma execução anterior apresentou HTTP 201 e tempo de 62.763,45 ms. Ela não foi considerada válida para a amostra, pois não correspondeu ao status esperado do endpoint GET.

### 4. Conclusão

Nas cinco medições válidas, o relatório mensal foi gerado em menos de 10 segundos.

O tempo médio observado foi de aproximadamente 1,31 segundo.

Portanto, o RNF020 foi atendido nas condições do ambiente de medição utilizado.

### 5. Medições antes da V7 — banco financas_medicao_v6

Para comparar o desempenho antes e depois da V7, foram realizadas cinco requisições ao mesmo endpoint, utilizando o banco isolado `financas_medicao_v6`, com as migrações aplicadas até a versão 6.

| Medição | Status HTTP | Tempo |
|---|---:|---:|
| 1 | 200 | 1.338,95 ms |
| 2 | 200 | 1.381,24 ms |
| 3 | 200 | 1.369,63 ms |
| 4 | 200 | 1.275,06 ms |
| 5 | 200 | 1.405,65 ms |

**Resultado consolidado:**

- Requisições válidas: 5
- Respostas HTTP 200: 5
- Tempo médio: 1.354,11 ms
- Menor tempo: 1.275,06 ms
- Maior tempo: 1.405,65 ms

### 6. Comparação entre V6 e V7

| Indicador | Antes da V7 (V6) | Depois da V7 |
|---|---:|---:|
| Requisições válidas | 5 | 5 |
| Tempo médio | 1.354,11 ms | 1.306,70 ms |
| Menor tempo | 1.275,06 ms | 1.262,13 ms |
| Maior tempo | 1.405,65 ms | 1.365,88 ms |

A média observada depois da V7 foi 47,41 ms menor que a média anterior. Essa diferença é pequena e não permite afirmar, isoladamente, que houve ganho significativo de desempenho causado pela V7.

### 7. Conclusão comparativa

Tanto antes quanto depois da V7, as cinco requisições válidas ficaram abaixo do limite de 10 segundos estabelecido pelo RNF020.

Portanto, o requisito foi atendido nas duas versões, nas condições do ambiente de medição utilizado.

### Ambiente de medição

- **Data:** 12/11/2026
- **Volume de dados:** 100.000 transações
- **Processador:** AMD Ryzen 5 7520U with Radeon Graphics
- **Memória RAM reconhecida pelo sistema:** 5,74 GB
- **Banco de dados:** PostgreSQL
- **Aplicação:** Spring Boot / Java 21

