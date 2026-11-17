import { useMemo, useState } from "react";
import {
  ArrowDownLeft,
  ArrowRight,
  ArrowUpRight,
  Bell,
  CheckCircle2,
  Lightbulb,
  Plus,
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
import { useNavigate } from "react-router-dom";
import { AppLayout } from "../layouts/AppLayout";

import { useQuery } from "@tanstack/react-query";
import { getUsuario } from "@/lib/session";
import { rotuloMes } from "@/lib/format";
import {
  getEvolucaoDespesas,
  getEvolucaoReceitas,
  getGastosPorCategoria,
  getResumo,
} from "@/services/financeiro";
import { getCategorias, getTransacoes } from "@/services/transacoes";

const budgetTotal = 5900;
const expensesTotal = 3650;
const budgetUsed = Math.round((expensesTotal / budgetTotal) * 100);
const budgetRemaining = budgetTotal - expensesTotal;

function money(value: number) {
  return value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function SectionTitle({
  title,
  action,
  onAction,
}: {
  title: string;
  action?: string;
  onAction?: () => void;
}) {
  return (
    <div className="mb-4 flex items-center justify-between gap-3">
      <h2 className="text-sm font-semibold text-slate-800 sm:text-base">
        {title}
      </h2>

      {action && onAction && (
        <button
          type="button"
          onClick={onAction}
          className="flex shrink-0 items-center gap-1 text-xs font-medium text-slate-500 transition-colors hover:text-slate-900"
        >
          {action}
          <ArrowRight size={14} />
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
  tone: "slate" | "green" | "red";
}) {
  const toneStyles = {
    slate: "bg-slate-100 text-slate-700",
    green: "bg-emerald-50 text-emerald-700",
    red: "bg-rose-50 text-rose-700",
  };

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 transition-shadow hover:shadow-sm sm:p-6">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-medium text-slate-500">{title}</p>
          <p className="mt-3 text-2xl font-semibold tracking-tight text-slate-800 sm:text-[27px]">
            {value}
          </p>
          <p className="mt-2 text-xs text-slate-400">{caption}</p>
        </div>

        <div className={`rounded-xl p-3 ${toneStyles[tone]}`}>
          <Icon size={19} strokeWidth={1.8} />
        </div>
      </div>
    </div>
  );
}

const CORES = [
    "#23445d",
    "#368661",
    "#e5ad20",
    "#a30f35",
    "#6b7a8f",
    "#8c092e",
  ];

export default function Dashboard() {
  const navigate = useNavigate();
  const [period, setPeriod] = useState<3 | 6>(6);


  const resumoQ = useQuery({
    queryKey: ["resumo"],
    queryFn: getResumo,
  });

  const receitasQ = useQuery({
    queryKey: ["evolucao-receitas"],
    queryFn: getEvolucaoReceitas,
  });

  const despesasQ = useQuery({
    queryKey: ["evolucao-despesas"],
    queryFn: getEvolucaoDespesas,
  });

  const gastosQ = useQuery({
    queryKey: ["gastos-categoria"],
    queryFn: getGastosPorCategoria,
  });

  const transacoesQ = useQuery({
    queryKey: ["transacoes"],
    queryFn: getTransacoes,
  });

  const categoriasQ = useQuery({
    queryKey: ["categorias"],
    queryFn: getCategorias,
  });

  const erro = [
    resumoQ,
    receitasQ,
    despesasQ,
    gastosQ,
    transacoesQ,
    categoriasQ,
  ].find((q) => q.error)?.error as Error | undefined;

  // Gráfico de evolução: junta receitas e despesas pelo mês
  const evolution = useMemo(() => {
    const meses = new Map<string, { receitas: number; despesas: number }>();

    (receitasQ.data ?? []).forEach((i) =>
      meses.set(i.mes, {
        receitas: i.valor,
        despesas: meses.get(i.mes)?.despesas ?? 0,
      }),
    );

    (despesasQ.data ?? []).forEach((i) =>
      meses.set(i.mes, {
        receitas: meses.get(i.mes)?.receitas ?? 0,
        despesas: i.valor,
      }),
    );

    return [...meses.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .slice(-6)
      .map(([mes, v]) => ({
        month: rotuloMes(mes),
        receitas: v.receitas,
        despesas: v.despesas,
        saldo: v.receitas - v.despesas,
      }));
  }, [receitasQ.data, despesasQ.data]);

  // Despesas por categoria
  const categories = useMemo(() => {
    const lista = [...(gastosQ.data ?? [])]
      .sort((a, b) => b.valor - a.valor)
      .slice(0, 5);

    const total = (gastosQ.data ?? []).reduce(
      (soma, g) => soma + g.valor,
      0,
    );

    return lista.map((g, i) => ({
      name: g.rotulo,
      value: g.valor,
      percent: total > 0 ? Math.round((g.valor / total) * 100) : 0,
      color: CORES[i % CORES.length],
    }));
  }, [gastosQ.data]);


  // Transações recentes
  const transactions = useMemo(() => {
    const nomes = new Map(
      (categoriasQ.data ?? []).map((c) => [c.id, c.nome]),
    );

    return [...(transacoesQ.data ?? [])]
      .sort((a, b) => b.dataTransacao.localeCompare(a.dataTransacao))
      .slice(0, 5)
      .map((t) => ({
        name: t.descricao,
        category: nomes.get(t.categoriaId) ?? "Outros",
        date: new Date(`${t.dataTransacao}T00:00:00`).toLocaleDateString(
          "pt-BR",
          {
            day: "2-digit",
            month: "short",
          },
        ),
        value: t.tipo === "DESPESA" ? -t.valor : t.valor,
      }));
  }, [transacoesQ.data, categoriasQ.data]);

  const primeiroNome = getUsuario()?.nome?.split(" ")[0] ?? "";

  const chartData = useMemo(
    () => evolution.slice(-period),
    [period, evolution],
  );

  return (
    <AppLayout subtitle="Acompanhe suas receitas, despesas e objetivos">
      <div className="space-y-7">
        <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-slate-500">
              Olá{primeiroNome ? `, ${primeiroNome}` : ""}!
            </p>
            <h2 className="mt-1 text-xl font-semibold tracking-tight text-slate-800 sm:text-2xl">
              Sua vida financeira em resumo
            </h2>
            <p className="mt-1 text-sm text-slate-400">
              Confira o panorama das suas finanças.
            </p>
          </div>

          <button
            type="button"
            onClick={() => navigate("/nova-transacao")}
            className="inline-flex items-center justify-center gap-2 rounded-xl bg-slate-900 px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-slate-700"
          >
            <Plus size={17} />
            Nova transação
          </button>
        </section>

        {erro && (
          <p
            role="alert"
            className="rounded-lg bg-rose-50 p-3 text-sm text-[#a30f35]"
          >
            {erro.message}
          </p>
        )}

        <section
          aria-label="Resumo financeiro"
          className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3"
        >
          <SummaryCard
            title="Saldo disponível"
            value={money(resumoQ.data?.saldo ?? 0)}
            caption={`Economia acumulada: ${money(
              resumoQ.data?.economiaAcumulada ?? 0,
            )}`}
            icon={Wallet}
            tone="slate"
          />

          <SummaryCard
            title="Total de receitas"
            value={money(resumoQ.data?.totalReceitas ?? 0)}
            caption="Total do histórico"
            icon={ArrowDownLeft}
            tone="green"
          />

          <SummaryCard
            title="Total de despesas"
            value={money(resumoQ.data?.totalDespesas ?? 0)}
            caption={`${
              resumoQ.data?.percentualComprometimentoRenda ?? 0
            }% da renda comprometida`}
            icon={ArrowUpRight}
            tone="red"
          />
        </section>

        <section className="grid grid-cols-1 gap-5 xl:grid-cols-[1.65fr_1fr]">
          <div className="min-w-0 rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
            <div className="mb-5 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
              <div>
                <h2 className="text-sm font-semibold text-slate-800 sm:text-base">
                  Evolução financeira
                </h2>
                <p className="mt-1 text-xs text-slate-400">
                  Comparativo entre receitas, despesas e saldo
                </p>
              </div>

              <div className="flex w-fit rounded-lg bg-slate-100 p-1">
                {([3, 6] as const).map((item) => (
                  <button
                    key={item}
                    type="button"
                    onClick={() => setPeriod(item)}
                    className={`rounded-md px-3 py-1.5 text-xs font-medium transition-colors ${
                      period === item
                        ? "bg-white text-slate-800 shadow-sm"
                        : "text-slate-500 hover:text-slate-700"
                    }`}
                  >
                    {item} meses
                  </button>
                ))}
              </div>
            </div>

            <div className="mb-4 flex flex-wrap gap-x-5 gap-y-2">
              <div className="flex items-center gap-2 text-xs text-slate-500">
                <span className="h-2 w-2 rounded-full bg-emerald-600" />
                Receitas
              </div>
              <div className="flex items-center gap-2 text-xs text-slate-500">
                <span className="h-2 w-2 rounded-full bg-rose-500" />
                Despesas
              </div>
              <div className="flex items-center gap-2 text-xs text-slate-500">
                <span className="h-2 w-2 rounded-full bg-slate-700" />
                Saldo
              </div>
            </div>

            <div className="h-[260px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={chartData}
                  margin={{ top: 8, right: 8, left: -18, bottom: 0 }}
                >
                  <CartesianGrid
                    stroke="#eef1f4"
                    strokeDasharray="4 4"
                    vertical={false}
                  />
                  <XAxis
                    dataKey="month"
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: "#94a3b8", fontSize: 11 }}
                    dy={10}
                  />
                  <YAxis
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: "#94a3b8", fontSize: 10 }}
                    tickFormatter={(value: number) =>
                      `R$ ${(value / 1000).toFixed(0)} mil`
                    }
                  />
                  <Tooltip
                    formatter={(value) => money(Number(value))}
                    contentStyle={{
                      border: "1px solid #e2e8f0",
                      borderRadius: "12px",
                      fontSize: "12px",
                      boxShadow: "0 8px 24px rgba(15, 23, 42, 0.08)",
                    }}
                  />
                  <Line
                    type="monotone"
                    dataKey="receitas"
                    name="Receitas"
                    stroke="#368661"
                    strokeWidth={2.5}
                    dot={false}
                    activeDot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="despesas"
                    name="Despesas"
                    stroke="#e05b6b"
                    strokeWidth={2.5}
                    dot={false}
                    activeDot={{ r: 5 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="saldo"
                    name="Saldo"
                    stroke="#334155"
                    strokeWidth={2.5}
                    dot={false}
                    activeDot={{ r: 5 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
            <SectionTitle title="Orçamento mensal" />

            <div className="flex flex-col items-center py-3">
              <div
                className="relative flex h-44 w-44 items-center justify-center rounded-full"
                style={{
                  background: `conic-gradient(#368661 ${budgetUsed}%, #e9eef0 ${budgetUsed}% 100%)`,
                }}
              >
                <div className="flex h-[138px] w-[138px] flex-col items-center justify-center rounded-full bg-white">
                  <span className="text-xs text-slate-400">Utilizado</span>
                  <strong className="mt-1 text-3xl font-semibold text-slate-800">
                    {budgetUsed}%
                  </strong>
                  <span className="mt-1 text-[11px] text-slate-400">
                    do orçamento
                  </span>
                </div>
              </div>

              <p className="mt-5 text-xl font-semibold text-slate-800">
                {money(expensesTotal)}
              </p>
              <p className="mt-1 text-xs text-slate-400">
                de {money(budgetTotal)} planejados
              </p>
            </div>

            <div className="mt-3 space-y-3 border-t border-slate-100 pt-4">
              <div className="flex items-center justify-between text-xs">
                <span className="text-slate-500">Valor utilizado</span>
                <span className="font-medium text-slate-700">
                  {money(expensesTotal)}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="text-slate-500">Restante</span>
                <span className="font-semibold text-emerald-700">
                  {money(budgetRemaining)}
                </span>
              </div>
              <div className="flex items-center gap-2 rounded-xl bg-emerald-50 px-3 py-2.5 text-xs font-medium text-emerald-700">
                <CheckCircle2 size={15} />
                Dentro do orçamento planejado
              </div>
            </div>

            <button
              type="button"
              onClick={() => navigate("/orcamento")}
              className="mt-4 w-full rounded-xl border border-slate-200 px-4 py-2.5 text-xs font-medium text-slate-600 transition-colors hover:bg-slate-50"
            >
              Ver orçamento
            </button>
          </div>
        </section>

        <section className="grid grid-cols-1 gap-5 xl:grid-cols-2">
          <div className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
            <SectionTitle
              title="Despesas por categoria"
              action="Ver categorias"
              onAction={() => navigate("/categorias")}
            />

            <div className="space-y-5 pt-2">
              {categories.map((category) => (
                <div key={category.name}>
                  <div className="mb-2 flex items-center justify-between gap-3">
                    <div className="flex items-center gap-2">
                      <span
                        className="h-2.5 w-2.5 rounded-full"
                        style={{ backgroundColor: category.color }}
                      />
                      <span className="text-xs font-medium text-slate-600">
                        {category.name}
                      </span>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-semibold text-slate-700">
                        {money(category.value)}
                      </span>
                      <span className="ml-2 text-[11px] text-slate-400">
                        {category.percent}%
                      </span>
                    </div>
                  </div>

                  <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                    <div
                      className="h-full rounded-full transition-all"
                      style={{
                        width: `${category.percent}%`,
                        backgroundColor: category.color,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
            <SectionTitle
              title="Insights e alertas"
              action="Ver insights"
              onAction={() => navigate("/insights")}
            />

            <div className="flex gap-3 rounded-xl border border-slate-100 bg-slate-50 p-4">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-amber-50 text-amber-700">
                <Lightbulb size={18} />
              </div>
              <div>
                <p className="text-sm font-semibold text-slate-700">
                  Acompanhe seus gastos
                </p>
                <p className="mt-1 text-xs leading-relaxed text-slate-500">
                  Confira a distribuição das despesas por categoria e compare
                  seus gastos com o orçamento mensal.
                </p>
              </div>
            </div>

            <button
              type="button"
              onClick={() => navigate("/relatorios")}
              className="mt-4 flex w-full items-center justify-between rounded-xl border border-slate-200 px-4 py-3 text-left transition-colors hover:bg-slate-50"
            >
              <div className="flex items-center gap-3">
                <div className="rounded-lg bg-slate-100 p-2 text-slate-600">
                  <Bell size={16} />
                </div>
                <div>
                  <p className="text-xs font-semibold text-slate-700">
                    Acompanhe seus relatórios
                  </p>
                  <p className="mt-1 text-[11px] text-slate-400">
                    Consulte o histórico financeiro
                  </p>
                </div>
              </div>
              <ArrowRight size={16} className="text-slate-400" />
            </button>
          </div>
        </section>

        <section className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
          <SectionTitle
            title="Transações recentes"
            action="Ver todas"
            onAction={() => navigate("/transacoes")}
          />

          <div className="divide-y divide-slate-100">
            {transactions.map((transaction, index) => {
              const isIncome = transaction.value > 0;

              return (
                <div
                  key={`${transaction.name}-${index}`}
                  className="flex items-center justify-between gap-3 py-4 first:pt-2 last:pb-1"
                >
                  <div className="flex min-w-0 items-center gap-3">
                    <div
                      className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl ${
                        isIncome
                          ? "bg-emerald-50 text-emerald-700"
                          : "bg-slate-100 text-slate-600"
                      }`}
                    >
                      {isIncome ? (
                        <ArrowDownLeft size={18} />
                      ) : (
                        <ArrowUpRight size={18} />
                      )}
                    </div>

                    <div className="min-w-0">
                      <p className="truncate text-xs font-semibold text-slate-700 sm:text-sm">
                        {transaction.name}
                      </p>
                      <p className="mt-1 truncate text-[11px] text-slate-400">
                        {transaction.category} · {transaction.date}
                      </p>
                    </div>
                  </div>

                  <p
                    className={`shrink-0 text-xs font-semibold sm:text-sm ${
                      isIncome ? "text-emerald-700" : "text-slate-700"
                    }`}
                  >
                    {isIncome ? "+" : "−"}
                    {money(Math.abs(transaction.value))}
                  </p>
                </div>
              );
            })}
          </div>

          <button
            type="button"
            onClick={() => navigate("/nova-transacao")}
            className="mt-4 inline-flex items-center gap-2 rounded-xl border border-slate-200 px-4 py-2.5 text-xs font-medium text-slate-600 transition-colors hover:bg-slate-50"
          >
            <Plus size={15} />
            Adicionar transação
          </button>
        </section>
      </div>
    </AppLayout>
  );
}
