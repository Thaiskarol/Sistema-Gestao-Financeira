CREATE INDEX idx_transacao_usuario_data ON transacao (usuario_id, data_transacao);
CREATE INDEX idx_orcamento_usuario ON orcamento (usuario_id);
CREATE INDEX idx_renda_usuario ON renda (usuario_id);
CREATE INDEX idx_meta_usuario ON meta (usuario_id);