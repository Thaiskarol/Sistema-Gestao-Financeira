-- public.categoria definição

-- Drop table

-- DROP TABLE public.categoria;

CREATE TABLE public.categoria (
	id_categoria serial4 NOT NULL,
	nome varchar(100) NOT NULL,
	CONSTRAINT categoria_id_categoria_not_null NOT NULL id_categoria,
	CONSTRAINT categoria_nome_not_null NOT NULL nome,
	CONSTRAINT categoria_pkey PRIMARY KEY (id_categoria)
);


-- public.usuario definição

-- Drop table

-- DROP TABLE public.usuario;

CREATE TABLE public.usuario (
	id_usuario serial4 NOT NULL,
	nome varchar(100) NOT NULL,
	data_nascimento date NOT NULL,
	email varchar(250) NOT NULL,
	cpf bpchar(11) NOT NULL,
	senha bpchar(64) NOT NULL,
	telefone varchar(15) NULL,
	logradouro varchar(100) NULL,
	numero_endereco varchar(10) NULL,
	bairro varchar(50) NULL,
	cidade varchar(50) NULL,
	estado bpchar(2) NULL,
	cep bpchar(8) NULL,
	data_cadastro date NOT NULL,
	CONSTRAINT usuario_cpf_key UNIQUE (cpf),
	CONSTRAINT usuario_cpf_not_null NOT NULL cpf,
	CONSTRAINT usuario_data_cadastro_not_null NOT NULL data_cadastro,
	CONSTRAINT usuario_data_nascimento_not_null NOT NULL data_nascimento,
	CONSTRAINT usuario_email_key UNIQUE (email),
	CONSTRAINT usuario_email_not_null NOT NULL email,
	CONSTRAINT usuario_id_usuario_not_null NOT NULL id_usuario,
	CONSTRAINT usuario_nome_not_null NOT NULL nome,
	CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario),
	CONSTRAINT usuario_senha_not_null NOT NULL senha
);


-- public.insight definição

-- Drop table

-- DROP TABLE public.insight;

