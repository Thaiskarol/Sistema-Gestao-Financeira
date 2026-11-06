import { useState, type ReactNode } from "react";
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

type Insight = {
  id: number;
  title: string;
  description: string;
  category: string;
  date: string;
  type: "attention" | "success" | "info";
  details: string;
};

const insights: Insight[] = [
  {
    id: 1,
    title: "Alimentação acima da média",
    description:
      "Seus gastos com delivery cresceram 28% nesta semana.",
    category: "Atenção",
    date: "Hoje · Atualizado automaticamente",
    type: "attention",
    details:
      "Compare seus gastos com alimentação nas últimas semanas e confira se o aumento veio de delivery, restaurantes ou supermercado. Definir um limite para essa categoria pode ajudar no controle.",
  },
  {
    id: 2,
    title: "Meta de reserva no caminho certo",
    description:
      "Mantendo o ritmo, você poderá alcançar sua reserva antes do prazo estimado.",
    category: "Boa notícia",
    date: "Hoje · Com base no seu histórico",
    type: "success",
    details:
      "Seus aportes recentes estão contribuindo para o progresso da meta. Continue acompanhando os depósitos e revise o valor planejado quando sua renda ou suas despesas mudarem.",
  },
  {
    id: 3,
    title: "Orçamento de transporte sob controle",
    description:
      "Seus gastos estão dentro do limite definido para o mês.",
    category: "Informação",
    date: "Atualizado este mês",
    type: "info",
    details:
      "O acompanhamento periódico ajuda a perceber mudanças antes que o limite seja ultrapassado. Continue registrando os gastos dessa categoria.",
  },
];

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
  const [period, setPeriod] = useState("Este mês");
  const [expandedInsight, setExpandedInsight] = useState<number | null>(null);

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

          <label className="flex items-center gap-2 self-start sm:self-auto">
            <span className="text-xs text-[#777] sm:hidden">Período:</span>
            <select
              value={period}
              onChange={(event) => setPeriod(event.target.value)}
              className="min-h-10 rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#555] outline-none transition focus:border-[#bd0c36] focus:ring-2 focus:ring-[#bd0c36]/10"
              aria-label="Selecionar período dos insights"
            >
              <option>Esta semana</option>
              <option>Este mês</option>
              <option>Últimos 3 meses</option>
              <option>Últimos 6 meses</option>
            </select>
          </label>
        </section>

        {/* Indicadores resumidos */}
        <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <SummaryCard
            icon={<Sparkles size={19} />}
            label="Insights disponíveis"
            value="08"
            description="Análises para consultar"
            iconClass="bg-[#fbedf0] text-[#bd0c36]"
          />
          <SummaryCard
            icon={<CircleAlert size={19} />}
            label="Precisam de atenção"
            value="02"
            description="Revise seus gastos"
            iconClass="bg-[#fff4df] text-[#c58a08]"
          />
          <SummaryCard
            icon={<TrendingUp size={19} />}
            label="Taxa de economia"
            value="41%"
            description="No período selecionado"
            iconClass="bg-[#eaf5ef] text-[#39845b]"
          />
          <SummaryCard
            icon={<ShieldCheck size={19} />}
            label="Saúde financeira"
            value="82/100"
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
                      {period}
                    </span>
                  </div>

                  <h2 className="text-base font-semibold text-[#303030]">
                    Sua taxa de economia está evoluindo
                  </h2>
                  <p className="mt-1 text-sm leading-6 text-[#777]">
                    Sua taxa de economia chegou a 41%, segundo os dados deste painel.
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
                      A taxa de economia é calculada comparando o valor
                      economizado com a renda do mesmo período. Para uma
                      análise real, o sistema precisará dos registros de
                      renda e despesas.
                    </p>
                  )}
                </div>

                <div className="shrink-0 sm:text-right">
                  <p className="text-3xl font-bold tracking-tight text-[#bd0c36]">
                    41%
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
                    82
                  </span>
                </div>
                <div>
                  <p className="text-sm font-bold text-[#303030]">
                    Muito bem!
                  </p>
                  <p className="mt-1 text-xs leading-5 text-[#777]">
                    Sua pontuação está boa, continue assim!
                  </p>
                </div>
              </div>

              <div className="mt-4 divide-y divide-[#eeeeee]">
                <MetricRow
                  label="Controle de gastos"
                  value="Excelente"
                  tone="green"
                />
                <MetricRow
                  label="Consistência"
                  value="Boa"
                  tone="green"
                />
                <MetricRow
                  label="Diversificação"
                  value="Pode melhorar"
                  tone="yellow"
                />
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

            <div className="flex items-start gap-2 px-1 text-[11px] leading-5 text-[#999]">
              
            </div>
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