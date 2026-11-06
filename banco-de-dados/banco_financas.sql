-- =====================================================
-- Banco de dados: Gestão Financeira Pessoal (PostgreSQL)
-- Execute este arquivo inteiro (Alt + X no DBeaver)
-- com o banco "financas" selecionado.
-- =====================================================

-- Limpeza (permite rodar o script mais de uma vez sem erro)
DROP TABLE IF EXISTS relatorio_categoria CASCADE;
DROP TABLE IF EXISTS relatorio_mensal CASCADE;
DROP TABLE IF EXISTS meta_financeira CASCADE;
DROP TABLE IF EXISTS historico_investimento CASCADE;
DROP TABLE IF EXISTS investimento CASCADE;
DROP TABLE IF EXISTS score_financeira CASCADE;
DROP TABLE IF EXISTS insight CASCADE;
DROP TABLE IF EXISTS transacao CASCADE;
DROP TABLE IF EXISTS orcamento CASCADE;
DROP TABLE IF EXISTS renda CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- =====================================================
-- CRIAÇÃO DAS TABELAS
-- =====================================================

CREATE TABLE usuario (
    id_usuario SERIAL,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(250) NOT NULL,
    cpf CHAR(11) NOT NULL,
    senha CHAR(64) NOT NULL,
    telefone VARCHAR(15),
    logradouro VARCHAR(100),
    numero_endereco VARCHAR(10),
    bairro VARCHAR(50),
    cidade VARCHAR(50),
    estado CHAR(2),
    cep CHAR(8),
    data_cadastro DATE NOT NULL,
    PRIMARY KEY (id_usuario),
    UNIQUE (email),
    UNIQUE (cpf)
);

CREATE TABLE categoria (
    id_categoria SERIAL,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_categoria)
);

