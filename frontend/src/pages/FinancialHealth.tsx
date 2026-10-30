import { useMemo } from "react";
import {
  HeartPulse,
  TrendingUp,
  PiggyBank,
  ShieldCheck,
  Wallet,
  Target,
  ArrowUpRight,
  CheckCircle2,
  CircleDollarSign,
} from "lucide-react";
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";
import { AppLayout } from "../layouts/AppLayout";

const historicoScore = [
  { mes: "Dez", score: 38 },
  { mes: "Jan", score: 52 },
  { mes: "Fev", score: 66 },
  { mes: "Mar", score: 78 },
  { mes: "Abr", score: 86 },
  { mes: "Mai", score: 96 },
  { mes: "Jun", score: 100 },
];

const indicadores = [
  {
    titulo: "Percentual de economia",
    descricao: "Excelente",
    valor: 92,
    cor: "#32885e",
  },
  {
    titulo: "Frequência de estouro",
    descricao: "Muito bom",
    valor: 87,
    cor: "#32885e",
  },
  {
    titulo: "Evolução patrimonial",
    descricao: "Bom",
    valor: 78,
    cor: "#32885e",
  },
  {
    titulo: "Consistência de investimentos",
    descricao: "Pode melhorar",
    valor: 71,
    cor: "#e5a900",
  },
  {
    titulo: "Controle de gastos",
    descricao: "Muito bom",
    valor: 84,
    cor: "#32885e",
  },
];

const recomendacoes = [
  {
    titulo: "Complete sua reserva",
    descricao: "Finalize os 13% restantes da reserva.",
    pontos: "+4 pontos",
    icone: ShieldCheck,
  },
  {
    titulo: "Diversifique investimentos",
    descricao: "Reduza a concentração em renda fixa.",
    pontos: "+3 pontos",
    icone: TrendingUp,
  },
  {
    titulo: "Mantenha o orçamento",
    descricao: "Complete 3 meses dentro do planejado.",
    pontos: "+2 pontos",
    icone: Wallet,
  },
];

function ScoreCircular({ score }: { score: number }) {
  const circunferencia = 2 * Math.PI * 48;
  const progresso = (Math.min(Math.max(score, 0), 100) / 100) * circunferencia;

  return (
    <div className="relative mx-auto flex size-36 items-center justify-center">
      <svg
        viewBox="0 0 112 112"
        className="absolute inset-0 size-full -rotate-90"
        aria-hidden="true"
      >
        <circle
          cx="56"
          cy="56"
          r="48"
          fill="none"
          stroke="#e8eeeb"
          strokeWidth="7"
        />
        <circle
          cx="56"
          cy="56"
          r="48"
          fill="none"
          stroke="#32885e"
          strokeWidth="7"
          strokeLinecap="round"
          strokeDasharray={`${progresso} ${circunferencia}`}
          className="transition-all duration-700"
        />
      </svg>

      <div className="text-center">
        <p className="text-4xl font-bold leading-none text-[#303030]">
          {score}
        </p>
        <p className="mt-2 text-[10px] text-[#888]">de 100</p>
      </div>
    </div>
  );
}

