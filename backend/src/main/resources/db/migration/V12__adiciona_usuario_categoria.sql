-- Categoria com usuario_id NULO = categoria padrão (as 12 que já existem).
-- Categoria com usuario_id preenchido = categoria criada por aquele usuário.
ALTER TABLE categoria ADD COLUMN usuario_id BIGINT REFERENCES usuario(id_usuario);

CREATE INDEX idx_categoria_usuario ON categoria (usuario_id);

-- Um usuário não pode ter duas categorias com o mesmo nome (sem diferenciar maiúsculas)
CREATE UNIQUE INDEX uq_categoria_usuario_nome
    ON categoria (usuario_id, LOWER(nome))
    WHERE usuario_id IS NOT NULL;