CREATE TABLE renda (
    id_renda SERIAL,
    salario_liquido DECIMAL(10,2) NOT NULL,
    receitas_variaveis DECIMAL(10,2),
    receitas_recorrentes DECIMAL(10,2),
    data_registro_renda DATE NOT NULL,
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_renda),
    CONSTRAINT fk_renda_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE orcamento (
    id_orcamento SERIAL,
    limite DECIMAL(10,2) NOT NULL,
    percentual_utilizado DECIMAL(5,2),
    status VARCHAR(10) NOT NULL CHECK (status IN ('normal', 'atencao', 'risco')),
    periodo_referencia DATE NOT NULL,
    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    PRIMARY KEY (id_orcamento),
    CONSTRAINT fk_orcamento_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_orcamento_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id_categoria)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE transacao (
    id_transacao SERIAL,
    descricao VARCHAR(350) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_transacao DATE NOT NULL,
    recorrencia VARCHAR(50),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    usuario_id INT NOT NULL,
    categoria_id INT NOT NULL,
    PRIMARY KEY (id_transacao),
    CONSTRAINT fk_transacao_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_transacao_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id_categoria)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE insight (
    id_insight SERIAL,
    tipo VARCHAR(50) NOT NULL,
    data DATE NOT NULL,
    mensagem VARCHAR(250) NOT NULL,
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_insight),
    CONSTRAINT fk_insight_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE score_financeira (
    id_score SERIAL,
    pontuacao INT NOT NULL,
    classificacao_faixa VARCHAR(50) NOT NULL,
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_score),
    CONSTRAINT fk_score_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE investimento (
    id_investimento SERIAL,
    reserva_emergencial DECIMAL(10,2),
    percentual_minimo_investimento DECIMAL(10,2),
    metas_sugeridas VARCHAR(250),
    data_inicio_investimento DATE,
    data_fim_investimento DATE,
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_investimento),
    CONSTRAINT fk_investimento_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE historico_investimento (
    id_historico_investimento SERIAL,
    tipo VARCHAR(250) NOT NULL,
    data_registro DATE NOT NULL,
    transacao_id INT NOT NULL,
    investimento_id INT NOT NULL,
    PRIMARY KEY (id_historico_investimento),
    CONSTRAINT fk_historico_transacao
        FOREIGN KEY (transacao_id)
        REFERENCES transacao(id_transacao)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_historico_investimento
        FOREIGN KEY (investimento_id)
        REFERENCES investimento(id_investimento)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE meta_financeira (
    id_meta_financeira SERIAL,
    nome VARCHAR(250) NOT NULL,
    data_estimada DATE,
    valor_objetivo DECIMAL(10,2) NOT NULL,
    valor_acumulado DECIMAL(10,2) DEFAULT 0.00,
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_meta_financeira),
    CONSTRAINT fk_meta_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE relatorio_mensal (
    id_relatorio_mensal SERIAL,
    total_gasto DECIMAL(10,2),
    total_recebido DECIMAL(10,2),
    evolucao_relacao_mes_anterior VARCHAR(250),
    principais_categorias VARCHAR(250),
    ranking_categorias VARCHAR(250),
    usuario_id INT NOT NULL,
    PRIMARY KEY (id_relatorio_mensal),
    CONSTRAINT fk_relatorio_mensal_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE relatorio_categoria (
    id_relatorio_categoria SERIAL,
    percentual DECIMAL(5,2),
    data_registro DATE NOT NULL,
    valor DECIMAL(10,2),
    relatorio_mensal_id INT NOT NULL,
    PRIMARY KEY (id_relatorio_categoria),
    CONSTRAINT fk_relatorio_categoria_mensal
        FOREIGN KEY (relatorio_mensal_id)
        REFERENCES relatorio_mensal(id_relatorio_mensal)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- =====================================================
-- DADOS DE TESTE (fictícios)
-- A ordem importa: primeiro as tabelas "pai", depois as "filhas".
-- =====================================================

INSERT INTO usuario (nome, data_nascimento, email, cpf, senha, data_cadastro)
VALUES ('Usuario Teste', '2001-11-19', 'teste@email.com', '12345678945', 'senha@123', '2026-10-01');

INSERT INTO categoria (nome)
VALUES ('Alimentação'), ('Transporte'), ('Lazer'), ('Salário');

INSERT INTO renda (salario_liquido, receitas_variaveis, receitas_recorrentes, data_registro_renda, usuario_id)
VALUES (3000.00, 200.00, 100.00, '2026-10-05', 1);

INSERT INTO orcamento (limite, percentual_utilizado, status, periodo_referencia, usuario_id, categoria_id)
VALUES (500.00, 5.10, 'normal', '2026-10-01', 1, 1);

INSERT INTO transacao (descricao, valor, data_transacao, tipo, usuario_id, categoria_id)
VALUES
    ('Almoço', 25.50, '2026-10-03', 'despesa', 1, 1),
    ('Ônibus', 4.80, '2026-10-03', 'despesa', 1, 2),
    ('Salário de outubro', 3000.00, '2026-10-05', 'receita', 1, 4);

INSERT INTO insight (tipo, data, mensagem, usuario_id)
VALUES ('alerta', '2026-10-05', 'Seus gastos com alimentação estão dentro do limite.', 1);

INSERT INTO score_financeira (pontuacao, classificacao_faixa, usuario_id)
VALUES (750, 'Bom', 1);

INSERT INTO investimento (reserva_emergencial, percentual_minimo_investimento, metas_sugeridas, data_inicio_investimento, data_fim_investimento, usuario_id)
VALUES (9000.00, 10.00, 'Guardar 10% da renda por mês', '2026-10-01', '2027-10-01', 1);

INSERT INTO historico_investimento (tipo, data_registro, transacao_id, investimento_id)
VALUES ('aporte', '2026-10-06', 3, 1);

INSERT INTO meta_financeira (nome, data_estimada, valor_objetivo, valor_acumulado, usuario_id)
VALUES ('Viagem', '2027-07-01', 5000.00, 800.00, 1);

INSERT INTO relatorio_mensal (total_gasto, total_recebido, evolucao_relacao_mes_anterior, principais_categorias, ranking_categorias, usuario_id)
VALUES (30.30, 3000.00, 'Gastos 5% menores que o mês anterior', 'Alimentação, Transporte', '1. Alimentação 2. Transporte', 1);

INSERT INTO relatorio_categoria (percentual, data_registro, valor, relatorio_mensal_id)
VALUES (84.16, '2026-10-31', 25.50, 1);

-- =====================================================
-- CONSULTA DE TESTE
-- =====================================================
SELECT u.nome, c.nome AS categoria, t.descricao, t.valor, t.tipo
FROM transacao t
JOIN usuario u ON u.id_usuario = t.usuario_id
JOIN categoria c ON c.id_categoria = t.categoria_id;
