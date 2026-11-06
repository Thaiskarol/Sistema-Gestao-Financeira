import {
  ArrowDownLeft,
  ArrowRight,
  ArrowUpRight,
  Bell,
  Check,
  Lightbulb,
  Plus,
  Tag,
  Wallet,
} from "lucide-react";
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { AppLayout } from "../layouts/AppLayout";
import { useNavigate } from "react-router-dom";

const evolution = [
  { month: "Mai", receitas: 5200, despesas: 3200, saldo: 2000 },
  { month: "Jun", receitas: 6100, despesas: 3800, saldo: 2300 },
  { month: "Jul", receitas: 5900, despesas: 3600, saldo: 2300 },
  { month: "Ago", receitas: 7200, despesas: 4100, saldo: 3100 },
  { month: "Set", receitas: 7800, despesas: 4300, saldo: 3500 },
  { month: "Out", receitas: 8500, despesas: 3650, saldo: 4850 },
];

const categories = [
  { name: "Moradia", value: 1820, percent: 34, color: "#23445d" },
  { name: "Alimentação", value: 1210, percent: 23, color: "#368661" },
  { name: "Transporte", value: 780, percent: 15, color: "#e5ad20" },
  { name: "Lazer", value: 520, percent: 10, color: "#a30f35" },
];

const transactions = [
  { name: "Supermercado Verde", category: "Alimentação", date: "Hoje, 09:42", value: -284.6 },
  { name: "Salário mensal", category: "Renda", date: "05 out, 08:15", value: 7850 },
  { name: "Netflix", category: "Assinaturas", date: "04 out, 20:00", value: -39.9 },
  { name: "Posto Avenida", category: "Transporte", date: "03 out, 18:22", value: -220 },
];

const money = (value: number) =>
  value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

function SectionTitle({
  title,
  action,
}: {
  title: string;
  action?: string;
}) {
  return (
    <div className="mb-5 flex items-center justify-between gap-3">
      <h2 className="text-base font-semibold tracking-tight text-[#292929]">
        {title}
      </h2>
      {action && (
        <button className="shrink-0 text-xs font-semibold text-[#a30f35] hover:underline">
          {action}
        </button>
      )}
    </div>
  );
}

function SummaryCard({
  title,
  value,
  caption,
  icon: Icon,
  tone,
}: {
  title: string;
  value: string;
  caption: string;
  icon: typeof Wallet;
  tone: "blue" | "green" | "red";
}) {
  const tones = {
    blue: "bg-slate-100 text-[#23445d]",
    green: "bg-emerald-50 text-emerald-700",
    red: "bg-rose-50 text-[#b20d38]",
  };

  return (
    <article className="relative min-w-0 overflow-hidden rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
      <div className="mb-3 flex items-center justify-between gap-2">
        <p className="text-sm text-[#747474]">{title}</p>
        <span className={`flex size-8 shrink-0 items-center justify-center rounded-lg ${tones[tone]}`}>
          <Icon size={17} />
        </span>
      </div>
      <p className="text-xl font-bold tracking-tight text-[#292929] sm:text-2xl">
        {value}
      </p>
      <p className="mt-2 text-xs text-[#858585]">{caption}</p>
      <span
        aria-hidden="true"
        className={`absolute -bottom-7 -right-3 size-16 rounded-tl-full opacity-20 ${
          tone === "red" ? "bg-rose-600" : "bg-slate-500"
        }`}
      />
    </article>
  );
}

