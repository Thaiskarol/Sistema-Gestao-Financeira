-- usuário de teste
INSERT INTO usuario (nome, data_nascimento, email, cpf, senha, data_cadastro)
VALUES ('Carga Teste', '2000-01-01', 'carga@teste.com', '11122233344', 'senha123', CURRENT_DATE)
ON CONFLICT (email) DO NOTHING;

-- 100.000 transações nos últimos 24 meses
INSERT INTO transacao (descricao, estabelecimento, valor, data_transacao, tipo, categoria_id, usuario_id)
SELECT
    'Carga ' || g,
    'Loja ' || (g % 50),
    round((random() * 500 + 10)::numeric, 2),
    CURRENT_DATE - (g % 720),
    CASE WHEN g % 10 = 0 THEN 'receita' ELSE 'despesa' END,
    (g % 12) + 1,
    (SELECT id_usuario FROM usuario WHERE email = 'carga@teste.com')
FROM generate_series(1, 100000) AS g;