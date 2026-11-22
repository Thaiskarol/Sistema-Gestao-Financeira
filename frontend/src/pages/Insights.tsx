
import { useMemo, useState, type ReactNode } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  ArrowRight,
  Bell,
  ChartNoAxesColumn,
  CheckCircle2,
  ChevronRight,
  CircleAlert,
  Lightbulb,
  ShieldCheck,
  Sparkles,
  TrendingUp,
  Wallet,
} from "lucide-react";

import { AppLayout } from "../layouts/AppLayout";
import { getInsights } from "@/services/insights";
import { getScore } from "@/services/saude";
import { getResumo } from "@/services/financeiro";

type Insight = {
  id: number;
  title: string;
  description: string;
  category: string;
  date: string;
  type: "attention" | "success" | "info";
  details: string;
};

function tipoVisual(tipo: string): Insight["type"] {
  const t = (tipo ?? "").toUpperCase();

  if (
    t.includes("ALERTA") ||
    t.includes("AUMENTO") ||
    t.includes("EXCED") ||
    t.includes("ATENC")
  ) {
    return "attention";
  }

  if (t.includes("ECONOMIA") || t.includes("POSITIV")) {
    return "success";
  }

  return "info";
}

const rotuloTipo = {
  attention: "Atenção",
  success: "Boa notícia",
  info: "Informação",
};

const typeStyles = {
  attention: {
    badge: "bg-[#fff4df] text-[#c58a08]",
    icon: "bg-[#fff4df] text-[#bd8a13]",
  },
  success: {
    badge: "bg-[#eaf5ef] text-[#39845b]",
    icon: "bg-[#fcecf0] text-[#bd0c36]",
  },
  info: {
    badge: "bg-[#eaf0f5] text-[#244a64]",
    icon: "bg-[#eaf0f5] text-[#244a64]",
  },
};

