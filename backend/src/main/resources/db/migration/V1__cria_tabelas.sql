CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(250) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    telefone VARCHAR(15),
    logradouro VARCHAR(100),
    numero_endereco VARCHAR(10),
    bairro VARCHAR(50),
    cidade VARCHAR(50),
    estado VARCHAR(2),
    cep VARCHAR(8),
    data_cadastro DATE NOT NULL
);

CREATE TABLE categoria (
    id_categoria BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE transacao (
    id_transacao BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(350) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    data_transacao DATE NOT NULL,
    recorrencia VARCHAR(50),
    tipo VARCHAR(10) NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id_usuario),
    categoria_id BIGINT NOT NULL REFERENCES categoria(id_categoria)
);