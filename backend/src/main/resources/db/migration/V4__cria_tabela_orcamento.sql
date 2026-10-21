CREATE TABLE orcamento (
    id_orcamento BIGSERIAL PRIMARY KEY,
    limite NUMERIC(10,2) NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id_usuario),
    categoria_id BIGINT NOT NULL REFERENCES categoria(id_categoria)
);