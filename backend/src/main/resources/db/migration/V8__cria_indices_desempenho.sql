CREATE INDEX IF NOT EXISTS idx_transacao_usuario_data ON transacao (usuario_id, data_transacao);
CREATE INDEX IF NOT EXISTS idx_orcamento_usuario ON orcamento (usuario_id);
CREATE INDEX IF NOT EXISTS idx_renda_usuario ON renda (usuario_id);
CREATE INDEX IF NOT EXISTS idx_meta_usuario ON meta (usuario_id);