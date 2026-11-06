import { useState } from "react";
import {
  ArrowDownLeft,
  ArrowRight,
  Check,
  Lightbulb,
  Wallet,
} from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";

const fontesDeRenda = [
  { nome: "Salário", valor: 7850, percentual: 88 },
  { nome: "Freelance", valor: 1000, percentual: 11 },
  { nome: "Rendimentos", valor: 126.42, percentual: 1 },
];

const formatarMoeda = (valor: number) =>
  valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

export default function Renda() {
  const [periodo, setPeriodo] = useState("Junho de 2025");
  const [sugestaoAplicada, setSugestaoAplicada] = useState(false);

  const rendaTotal = fontesDeRenda.reduce(
    (total, fonte) => total + fonte.valor,
    0,
  );

  const distribuicao = [
    {
      nome: "Necessidades",
      percentual: 50,
      valor: rendaTotal * 0.5,
      descricao: "Moradia, alimentação e contas",
      cor: "bg-[#193f59]",
      marcador: "bg-[#193f59]",
    },
    {
      nome: "Desejos",
      percentual: 30,
      valor: rendaTotal * 0.3,
      descricao: "Lazer, compras e experiências",
      cor: "bg-[#bd0c36]",
      marcador: "bg-[#bd0c36]",
    },
    {
      nome: "Poupança / investimentos",
      percentual: 20,
      valor: rendaTotal * 0.2,
      descricao: "Construção de patrimônio",
      cor: "bg-[#32885e]",
      marcador: "bg-[#32885e]",
    },
  ];

  return (
    <AppLayout>
      <div className="mx-auto w-full max-w-[1500px] space-y-5">
        {/* Título e período */}
        <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="mb-1 text-[10px] font-bold uppercase tracking-[0.18em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-[30px]">
              Renda
            </h1>
            <p className="mt-1 text-sm text-[#777] sm:text-base">
              Veja de onde vem sua renda e como distribuí-la melhor.
            </p>
          </div>

          <label className="flex items-center gap-2">
            <span className="text-xs text-[#777] sm:hidden">Período:</span>
            <select
              value={periodo}
              onChange={(event) => setPeriodo(event.target.value)}
              aria-label="Período da renda"
              className="min-h-10 rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#555] outline-none focus:border-[#bd0c36] focus:ring-2 focus:ring-[#bd0c36]/10"
            >
              <option>Junho de 2025</option>
              <option>Maio de 2025</option>
              <option>Abril de 2025</option>
              <option>Março de 2025</option>
            </select>
          </label>
        </section>

        {/* Resumo e fontes */}
        <section className="grid min-w-0 grid-cols-1 gap-4 lg:grid-cols-[minmax(0,0.9fr)_minmax(0,1.45fr)]">
          <article className="relative min-w-0 overflow-hidden rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
            <div className="absolute -bottom-8 -right-5 size-24 rounded-full bg-[#d3dbe0]" />

            <div className="relative flex items-start justify-between gap-3">
              <p className="text-sm font-medium text-[#777]">
                Renda total mensal
              </p>
              <span className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-[#eaf0f5] text-[#193f59]">
                <ArrowDownLeft size={19} />
              </span>
            </div>

            <p className="relative mt-2 text-2xl font-bold tracking-tight text-[#292929] sm:text-[27px]">
              {formatarMoeda(rendaTotal)}
            </p>
            <p className="relative mt-2 text-xs text-[#888]">
              Valores demonstrativos de {periodo.toLowerCase()}
            </p>
          </article>

          <article className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
            <div className="flex items-center gap-2">
              <Wallet size={19} className="text-[#193f59]" />
              <h2 className="text-base font-semibold text-[#303030]">
                Fontes de renda
              </h2>
            </div>

            <div className="mt-4">
              {fontesDeRenda.map((fonte) => (
                <div
                  key={fonte.nome}
                  className="grid grid-cols-[minmax(0,1fr)_auto_36px] items-center gap-3 border-t border-[#dedede] py-3"
                >
                  <span className="min-w-0 text-sm text-[#555]">
                    {fonte.nome}
                  </span>
                  <span className="text-right text-xs font-semibold text-[#333] sm:text-sm">
                    {formatarMoeda(fonte.valor)}
                  </span>
                  <span className="text-right text-[10px] text-[#888]">
                    {fonte.percentual}%
                  </span>
                </div>
              ))}
            </div>
          </article>
        </section>

        {/* Distribuição sugerida */}
        <section className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
          <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
            <h2 className="text-base font-semibold text-[#303030]">
              Distribuição sugerida
            </h2>
            <span className="w-fit rounded-full bg-[#eaf0f5] px-2.5 py-1 text-[10px] font-semibold text-[#193f59]">
              Sugestão personalizada
            </span>
          </div>

          <div
            className="mt-5 flex h-3 w-full gap-[3px] overflow-hidden rounded-full bg-[#eeeeee]"
            role="img"
            aria-label="Distribuição sugerida: 50% para necessidades, 30% para desejos e 20% para poupança e investimentos"
          >
            {distribuicao.map((item) => (
              <div
                key={item.nome}
                className={item.cor}
                style={{ width: `${item.percentual}%` }}
              />
            ))}
          </div>

          <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-3 sm:gap-5">
            {distribuicao.map((item) => (
              <div key={item.nome} className="min-w-0">
                <div className="flex items-start gap-2">
                  <span
                    className={`mt-1 size-2 shrink-0 rounded-sm ${item.marcador}`}
                  />
                  <p className="text-xs leading-5 text-[#777]">
                    {item.nome} · {item.percentual}%
                  </p>
                </div>
                <p className="mt-1 pl-4 text-base font-bold text-[#333]">
                  {formatarMoeda(item.valor)}
                </p>
                <p className="mt-1 pl-4 text-[10px] leading-4 text-[#999]">
                  {item.descricao}
                </p>
              </div>
            ))}
          </div>

          <p className="mt-4 border-t border-[#eeeeee] pt-3 text-[11px] leading-5 text-[#999]">
            A divisão 50/30/20 é uma referência de planejamento, não uma regra
            obrigatória. Ajuste-a conforme suas necessidades e seus objetivos.
          </p>
        </section>

        {/* Recomendação */}
        <section className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
            <div className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-[#fbedf0] text-[#bd0c36]">
              <Lightbulb size={23} />
            </div>

            <div className="min-w-0 flex-1">
              <span className="inline-flex rounded-full bg-[#eaf5ef] px-2.5 py-1 text-[10px] font-semibold text-[#39845b]">
                Recomendação
              </span>
              <h2 className="mt-2 text-sm font-semibold text-[#303030] sm:text-[15px]">
                Direcione o aumento da renda para seus objetivos
              </h2>
              <p className="mt-1 text-sm leading-6 text-[#777]">
                Organize suas receitas, acompanhe sua evolução e avalie quanto
                pode destinar às suas metas e investimentos.
              </p>
              {sugestaoAplicada && (
                <p className="mt-2 flex items-center gap-1.5 text-xs font-medium text-[#39845b]">
                  <Check size={15} />
                  Sugestão marcada para acompanhamento.
                </p>
              )}
            </div>

            <button
              type="button"
              onClick={() => setSugestaoAplicada((atual) => !atual)}
              className="inline-flex min-h-10 shrink-0 items-center justify-center gap-2 rounded-xl bg-[#fbedf0] px-4 py-2.5 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f7dfe5]"
            >
              {sugestaoAplicada ? "Desmarcar sugestão" : "Aplicar sugestão"}
              {sugestaoAplicada ? (
                <Check size={16} />
              ) : (
                <ArrowRight size={16} />
              )}
            </button>
          </div>
        </section>

      </div>
    </AppLayout>
  );
}