export default function FinancialHealth() {
  const score = 82;

  const classificacao = useMemo(() => {
    if (score >= 90) return "Excelente";
    if (score >= 75) return "Muito boa";
    if (score >= 60) return "Boa";
    if (score >= 40) return "Regular";
    return "Precisa melhorar";
  }, [score]);

  return (
    <AppLayout>
      <main className="w-full min-w-0 bg-[#f5f5f5]">
         <div className="w-full min-w-0 pb-8 pt-1">
          {/* Cabeçalho */}
          <header className="mb-7">
            <p className="text-[10px] font-bold uppercase tracking-[0.17em] text-[#8c092e]">
              Visão financeira
            </p>
            <h1 className="mt-1 text-2xl font-bold tracking-tight text-[#292929] sm:text-3xl">
              Saúde financeira
            </h1>
            <p className="mt-1 text-sm text-[#777]">
              Um retrato completo dos seus hábitos e decisões.
            </p>
          </header>

          {/* Score e indicadores */}
          <section className="grid grid-cols-1 gap-4 lg:grid-cols-[minmax(260px,0.9fr)_minmax(0,1.8fr)]">
            {/* Cartão do score */}
            <article className="flex min-w-0 flex-col items-center rounded-2xl border border-[#dedede] bg-white px-5 py-6 sm:px-7">
              <div className="mb-5 flex w-full items-center gap-2 self-start">
                <span className="flex size-9 items-center justify-center rounded-xl bg-[#f8e9ed] text-[#a30a32]">
                  <HeartPulse size={19} />
                </span>
                <div>
                  <h2 className="text-sm font-semibold text-[#333]">
                    Seu score financeiro
                  </h2>
                  <p className="text-xs text-[#888]">Junho de 2025</p>
                </div>
              </div>

              <ScoreCircular score={score} />

              <span className="mt-4 rounded-full bg-[#e8f3ed] px-3 py-1 text-[11px] font-semibold text-[#32885e]">
                Saúde {classificacao.toLowerCase()}
              </span>

              <p className="mt-2 max-w-xs text-center text-xs leading-5 text-[#888]">
                Você está entre os perfis com hábitos financeiros organizados.
              </p>

              <div className="mt-6 grid w-full grid-cols-2 gap-3 border-t border-[#eee] pt-5">
                <div>
                  <p className="text-xs text-[#888]">Pontuação</p>
                  <p className="mt-1 text-lg font-bold text-[#333]">
                    {score}/100
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-xs text-[#888]">Tendência</p>
                  <p className="mt-1 inline-flex items-center gap-1 text-sm font-semibold text-[#32885e]">
                    <ArrowUpRight size={16} />
                    Em evolução
                  </p>
                </div>
              </div>
            </article>

            {/* Indicadores */}
            <article className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="mb-5 flex items-center gap-2">
                <span className="flex size-9 items-center justify-center rounded-xl bg-[#e8f3ed] text-[#32885e]">
                  <Target size={19} />
                </span>
                <div>
                  <h2 className="text-sm font-semibold text-[#333]">
                    Indicadores de saúde
                  </h2>
                  <p className="text-xs text-[#888]">
                    Desempenho por categoria
                  </p>
                </div>
              </div>

              <div className="space-y-5">
                {indicadores.map((item) => (
                  <div key={item.titulo}>
                    <div className="flex flex-wrap items-start justify-between gap-x-3 gap-y-1">
                      <div className="min-w-0">
                        <p className="text-xs font-medium text-[#444]">
                          {item.titulo}
                        </p>
                        <p className="mt-1 text-[10px] text-[#999]">
                          {item.descricao}
                        </p>
                      </div>

                      <div className="flex shrink-0 items-center gap-2">
                        <span className="text-[11px] font-semibold text-[#444]">
                          {item.valor}/100
                        </span>
                        <div className="h-1.5 w-14 overflow-hidden rounded-full bg-[#edf0ef] sm:w-16">
                          <div
                            className="h-full rounded-full"
                            style={{
                              width: `${item.valor}%`,
                              backgroundColor: item.cor,
                            }}
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </article>
          </section>

          {/* Histórico e recomendações */}
          <section className="mt-4 grid grid-cols-1 gap-4 xl:grid-cols-[minmax(0,1.6fr)_minmax(300px,1fr)]">
            {/* Histórico */}
            <article className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="mb-5 flex items-center gap-2">
                <span className="flex size-9 items-center justify-center rounded-xl bg-[#edf3f6] text-[#193f59]">
                  <TrendingUp size={19} />
                </span>
                <div>
                  <h2 className="text-base font-semibold text-[#333]">
                    Histórico do score
                  </h2>
                  <p className="text-xs text-[#888]">
                    Evolução ao longo dos meses
                  </p>
                </div>
              </div>

              <div className="h-[230px] w-full sm:h-[255px]">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart
                    data={historicoScore}
                    margin={{ top: 8, right: 8, left: -12, bottom: 0 }}
                  >
                    <CartesianGrid
                      stroke="#e5e5e5"
                      vertical={false}
                      strokeDasharray="0"
                    />
                    <XAxis
                      dataKey="mes"
                      tick={{ fill: "#999", fontSize: 11 }}
                      axisLine={false}
                      tickLine={false}
                      tickMargin={12}
                    />
                    <YAxis
                      domain={[0, 100]}
                      ticks={[0, 25, 50, 75, 100]}
                      tick={{ fill: "#999", fontSize: 10 }}
                      axisLine={false}
                      tickLine={false}
                    />
                    <Tooltip
                      formatter={(value) => [`${value}/100`, "Score"]}
                      contentStyle={{
                        borderRadius: 12,
                        border: "1px solid #e5e5e5",
                        fontSize: 12,
                      }}
                    />
                    <Line
                      type="monotone"
                      dataKey="score"
                      stroke="#193f59"
                      strokeWidth={2.5}
                      dot={{
                        r: 3.5,
                        fill: "#fff",
                        stroke: "#193f59",
                        strokeWidth: 2,
                      }}
                      activeDot={{ r: 5 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </article>

            {/* Recomendações */}
            <article className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="mb-4 flex items-center gap-2">
                <span className="flex size-9 items-center justify-center rounded-xl bg-[#f8e9ed] text-[#a30a32]">
                  <CircleDollarSign size={19} />
                </span>
                <div>
                  <h2 className="text-base font-semibold text-[#333]">
                    Como melhorar
                  </h2>
                  <p className="text-xs text-[#888]">
                    Ações para fortalecer suas finanças
                  </p>
                </div>
              </div>

              <div className="divide-y divide-[#e5e5e5]">
                {recomendacoes.map((item, index) => {
                  const Icone = item.icone;

                  return (
                    <div
                      key={item.titulo}
                      className="flex items-start gap-3 py-4 first:pt-2 last:pb-1"
                    >
                      <span className="flex size-7 shrink-0 items-center justify-center rounded-lg bg-[#f5f5f5] text-[11px] font-semibold text-[#555]">
                        {index + 1}
                      </span>

                      <div className="min-w-0 flex-1">
                        <div className="flex items-start gap-2">
                          <Icone
                            size={15}
                            className="mt-0.5 shrink-0 text-[#193f59]"
                          />
                          <h3 className="text-xs font-semibold text-[#333]">
                            {item.titulo}
                          </h3>
                        </div>
                        <p className="mt-1 text-[10px] leading-4 text-[#888]">
                          {item.descricao}
                        </p>
                      </div>

                      <span className="shrink-0 rounded-lg bg-[#f5f5f5] px-2 py-1.5 text-[10px] font-semibold text-[#193f59]">
                        {item.pontos}
                      </span>
                    </div>
                  );
                })}
              </div>

              <div className="mt-5 rounded-xl bg-[#f5f8f6] p-3">
                <div className="flex items-start gap-2">
                  <CheckCircle2
                    size={17}
                    className="mt-0.5 shrink-0 text-[#32885e]"
                  />
                  <p className="text-xs leading-5 text-[#666]">
                    Pequenas ações consistentes podem ajudar você a alcançar
                    seus objetivos financeiros.
                  </p>
                </div>
              </div>
            </article>
          </section>

          {/* Mensagem final */}
          <section className="mt-4 flex flex-col gap-3 rounded-2xl border border-[#dedede] bg-white p-5 sm:flex-row sm:items-center sm:justify-between sm:p-6">
            <div className="flex items-center gap-3">
              <span className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-[#edf3f6] text-[#193f59]">
                <PiggyBank size={22} />
              </span>
              <div>
                <h2 className="text-sm font-semibold text-[#333]">
                  Continue acompanhando sua saúde financeira
                </h2>
                <p className="mt-1 text-xs leading-5 text-[#888]">
                  Revise seus gastos, acompanhe suas metas e mantenha seu
                  planejamento atualizado.
                </p>
              </div>
            </div>
            <HeartPulse
              size={23}
              className="hidden shrink-0 text-[#8c092e] sm:block"
            />
          </section>
        </div>
      </main>
    </AppLayout>
  );
}