export default function Dashboard() {
  const navigate = useNavigate();
  return (
    <AppLayout>
      <div className="mx-auto w-full max-w-[1500px] space-y-5 sm:space-y-6">
        <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="mb-1 text-[11px] font-bold uppercase tracking-[0.16em] text-[#a30f35]">
              Visão financeira
            </p>
            <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-3xl">
              Olá, Ana!
            </h1>
            <p className="mt-1 text-sm text-[#777]">
              Aqui está o resumo das suas finanças este mês.
            </p>
          </div>
            <button onClick={() => navigate("/nova-transacao")} className="inline-flex min-h-11 items-center justify-center gap-2 self-start rounded-xl bg-[#bd0c36] px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a20b2e] sm:self-auto">
            <Plus size={18} />
            Nova transação
          </button>
        </section> 

        <section
          aria-label="Resumo financeiro"
          className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-3"
        >
          <SummaryCard
            title="Saldo disponível"
            value="R$ 4.850,00"
            caption="+8,4% desde o mês passado"
            icon={Wallet}
            tone="blue"
          />
          <SummaryCard
            title="Total de receitas"
            value="R$ 8.500,00"
            caption="+12,1% este mês"
            icon={ArrowDownLeft}
            tone="green"
          />
          <SummaryCard
            title="Total de despesas"
            value="R$ 3.650,00"
            caption="43% do orçamento"
            icon={ArrowUpRight}
            tone="red"
          />
        </section>

        <section className="grid grid-cols-1 items-stretch gap-4 xl:grid-cols-[minmax(0,2fr)_minmax(260px,0.9fr)]">
          <article className="min-w-0 rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <SectionTitle title="Evolução financeira" />
              <div className="flex gap-1 rounded-lg bg-[#f5f5f5] p-1">
                <button className="rounded-md px-2 py-1.5 text-xs text-[#777] sm:px-3">
                  3 meses
                </button>
                <button className="rounded-md bg-white px-2 py-1.5 text-xs font-semibold text-[#a30f35] shadow-sm sm:px-3">
                  6 meses
                </button>
                <button className="rounded-md px-2 py-1.5 text-xs text-[#777] sm:px-3">
                  1 ano
                </button>
              </div>
            </div>

            <div className="mb-3 flex flex-wrap gap-4 text-[11px] text-[#777]">
              <span className="flex items-center gap-1.5">
                <span className="size-2 rounded-full bg-[#23445d]" /> Receitas
              </span>
              <span className="flex items-center gap-1.5">
                <span className="size-2 rounded-full bg-[#a30f35]" /> Despesas
              </span>
              <span className="flex items-center gap-1.5">
                <span className="size-2 rounded-full bg-[#368661]" /> Saldo
              </span>
            </div>

            <div className="h-[230px] w-full sm:h-[270px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={evolution} margin={{ top: 8, right: 5, left: -18, bottom: 0 }}>
                  <CartesianGrid stroke="#e9e9e9" vertical={false} />
                  <XAxis dataKey="month" tickLine={false} axisLine={false} tick={{ fill: "#888", fontSize: 11 }} />
                  <YAxis
                    tickLine={false}
                    axisLine={false}
                    tick={{ fill: "#888", fontSize: 10 }}
                    tickFormatter={(value) => `${value / 1000}k`}
                  />
                  <Tooltip formatter={(value) => money(Number(value))} />
                  <Line type="monotone" dataKey="receitas" name="Receitas" stroke="#23445d" strokeWidth={2.5} dot={{ r: 3, fill: "#fff", strokeWidth: 2 }} />
                  <Line type="monotone" dataKey="despesas" name="Despesas" stroke="#a30f35" strokeWidth={2.5} dot={false} />
                  <Line type="monotone" dataKey="saldo" name="Saldo" stroke="#368661" strokeWidth={2.5} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </article>

          <article className="rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
            <SectionTitle title="Orçamento mensal" action="Ver detalhes" />
            <div className="flex flex-col items-center py-2">
              <div
                className="relative flex size-40 items-center justify-center rounded-full"
                style={{
                  background: "conic-gradient(#bd0c36 0% 62%, #f1f1f1 62% 100%)",
                }}
              >
                <div className="flex size-[122px] flex-col items-center justify-center rounded-full bg-white">
                  <span className="text-3xl font-bold text-[#292929]">62%</span>
                  <span className="text-[11px] text-[#888]">utilizado</span>
                </div>
              </div>
              <div className="mt-6 w-full space-y-3">
                <div className="flex justify-between gap-3 text-sm">
                  <span className="text-[#777]">Gasto até agora</span>
                  <strong>{money(3650)}</strong>
                </div>
                <div className="flex justify-between gap-3 text-sm">
                  <span className="text-[#777]">Orçamento total</span>
                  <strong>{money(5900)}</strong>
                </div>
                <div className="flex items-center gap-2 rounded-lg bg-emerald-50 px-3 py-2.5 text-xs font-semibold text-emerald-700">
                  <Check size={15} />
                  Dentro do planejado
                </div>
              </div>
            </div>
          </article>
        </section>

        <section className="grid grid-cols-1 items-stretch gap-4 lg:grid-cols-[minmax(0,1.35fr)_minmax(0,0.8fr)]">
          <article className="min-w-0 rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
            <SectionTitle title="Despesas por categoria" />
            <div className="space-y-1">
              {categories.map((category) => (
                <div
                  key={category.name}
                  className="flex items-center gap-3 border-b border-[#eeeeee] py-3 last:border-b-0"
                >
                  <span className="size-2 shrink-0 rounded-sm" style={{ backgroundColor: category.color }} />
                  <span className="min-w-0 flex-1 truncate text-xs font-medium text-[#4d4d4d]">
                    {category.name}
                  </span>
                  <span className="text-xs font-semibold text-[#444]">
                    {money(category.value)}
                  </span>
                  <span className="w-9 text-right text-xs text-[#888]">
                    {category.percent}%
                  </span>
                </div>
              ))}
            </div>
          </article>

          <article className="rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
            <SectionTitle title="Insights & alertas" action="Ver todos" />
            <div className="flex gap-3 rounded-xl bg-[#fbedf0] p-3">
              <span className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-white text-[#bd0c36]">
                <Lightbulb size={19} />
              </span>
              <div>
                <p className="text-xs font-semibold text-[#383838]">
                  Acompanhe sua economia
                </p>
                <p className="mt-1 text-[11px] leading-relaxed text-[#777]">
                  Consulte a evolução dos seus gastos para identificar oportunidades de economia.
                </p>
              </div>
            </div>
            <button className="mt-3 flex w-full items-center gap-3 rounded-lg p-2 text-left transition hover:bg-slate-50">
              <span className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-amber-50 text-amber-700">
                <Bell size={17} />
              </span>
              <span className="min-w-0 flex-1">
                <span className="block text-xs font-semibold text-[#383838]">Revise suas assinaturas</span>
                <span className="mt-1 block text-[11px] text-[#888]">Confira as despesas recorrentes.</span>
              </span>
              <ArrowRight size={16} className="text-[#999]" />
            </button>
          </article>
        </section>

        <section className="rounded-xl border border-[#dedede] bg-white p-4 sm:p-5">
          <SectionTitle title="Transações recentes" action="Ver todas" />
          <div>
            {transactions.map((transaction) => (
              <div
                key={`${transaction.name}-${transaction.date}`}
                className="flex items-center gap-3 border-b border-[#eeeeee] py-3 last:border-b-0"
              >
                <span className={`flex size-9 shrink-0 items-center justify-center rounded-lg ${
                  transaction.value > 0
                    ? "bg-emerald-50 text-emerald-700"
                    : "bg-slate-100 text-[#23445d]"
                }`}>
                  {transaction.value > 0 ? <ArrowDownLeft size={18} /> : <Tag size={18} />}
                </span>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-semibold text-[#333]">
                    {transaction.name}
                  </p>
                  <p className="mt-0.5 text-xs text-[#888]">
                    {transaction.category} · {transaction.date}
                  </p>
                </div>
                <span className={`shrink-0 text-right text-xs font-bold sm:text-sm ${
                  transaction.value > 0 ? "text-[#23445d]" : "text-[#333]"
                }`}>
                  {transaction.value > 0 ? "+ " : "- "}
                  {money(Math.abs(transaction.value))}
                </span>
              </div>
            ))}
          </div>
        </section>

      </div>
    </AppLayout>
  );
}