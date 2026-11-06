CREATE TABLE renda (
    id_renda      BIGSERIAL    PRIMARY KEY,
    descricao     VARCHAR(150),
    valor         NUMERIC(10,2) NOT NULL,
    tipo          VARCHAR(20)   NOT NULL,
    usuario_id    BIGINT        NOT NULL REFERENCES usuario(id_usuario)
);
