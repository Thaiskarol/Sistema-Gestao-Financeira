CREATE TABLE score_historico (
    id_score_historico BIGSERIAL   PRIMARY KEY,
    mes                VARCHAR(7)  NOT NULL,
    score              INTEGER     NOT NULL CHECK (score BETWEEN 0 AND 100),
    nivel              VARCHAR(20) NOT NULL,
    calculado_em       TIMESTAMP   NOT NULL,
    usuario_id         BIGINT      NOT NULL REFERENCES usuario(id_usuario),
    CONSTRAINT uq_score_historico_usuario_mes UNIQUE (usuario_id, mes)
);