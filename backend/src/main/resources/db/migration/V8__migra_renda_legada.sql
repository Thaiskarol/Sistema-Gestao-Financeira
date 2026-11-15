DO $$
BEGIN
    -- Verifica se existe uma tabela antiga preservada.
    IF to_regclass('public.renda_legado') IS NOT NULL THEN

        -- Transfere as tres fontes de receita para a nova estrutura.
        INSERT INTO public.renda (
            descricao,
            valor,
            tipo,
            usuario_id
        )
        SELECT
            receita.descricao,
            receita.valor,
            receita.tipo,
            legado.usuario_id
        FROM public.renda_legado AS legado
        CROSS JOIN LATERAL (
            VALUES
                (
                    'Salario liquido'::VARCHAR(150),
                    legado.salario_liquido,
                    'SALARIO_LIQUIDO'::VARCHAR(20)
                ),
                (
                    'Receita variavel'::VARCHAR(150),
                    legado.receitas_variaveis,
                    'VARIAVEL'::VARCHAR(20)
                ),
                (
                    'Receita recorrente'::VARCHAR(150),
                    legado.receitas_recorrentes,
                    'RECORRENTE'::VARCHAR(20)
                )
        ) AS receita(descricao, valor, tipo)
        WHERE receita.valor IS NOT NULL
          AND receita.valor <> 0;

    END IF;
END $$;
