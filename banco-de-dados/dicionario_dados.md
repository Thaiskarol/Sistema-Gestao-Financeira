# Dicionário de Dados - Sistema de Gestão Financeira

Banco de dados: **PostgreSQL 18** | Nome do banco: `financas` | Esquema: `public`

Este documento descreve as 12 tabelas do banco, seus campos, tipos e relacionamentos, para facilitar a integração com o backend.

> As descrições dos campos foram escritas a partir dos nomes e do script do banco. Se alguma regra de negócio for diferente, ajuste aqui para manter o documento fiel ao sistema.

---

## 1. Visão geral dos relacionamentos

A tabela `usuario` é o centro do modelo. Quase todas as outras tabelas pertencem a um usuário.

| Tabela filha | Coluna (FK) | Tabela pai | Ao apagar o pai | Ao atualizar o pai |
|---|---|---|---|---|
| `renda` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `orcamento` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `orcamento` | `categoria_id` | `categoria` | RESTRICT | CASCADE |
| `transacao` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `transacao` | `categoria_id` | `categoria` | RESTRICT | CASCADE |
| `insight` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `score_financeira` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `investimento` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `historico_investimento` | `transacao_id` | `transacao` | CASCADE | CASCADE |
| `historico_investimento` | `investimento_id` | `investimento` | CASCADE | CASCADE |
| `meta_financeira` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `relatorio_mensal` | `usuario_id` | `usuario` | CASCADE | CASCADE |
| `relatorio_categoria` | `relatorio_mensal_id` | `relatorio_mensal` | CASCADE | CASCADE |

**Como ler as regras de exclusão**
- **CASCADE:** ao apagar o registro pai, os registros filhos são apagados junto (ex.: apagar um usuário apaga suas transações, rendas, metas etc.).
- **RESTRICT:** o banco **não deixa** apagar o registro pai se houver filhos (ex.: uma categoria em uso por transações ou orçamentos não pode ser excluída).

### Ordem de criação (por causa das chaves estrangeiras)
`usuario` e `categoria` → `renda`, `orcamento`, `transacao`, `insight`, `score_financeira`, `investimento`, `meta_financeira`, `relatorio_mensal` → `historico_investimento` e `relatorio_categoria`

---

## 2. Tabelas

Legenda: **PK** = chave primária | **FK** = chave estrangeira | **UQ** = valor único | **NN** = obrigatório (NOT NULL)

### 2.1 `usuario`
Dados cadastrais de quem usa o sistema.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_usuario` | SERIAL | PK | Identificador (gerado automaticamente) |
| `nome` | VARCHAR(100) | NN | Nome completo |
| `data_nascimento` | DATE | NN | Data de nascimento |
| `email` | VARCHAR(250) | NN, UQ | E-mail (não pode repetir) |
| `cpf` | CHAR(11) | NN, UQ | CPF, somente números (não pode repetir) |
| `senha` | CHAR(64) | NN | Senha **já criptografada (hash)**, nunca em texto puro |
| `telefone` | VARCHAR(15) | | Telefone |
| `logradouro` | VARCHAR(100) | | Rua/avenida |
| `numero_endereco` | VARCHAR(10) | | Número do endereço |
| `bairro` | VARCHAR(50) | | Bairro |
| `cidade` | VARCHAR(50) | | Cidade |
| `estado` | CHAR(2) | | Sigla do estado (ex.: PE) |
| `cep` | CHAR(8) | | CEP, somente números |
| `data_cadastro` | DATE | NN | Data em que o usuário foi cadastrado |

### 2.2 `categoria`
Categorias usadas em transações e orçamentos (ex.: Alimentação, Transporte).

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_categoria` | SERIAL | PK | Identificador |
| `nome` | VARCHAR(100) | NN | Nome da categoria |

### 2.3 `renda`
Registro da renda do usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_renda` | SERIAL | PK | Identificador |
| `salario_liquido` | DECIMAL(10,2) | NN | Salário líquido |
| `receitas_variaveis` | DECIMAL(10,2) | | Receitas que mudam todo mês |
| `receitas_recorrentes` | DECIMAL(10,2) | | Receitas fixas/recorrentes |
| `data_registro_renda` | DATE | NN | Data do registro |
| `usuario_id` | INT | NN, FK → `usuario` | Dono da renda |

### 2.4 `orcamento`
Limite de gastos por categoria em um período.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_orcamento` | SERIAL | PK | Identificador |
| `limite` | DECIMAL(10,2) | NN | Valor máximo planejado |
| `percentual_utilizado` | DECIMAL(5,2) | | Percentual do limite já gasto |
| `status` | VARCHAR(10) | NN, CHECK | Situação do orçamento. Valores aceitos: `normal`, `atencao`, `risco` |
| `periodo_referencia` | DATE | NN | Período (mês) a que o orçamento se refere |
| `usuario_id` | INT | NN, FK → `usuario` | Dono do orçamento |
| `categoria_id` | INT | NN, FK → `categoria` | Categoria controlada |

### 2.5 `transacao`
Receitas e despesas lançadas pelo usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_transacao` | SERIAL | PK | Identificador |
| `descricao` | VARCHAR(350) | NN | Descrição do lançamento |
| `valor` | DECIMAL(10,2) | NN | Valor |
| `data_transacao` | DATE | NN | Data da transação |
| `recorrencia` | VARCHAR(50) | | Frequência, se repetir (ex.: mensal) |
| `tipo` | VARCHAR(10) | NN, CHECK | Tipo do lançamento. Valores aceitos: `receita`, `despesa` |
| `usuario_id` | INT | NN, FK → `usuario` | Dono da transação |
| `categoria_id` | INT | NN, FK → `categoria` | Categoria da transação |

### 2.6 `insight`
Mensagens/alertas geradas para o usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_insight` | SERIAL | PK | Identificador |
| `tipo` | VARCHAR(50) | NN | Tipo do insight (ex.: alerta) |
| `data` | DATE | NN | Data de geração |
| `mensagem` | VARCHAR(250) | NN | Texto exibido ao usuário |
| `usuario_id` | INT | NN, FK → `usuario` | Usuário que recebe o insight |