CREATE TABLE public.insight (
	id_insight serial4 NOT NULL,
	tipo varchar(50) NOT NULL,
	"data" date NOT NULL,
	mensagem varchar(250) NOT NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT insight_data_not_null NOT NULL data,
	CONSTRAINT insight_id_insight_not_null NOT NULL id_insight,
	CONSTRAINT insight_mensagem_not_null NOT NULL mensagem,
	CONSTRAINT insight_pkey PRIMARY KEY (id_insight),
	CONSTRAINT insight_tipo_not_null NOT NULL tipo,
	CONSTRAINT insight_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_insight_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.investimento definição

-- Drop table

-- DROP TABLE public.investimento;

CREATE TABLE public.investimento (
	id_investimento serial4 NOT NULL,
	reserva_emergencial numeric(10, 2) NULL,
	percentual_minimo_investimento numeric(10, 2) NULL,
	metas_sugeridas varchar(250) NULL,
	data_inicio_investimento date NULL,
	data_fim_investimento date NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT investimento_id_investimento_not_null NOT NULL id_investimento,
	CONSTRAINT investimento_pkey PRIMARY KEY (id_investimento),
	CONSTRAINT investimento_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_investimento_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.meta_financeira definição

-- Drop table

-- DROP TABLE public.meta_financeira;

CREATE TABLE public.meta_financeira (
	id_meta_financeira serial4 NOT NULL,
	nome varchar(250) NOT NULL,
	data_estimada date NULL,
	valor_objetivo numeric(10, 2) NOT NULL,
	valor_acumulado numeric(10, 2) DEFAULT 0.00 NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT meta_financeira_id_meta_financeira_not_null NOT NULL id_meta_financeira,
	CONSTRAINT meta_financeira_nome_not_null NOT NULL nome,
	CONSTRAINT meta_financeira_pkey PRIMARY KEY (id_meta_financeira),
	CONSTRAINT meta_financeira_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT meta_financeira_valor_objetivo_not_null NOT NULL valor_objetivo,
	CONSTRAINT fk_meta_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.orcamento definição

-- Drop table

-- DROP TABLE public.orcamento;

CREATE TABLE public.orcamento (
	id_orcamento serial4 NOT NULL,
	limite numeric(10, 2) NOT NULL,
	percentual_utilizado numeric(5, 2) NULL,
	status varchar(10) NOT NULL,
	periodo_referencia date NOT NULL,
	usuario_id int4 NOT NULL,
	categoria_id int4 NOT NULL,
	CONSTRAINT orcamento_categoria_id_not_null NOT NULL categoria_id,
	CONSTRAINT orcamento_id_orcamento_not_null NOT NULL id_orcamento,
	CONSTRAINT orcamento_limite_not_null NOT NULL limite,
	CONSTRAINT orcamento_periodo_referencia_not_null NOT NULL periodo_referencia,
	CONSTRAINT orcamento_pkey PRIMARY KEY (id_orcamento),
	CONSTRAINT orcamento_status_check CHECK (((status)::text = ANY ((ARRAY['normal'::character varying, 'atencao'::character varying, 'risco'::character varying])::text[]))),
	CONSTRAINT orcamento_status_not_null NOT NULL status,
	CONSTRAINT orcamento_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_orcamento_categoria FOREIGN KEY (categoria_id) REFERENCES public.categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT fk_orcamento_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.relatorio_mensal definição

-- Drop table

-- DROP TABLE public.relatorio_mensal;

CREATE TABLE public.relatorio_mensal (
	id_relatorio_mensal serial4 NOT NULL,
	total_gasto numeric(10, 2) NULL,
	total_recebido numeric(10, 2) NULL,
	evolucao_relacao_mes_anterior varchar(250) NULL,
	principais_categorias varchar(250) NULL,
	ranking_categorias varchar(250) NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT relatorio_mensal_id_relatorio_mensal_not_null NOT NULL id_relatorio_mensal,
	CONSTRAINT relatorio_mensal_pkey PRIMARY KEY (id_relatorio_mensal),
	CONSTRAINT relatorio_mensal_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_relatorio_mensal_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.renda definição

-- Drop table

-- DROP TABLE public.renda;

CREATE TABLE public.renda (
	id_renda serial4 NOT NULL,
	salario_liquido numeric(10, 2) NOT NULL,
	receitas_variaveis numeric(10, 2) NULL,
	receitas_recorrentes numeric(10, 2) NULL,
	data_registro_renda date NOT NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT renda_data_registro_renda_not_null NOT NULL data_registro_renda,
	CONSTRAINT renda_id_renda_not_null NOT NULL id_renda,
	CONSTRAINT renda_pkey PRIMARY KEY (id_renda),
	CONSTRAINT renda_salario_liquido_not_null NOT NULL salario_liquido,
	CONSTRAINT renda_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_renda_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.score_financeira definição

-- Drop table

-- DROP TABLE public.score_financeira;

CREATE TABLE public.score_financeira (
	id_score serial4 NOT NULL,
	pontuacao int4 NOT NULL,
	classificacao_faixa varchar(50) NOT NULL,
	usuario_id int4 NOT NULL,
	CONSTRAINT score_financeira_classificacao_faixa_not_null NOT NULL classificacao_faixa,
	CONSTRAINT score_financeira_id_score_not_null NOT NULL id_score,
	CONSTRAINT score_financeira_pkey PRIMARY KEY (id_score),
	CONSTRAINT score_financeira_pontuacao_not_null NOT NULL pontuacao,
	CONSTRAINT score_financeira_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT fk_score_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.transacao definição

-- Drop table

-- DROP TABLE public.transacao;

CREATE TABLE public.transacao (
	id_transacao serial4 NOT NULL,
	descricao varchar(350) NOT NULL,
	valor numeric(10, 2) NOT NULL,
	data_transacao date NOT NULL,
	recorrencia varchar(50) NULL,
	tipo varchar(10) NOT NULL,
	usuario_id int4 NOT NULL,
	categoria_id int4 NOT NULL,
	CONSTRAINT transacao_categoria_id_not_null NOT NULL categoria_id,
	CONSTRAINT transacao_data_transacao_not_null NOT NULL data_transacao,
	CONSTRAINT transacao_descricao_not_null NOT NULL descricao,
	CONSTRAINT transacao_id_transacao_not_null NOT NULL id_transacao,
	CONSTRAINT transacao_pkey PRIMARY KEY (id_transacao),
	CONSTRAINT transacao_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['receita'::character varying, 'despesa'::character varying])::text[]))),
	CONSTRAINT transacao_tipo_not_null NOT NULL tipo,
	CONSTRAINT transacao_usuario_id_not_null NOT NULL usuario_id,
	CONSTRAINT transacao_valor_not_null NOT NULL valor,
	CONSTRAINT fk_transacao_categoria FOREIGN KEY (categoria_id) REFERENCES public.categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT fk_transacao_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.historico_investimento definição

-- Drop table

-- DROP TABLE public.historico_investimento;

CREATE TABLE public.historico_investimento (
	id_historico_investimento serial4 NOT NULL,
	tipo varchar(250) NOT NULL,
	data_registro date NOT NULL,
	transacao_id int4 NOT NULL,
	investimento_id int4 NOT NULL,
	CONSTRAINT historico_investimento_data_registro_not_null NOT NULL data_registro,
	CONSTRAINT historico_investimento_id_historico_investimento_not_null NOT NULL id_historico_investimento,
	CONSTRAINT historico_investimento_investimento_id_not_null NOT NULL investimento_id,
	CONSTRAINT historico_investimento_pkey PRIMARY KEY (id_historico_investimento),
	CONSTRAINT historico_investimento_tipo_not_null NOT NULL tipo,
	CONSTRAINT historico_investimento_transacao_id_not_null NOT NULL transacao_id,
	CONSTRAINT fk_historico_investimento FOREIGN KEY (investimento_id) REFERENCES public.investimento(id_investimento) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_historico_transacao FOREIGN KEY (transacao_id) REFERENCES public.transacao(id_transacao) ON DELETE CASCADE ON UPDATE CASCADE
);


-- public.relatorio_categoria definição

-- Drop table

-- DROP TABLE public.relatorio_categoria;

CREATE TABLE public.relatorio_categoria (
	id_relatorio_categoria serial4 NOT NULL,
	percentual numeric(5, 2) NULL,
	data_registro date NOT NULL,
	valor numeric(10, 2) NULL,
	relatorio_mensal_id int4 NOT NULL,
	CONSTRAINT relatorio_categoria_data_registro_not_null NOT NULL data_registro,
	CONSTRAINT relatorio_categoria_id_relatorio_categoria_not_null NOT NULL id_relatorio_categoria,
	CONSTRAINT relatorio_categoria_pkey PRIMARY KEY (id_relatorio_categoria),
	CONSTRAINT relatorio_categoria_relatorio_mensal_id_not_null NOT NULL relatorio_mensal_id,
	CONSTRAINT fk_relatorio_categoria_mensal FOREIGN KEY (relatorio_mensal_id) REFERENCES public.relatorio_mensal(id_relatorio_mensal) ON DELETE CASCADE ON UPDATE CASCADE
);