CREATE TABLE meta (
    id_meta         BIGSERIAL     PRIMARY KEY,
    nome            VARCHAR(100)  NOT NULL,
    valor_objetivo  NUMERIC(12,2) NOT NULL CHECK (valor_objetivo > 0),
    valor_acumulado NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (valor_acumulado >= 0),
    data_desejada   DATE          NOT NULL,
    data_criacao    DATE          NOT NULL,
    usuario_id      BIGINT        NOT NULL REFERENCES usuario(id_usuario)
);
