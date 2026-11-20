CREATE TABLE investimento (
    id_investimento BIGSERIAL     PRIMARY KEY,
    nome            VARCHAR(100)  NOT NULL,
    tipo            VARCHAR(20)   NOT NULL
                    CHECK (tipo IN ('RENDA_FIXA', 'FUNDOS', 'ACOES', 'CRIPTOATIVOS', 'OUTROS')),
    valor           NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    data_registro   DATE          NOT NULL,
    usuario_id      BIGINT        NOT NULL REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_investimento_usuario ON investimento (usuario_id);