### 2.7 `score_financeira`
Pontuação financeira do usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_score` | SERIAL | PK | Identificador |
| `pontuacao` | INT | NN | Pontuação calculada |
| `classificacao_faixa` | VARCHAR(50) | NN | Faixa da pontuação (ex.: Bom) |
| `usuario_id` | INT | NN, FK → `usuario` | Dono da pontuação |

### 2.8 `investimento`
Plano de investimento do usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_investimento` | SERIAL | PK | Identificador |
| `reserva_emergencial` | DECIMAL(10,2) | | Valor da reserva de emergência |
| `percentual_minimo_investimento` | DECIMAL(10,2) | | Percentual mínimo da renda a investir |
| `metas_sugeridas` | VARCHAR(250) | | Metas sugeridas ao usuário |
| `data_inicio_investimento` | DATE | | Início do plano |
| `data_fim_investimento` | DATE | | Fim do plano |
| `usuario_id` | INT | NN, FK → `usuario` | Dono do investimento |

### 2.9 `historico_investimento`
Liga uma transação a um investimento (aportes e movimentações).

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_historico_investimento` | SERIAL | PK | Identificador |
| `tipo` | VARCHAR(250) | NN | Tipo da movimentação (ex.: aporte) |
| `data_registro` | DATE | NN | Data do registro |
| `transacao_id` | INT | NN, FK → `transacao` | Transação relacionada |
| `investimento_id` | INT | NN, FK → `investimento` | Investimento relacionado |

### 2.10 `meta_financeira`
Metas de economia do usuário.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_meta_financeira` | SERIAL | PK | Identificador |
| `nome` | VARCHAR(250) | NN | Nome da meta (ex.: Viagem) |
| `data_estimada` | DATE | | Data prevista para alcançar a meta |
| `valor_objetivo` | DECIMAL(10,2) | NN | Valor que se quer atingir |
| `valor_acumulado` | DECIMAL(10,2) | padrão `0.00` | Quanto já foi guardado |
| `usuario_id` | INT | NN, FK → `usuario` | Dono da meta |

### 2.11 `relatorio_mensal`
Resumo financeiro de um mês.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_relatorio_mensal` | SERIAL | PK | Identificador |
| `total_gasto` | DECIMAL(10,2) | | Total de despesas do mês |
| `total_recebido` | DECIMAL(10,2) | | Total de receitas do mês |
| `evolucao_relacao_mes_anterior` | VARCHAR(250) | | Comparação com o mês anterior |
| `principais_categorias` | VARCHAR(250) | | Categorias de maior peso |
| `ranking_categorias` | VARCHAR(250) | | Ranking das categorias |
| `usuario_id` | INT | NN, FK → `usuario` | Dono do relatório |

### 2.12 `relatorio_categoria`
Detalhe, por categoria, de um relatório mensal.

| Coluna | Tipo | Restrições | Descrição |
|---|---|---|---|
| `id_relatorio_categoria` | SERIAL | PK | Identificador |
| `percentual` | DECIMAL(5,2) | | Percentual da categoria no total |
| `data_registro` | DATE | NN | Data do registro |
| `valor` | DECIMAL(10,2) | | Valor da categoria no período |
| `relatorio_mensal_id` | INT | NN, FK → `relatorio_mensal` | Relatório ao qual pertence |

---

## 3. Dicas para o backend

### Tipos (correspondência sugerida em Java)
| Banco (PostgreSQL) | Java |
|---|---|
| `SERIAL` / `INT` | `Integer` (ou `Long`) |
| `VARCHAR`, `CHAR` | `String` |
| `DECIMAL(10,2)` | `BigDecimal` (nunca `double`, para evitar erro de arredondamento) |
| `DATE` | `LocalDate` |

### Regras importantes
- **Senha:** o campo `senha` é `CHAR(64)`, pensado para guardar um hash. O backend deve criptografar antes de gravar. Não salvar senha em texto puro.
- **Valores fixos:** `transacao.tipo` aceita só `receita` ou `despesa`, e `orcamento.status` aceita só `normal`, `atencao` ou `risco`. Qualquer outro valor é recusado pelo banco.
- **Campos únicos:** `email` e `cpf` não podem se repetir. O backend deve tratar o erro de duplicidade (código `23505` no PostgreSQL).
- **CPF e CEP:** gravar somente números, sem pontos e traços.
- **Exclusão:** apagar um usuário apaga todos os dados dele (CASCADE). Apagar uma categoria em uso é bloqueado (RESTRICT).
- **Nomes de colunas:** estão em `snake_case` (ex.: `data_nascimento`). Ao mapear em entidades, use `@Column(name = "...")` ou a convenção de nomes do framework.

### Exemplo de conexão (Spring Boot / `application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/financas
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
spring.jpa.hibernate.ddl-auto=none
```

- Cada pessoa usa a **senha do próprio PostgreSQL** local. **Não suba a senha real no GitHub.**
- `ddl-auto=none` evita que o framework altere as tabelas. A estrutura do banco é definida apenas pelo arquivo `banco_financas.sql`.
- Dependência necessária no `pom.xml` (Maven): o driver `org.postgresql:postgresql`.

### Como cada integrante cria o banco na própria máquina
1. Instalar o PostgreSQL e o DBeaver.
2. Criar um banco chamado `financas`.
3. Executar o arquivo `banco_financas.sql` (Alt + X no DBeaver).