export default function Insights() {
  const [expandedInsight, setExpandedInsight] = useState<number | null>(null);

  const insightsQ = useQuery({
    queryKey: ["insights"],
    queryFn: getInsights,
  });

  const scoreQ = useQuery({
    queryKey: ["score"],
    queryFn: getScore,
  });

  const resumoQ = useQuery({
    queryKey: ["resumo"],
    queryFn: getResumo,
  });

  const insights: Insight[] = useMemo(
    () =>
      (insightsQ.data ?? []).map((i, idx) => {
        const type = tipoVisual(i.tipo);

        return {
          id: idx + 1,
          title: i.categoria ?? "Análise financeira",
          description: i.mensagem,
          category: rotuloTipo[type],
          date: "Atualizado agora",
          type,
          details:
            i.percentual != null
              ? `Variação identificada: ${i.percentual}%.`
              : i.mensagem,
        };
      }),
    [insightsQ.data],
  );

  const atencao = insights.filter(
    (i) => i.type === "attention",
  ).length;

  const receitas = resumoQ.data?.totalReceitas ?? 0;

  const taxaEconomia =
    receitas > 0
      ? Math.round(
          ((resumoQ.data?.saldo ?? 0) / receitas) * 100,
        )
      : 0;

  const score = scoreQ.data?.score ?? 0;

  return (
    <AppLayout>
      <div className="mx-auto w-full max-w-[1500px] space-y-6">
        {/* Título da página */}
        <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="mb-1 text-[10px] font-bold uppercase tracking-[0.18em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-[30px]">
              Insights e alertas
            </h1>
            <p className="mt-1 text-sm text-[#777] sm:text-base">
              Análises inteligentes para apoiar suas próximas decisões.
            </p>
          </div>
        </section>

        {/* Indicadores resumidos */}
        <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <SummaryCard
            icon={<Sparkles size={19} />}
            label="Insights disponíveis"
            value={String(insights.length)}
            description="Análises para consultar"
            iconClass="bg-[#fbedf0] text-[#bd0c36]"
          />

          <SummaryCard
            icon={<CircleAlert size={19} />}
            label="Precisam de atenção"
            value={String(atencao)}
            description="Revise seus gastos"
            iconClass="bg-[#fff4df] text-[#c58a08]"
          />

          <SummaryCard
            icon={<TrendingUp size={19} />}
            label="Taxa de economia"
            value={`${taxaEconomia}%`}
            description="Total do histórico"
            iconClass="bg-[#eaf5ef] text-[#39845b]"
          />

          <SummaryCard
            icon={<ShieldCheck size={19} />}
            label="Saúde financeira"
            value={`${score}/100`}
            description="Pontuação"
            iconClass="bg-[#eaf0f5] text-[#244a64]"
          />
        </section>

        {/* Conteúdo principal */}
        <div className="grid min-w-0 grid-cols-1 items-start gap-5 xl:grid-cols-[minmax(0,1.8fr)_minmax(280px,0.9fr)]">
          <section className="min-w-0 space-y-5">
            {/* Insight em destaque */}
            <article className="overflow-hidden rounded-2xl border border-[#dedede] bg-white">
              <div className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:p-6">
                <div className="flex size-12 shrink-0 items-center justify-center rounded-xl bg-[#fbedf0] text-[#bd0c36]">
                  <TrendingUp size={23} />
                </div>

                <div className="min-w-0 flex-1">
                  <div className="mb-2 flex flex-wrap items-center gap-2">
                    <span className="rounded-full bg-[#eaf5ef] px-2.5 py-1 text-[10px] font-semibold text-[#39845b]">
                      Bom resultado
                    </span>
                    <span className="text-[11px] text-[#999]">
                      Total do histórico
                    </span>
                  </div>

                  <h2 className="text-base font-semibold text-[#303030]">
                    Sua taxa de economia está em {taxaEconomia}%
                  </h2>

                  <p className="mt-1 text-sm leading-6 text-[#777]">
                    Sua taxa de economia é calculada com base no saldo e
                    nas receitas registradas.
                  </p>

                  <button
                    type="button"
                    onClick={() =>
                      setExpandedInsight(
                        expandedInsight === 0 ? null : 0,
                      )
                    }
                    className="mt-3 inline-flex items-center gap-1.5 text-xs font-semibold text-[#970b2e] hover:text-[#bd0c36]"
                  >
                    {expandedInsight === 0
                      ? "Ocultar análise"
                      : "Ver análise completa"}
                    <ArrowRight size={15} />
                  </button>

                  {expandedInsight === 0 && (
                    <p className="mt-3 rounded-lg bg-[#faf6f7] p-3 text-sm leading-6 text-[#666]">
                      A taxa de economia representa a proporção do saldo
                      em relação às receitas registradas no histórico.
                    </p>
                  )}
                </div>

                <div className="shrink-0 sm:text-right">
                  <p className="text-3xl font-bold tracking-tight text-[#bd0c36]">
                    {taxaEconomia}%
                  </p>
                  <p className="text-[11px] text-[#888]">
                    Taxa de economia
                  </p>
                </div>
              </div>
            </article>

            {/* Lista de alertas */}
            <section className="space-y-3">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-base font-semibold text-[#303030]">
                    Análises e alertas
                  </h2>
                  <p className="mt-1 text-xs text-[#888]">
                    Acompanhe os pontos importantes das suas finanças.
                  </p>
                </div>

                <span className="rounded-full bg-white px-3 py-1 text-xs text-[#777] ring-1 ring-[#e5e5e5]">
                  {insights.length} análises
                </span>
              </div>

              {insightsQ.isLoading && (
                <p className="rounded-2xl border border-[#dedede] bg-white p-5 text-sm text-[#777]">
                  Carregando insights...
                </p>
              )}

              {insightsQ.isError && (
                <p className="rounded-2xl border border-[#dedede] bg-white p-5 text-sm text-[#bd0c36]">
                  Não foi possível carregar os insights. Tente novamente.
                </p>
              )}

              {!insightsQ.isLoading &&
                !insightsQ.isError &&
                insights.length === 0 && (
                  <p className="rounded-2xl border border-[#dedede] bg-white p-5 text-sm leading-6 text-[#777]">
                    Ainda não há insights. Cadastre transações de pelo
                    menos dois meses.
                  </p>
                )}

              {insights.map((insight) => {
                const isExpanded = expandedInsight === insight.id;
                const styles = typeStyles[insight.type];

                return (
                  <article
                    key={insight.id}
                    className="rounded-2xl border border-[#dedede] bg-white p-4 transition hover:border-[#d5b5be] sm:p-5"
                  >
                    <div className="flex items-start gap-3 sm:gap-4">
                      <div
                        className={`mt-1 flex size-10 shrink-0 items-center justify-center rounded-xl ${styles.icon}`}
                      >
                        {insight.type === "attention" ? (
                          <Lightbulb size={20} />
                        ) : insight.type === "success" ? (
                          <TrendingUp size={20} />
                        ) : (
                          <CheckCircle2 size={20} />
                        )}
                      </div>

                      <div className="min-w-0 flex-1">
                        <span
                          className={`inline-flex rounded-full px-2.5 py-1 text-[10px] font-semibold ${styles.badge}`}
                        >
                          {insight.category}
                        </span>

                        <h3 className="mt-2 text-sm font-semibold text-[#303030] sm:text-[15px]">
                          {insight.title}
                        </h3>

                        <p className="mt-1 text-sm leading-6 text-[#777]">
                          {insight.description}
                        </p>

                        <p className="mt-2 text-[10px] text-[#999]">
                          {insight.date}
                        </p>

                        {isExpanded && (
                          <div className="mt-4 rounded-xl bg-[#fafafa] p-3 sm:p-4">
                            <p className="text-xs font-semibold text-[#444]">
                              Análise detalhada
                            </p>
                            <p className="mt-2 text-sm leading-6 text-[#777]">
                              {insight.details}
                            </p>
                          </div>
                        )}
                      </div>

                      <button
                        type="button"
                        aria-label={
                          isExpanded
                            ? "Ocultar detalhes"
                            : `Ver detalhes: ${insight.title}`
                        }
                        aria-expanded={isExpanded}
                        onClick={() =>
                          setExpandedInsight(
                            isExpanded ? null : insight.id,
                          )
                        }
                        className="mt-2 shrink-0 rounded-lg p-1.5 text-[#999] transition hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                      >
                        {isExpanded ? (
                          <ChevronRight
                            size={19}
                            className="rotate-90"
                          />
                        ) : (
                          <ArrowRight size={19} />
                        )}
                      </button>
                    </div>
                  </article>
                );
              })}
            </section>
          </section>

          {/* Coluna lateral */}
          <aside className="min-w-0 space-y-5">
            {/* Score financeiro */}
            <section className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="flex items-center gap-2">
                <ShieldCheck size={19} className="text-[#970b2e]" />
                <h2 className="text-base font-semibold text-[#303030]">
                  Saúde financeira
                </h2>
              </div>

              <p className="mt-1 text-xs leading-5 text-[#888]">
                Resumo dos seus indicadores financeiros.
              </p>

              <div className="mt-5 flex items-center gap-4 rounded-xl bg-[#fbedf0] p-4">
                <div className="flex size-[62px] shrink-0 items-center justify-center rounded-full border-4 border-[#e8c1cb] bg-white">
                  <span className="text-xl font-bold text-[#970b2e]">
                    {score}
                  </span>
                </div>

                <div>
                  <p className="text-sm font-bold text-[#303030]">
                    {score >= 70 ? "Muito bem!" : "Continue melhorando!"}
                  </p>
                  <p className="mt-1 text-xs leading-5 text-[#777]">
                    Acompanhe seus critérios para melhorar sua pontuação.
                  </p>
                </div>
              </div>

              <div className="mt-4 divide-y divide-[#eeeeee]">
                {(scoreQ.data?.criterios ?? [])
                  .slice(0, 3)
                  .map((c) => (
                    <MetricRow
                      key={c.nome}
                      label={c.nome}
                      value={
                        c.pontos >= 12
                          ? "Bom"
                          : "Pode melhorar"
                      }
                      tone={c.pontos >= 12 ? "green" : "yellow"}
                    />
                  ))}
              </div>

              <a
                href="/saude-financeira"
                className="mt-4 flex items-center justify-center gap-2 rounded-xl border border-[#e5e5e5] px-4 py-2.5 text-sm font-semibold text-[#970b2e] transition hover:bg-[#fbedf0]"
              >
                Ver saúde financeira
                <ArrowRight size={16} />
              </a>
            </section>

            {/* Dados e conexão */}
            <section className="rounded-2xl border border-[#dedede] bg-white p-5 text-center sm:p-6">
              <div className="mx-auto flex size-12 items-center justify-center rounded-xl bg-[#f5f5f5] text-[#777]">
                <ChartNoAxesColumn size={23} />
              </div>

              <h2 className="mt-4 text-sm font-semibold text-[#303030]">
                Amplie suas análises
              </h2>

              <p className="mx-auto mt-2 max-w-xs text-xs leading-5 text-[#888]">
                Quanto mais completos forem seus registros de renda e
                despesas, mais úteis poderão ser os insights.
              </p>

              <a
                href="/transacoes"
                className="mt-4 inline-flex min-h-10 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-[#970b2e]"
              >
                <Wallet size={16} />
                Ver transações
              </a>
            </section>

            {/* Dica */}
            <section className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="flex items-center gap-2">
                <div className="flex size-9 items-center justify-center rounded-lg bg-[#fff4df] text-[#bd8a13]">
                  <Bell size={18} />
                </div>

                <h2 className="text-sm font-semibold text-[#303030]">
                  Dica financeira
                </h2>
              </div>

              <p className="mt-3 text-sm leading-6 text-[#777]">
                Revise seus gastos por categoria regularmente e compare o
                resultado com os limites que você definiu no orçamento.
              </p>

              <a
                href="/orcamento"
                className="mt-3 inline-flex items-center gap-1 text-xs font-semibold text-[#970b2e] hover:text-[#bd0c36]"
              >
                Consultar orçamento
                <ArrowRight size={14} />
              </a>
            </section>
          </aside>
        </div>
      </div>
    </AppLayout>
  );
}

type SummaryCardProps = {
  icon: ReactNode;
  label: string;
  value: string;
  description: string;
  iconClass: string;
};

function SummaryCard({
  icon,
  label,
  value,
  description,
  iconClass,
}: SummaryCardProps) {
  return (
    <article className="rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
      <div className="flex items-start justify-between gap-3">
        <p className="text-sm text-[#777]">{label}</p>
        <div
          className={`flex size-9 shrink-0 items-center justify-center rounded-xl ${iconClass}`}
        >
          {icon}
        </div>
      </div>

      <p className="mt-3 text-2xl font-bold tracking-tight text-[#292929]">
        {value}
      </p>

      <p className="mt-1 text-xs text-[#888]">{description}</p>
    </article>
  );
}

type MetricRowProps = {
  label: string;
  value: string;
  tone: "green" | "yellow";
};

function MetricRow({ label, value, tone }: MetricRowProps) {
  const toneClass =
    tone === "green"
      ? "bg-[#eaf5ef] text-[#39845b]"
      : "bg-[#fff4df] text-[#bd8a13]";

  return (
    <div className="flex items-center justify-between gap-3 py-3">
      <span className="text-xs text-[#666]">{label}</span>
      <span
        className={`shrink-0 rounded-full px-2.5 py-1 text-[10px] font-semibold ${toneClass}`}
      >
        {value}
      </span>
    </div>
  );
}