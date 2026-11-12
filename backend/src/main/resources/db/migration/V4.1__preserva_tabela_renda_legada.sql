DO $$
BEGIN
    -- Identifica a estrutura antiga da tabela renda.
    IF to_regclass('public.renda') IS NOT NULL
       AND EXISTS (
           SELECT 1
           FROM information_schema.columns
           WHERE table_schema = 'public'
             AND table_name = 'renda'
             AND column_name = 'salario_liquido'
       )
    THEN
        -- Preserva a tabela antiga.
        ALTER TABLE public.renda
        RENAME TO renda_legado;

        -- Preserva a sequencia antiga.
        IF to_regclass('public.renda_id_renda_seq') IS NOT NULL THEN
            ALTER SEQUENCE public.renda_id_renda_seq
            RENAME TO renda_legado_id_renda_seq;
        END IF;

        -- Preserva o indice da chave primaria.
        IF to_regclass('public.renda_pkey') IS NOT NULL THEN
            ALTER INDEX public.renda_pkey
            RENAME TO renda_legado_pkey;
        END IF;
    END IF;
END $$;
