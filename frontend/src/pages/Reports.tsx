
import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  Download,
  TrendingUp,
  ArrowLeftRight,
  Wallet,
  FileBarChart,
  CalendarDays,
} from "lucide-react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts";
import { AppLayout } from "../layouts/AppLayout";
import { nomeMes, rotuloMes } from "@/lib/format";
import {
  getEvolucaoDespesas,
  getEvolucaoReceitas,
  getGastosPorCategoria,
} from "@/services/financeiro";

const currency = (value: number) =>
  value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
    maximumFractionDigits: 0,
  });

const percentage = (value: number) =>
  `${value > 0 ? "+" : ""}${value.toLocaleString("pt-BR", {
    maximumFractionDigits: 1,
  })}%`;

  const CORES = ["#19435d", "#970b2e", "#e5a900", "#30885c", "#aeb8c2"];

export default function Reports() {

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

  const erro = (receitasQ.error ?? despesasQ.error ?? gastosQ.error) as
    | Error
    | null;

  // Mês a mês: junta receitas e despesas
  const monthlyData = useMemo(() => {
    const meses = new Map<
      string,
      { receitas: number; despesas: number }
    >();

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
      .map(([mes, v]) => ({
        mes,
        month: rotuloMes(mes),
        receitas: v.receitas,
        despesas: v.despesas,
      }));
  }, [receitasQ.data, despesasQ.data]);

  // Tabela de detalhes por mês
  const monthlyDetails = useMemo(
    () =>
      monthlyData.map((m) => ({
        month: nomeMes(m.mes),
        income: m.receitas,
        expenses: m.despesas,
      })),
    [monthlyData],
  );

  // Pizza: 4 maiores categorias + "Outros"
  const categoryData = useMemo(() => {
    const ordenado = [...(gastosQ.data ?? [])].sort(
      (a, b) => b.valor - a.valor,
    );

    const lista = ordenado
      .slice(0, 4)
      .map((g, i) => ({
        name: g.rotulo,
        value: g.valor,
        color: CORES[i],
      }));

    const resto = ordenado
      .slice(4)
      .reduce((soma, g) => soma + g.valor, 0);

    if (resto > 0) {
      lista.push({
        name: "Outros",
        value: resto,
        color: CORES[4],
      });
    }

    return lista;
  }, [gastosQ.data]);

  // Variação do último mês contra o anterior
  const variacao = (atual: number, anterior: number) =>
    anterior > 0 ? ((atual - anterior) / anterior) * 100 : 0;

  const ultimo = monthlyData[monthlyData.length - 1];
  const penultimo = monthlyData[monthlyData.length - 2];

  const incomeChange = variacao(
    ultimo?.receitas ?? 0,
    penultimo?.receitas ?? 0,
  );

  const expenseChange = variacao(
    ultimo?.despesas ?? 0,
    penultimo?.despesas ?? 0,
  );

  const [period, setPeriod] = useState("6");

  const visibleMonths = Number(period);

  const chartData = useMemo(
    () => monthlyData.slice(-visibleMonths),
    [monthlyData, visibleMonths],
  );

  const details = useMemo(
    () => monthlyDetails.slice(-visibleMonths).reverse(),
    [monthlyDetails, visibleMonths],
  );

  const totals = useMemo(() => {
    const income = chartData.reduce(
      (sum, item) => sum + item.receitas,
      0,
    );

    const expenses = chartData.reduce(
      (sum, item) => sum + item.despesas,
      0,
    );

    return {
      income,
      expenses,
      balance: income - expenses,
      savings: income > 0 ? ((income - expenses) / income) * 100 : 0,
    };
  }, [chartData]);

  const categoryTotal = categoryData.reduce(
    (sum, item) => sum + item.value,
    0,
  );

  function downloadReport() {
    const header = [
      "Mês",
      "Receitas (R$)",
      "Despesas (R$)",
      "Resultado líquido (R$)",
    ];

    const rows = details
      .slice()
      .reverse()
      .map((item) => [
        item.month,
        item.income.toFixed(2).replace(".", ","),
        item.expenses.toFixed(2).replace(".", ","),
        (item.income - item.expenses).toFixed(2).replace(".", ","),
      ]);

    const csvContent = [header, ...rows]
      .map((row) =>
        row.map((value) => `"${String(value).replace(/"/g, '""')}"`).join(";")
      )
      .join("\r\n");

    const blob = new Blob(["\uFEFF" + csvContent], {
      type: "text/csv;charset=utf-8;",
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = "relatorio-financeiro.csv";
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
  }

  return (
    <AppLayout>
      <section className="space-y-5 sm:space-y-6">
        {erro && (
          <p role="alert" className="text-sm text-[#970b2e]">
            Não foi possível carregar os dados do relatório: {erro.message}
          </p>
        )}

        {/* Cabeçalho */}
        <div className="flex flex-col justify-between gap-4 xl:flex-row xl:items-center">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-[0.16em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="mt-1 text-2xl font-bold tracking-tight text-[#292929] sm:text-[28px]">
              Relatórios
            </h1>
            <p className="mt-1 text-sm text-[#777]">
              Entenda seus números e acompanhe sua evolução.
            </p>
          </div>

          <div className="flex flex-col gap-2 sm:flex-row">
            <label className="flex h-10 items-center gap-2 rounded-xl border border-[#dedede] bg-white px-3">
              <CalendarDays size={16} className="text-[#888]" />
              <span className="sr-only">Período do relatório</span>
              <select
                value={period}
                onChange={(event) => setPeriod(event.target.value)}
                className="min-w-0 flex-1 bg-transparent text-sm text-[#555] outline-none"
              >
                <option value="3">Últimos 3 meses</option>
                <option value="6">Últimos 6 meses</option>
              </select>
            </label>

            <button
              type="button"
              onClick={downloadReport}
              className="inline-flex min-h-10 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a80b30] focus:outline-none focus:ring-2 focus:ring-[#bd0c36]/30"
            >
              <Download size={17} />
              Baixar relatório
            </button>
          </div>
        </div>

        {/* Indicadores principais */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-3">
          <article className="relative min-w-0 overflow-hidden rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="relative z-10 flex items-center justify-between gap-3">
              <p className="text-sm font-medium text-[#777]">
                Total de receitas
              </p>
              <span className="flex size-8 items-center justify-center rounded-lg bg-[#eaf0f3] text-[#19435d]">
                <TrendingUp size={17} />
              </span>
            </div>
            <p className="relative z-10 mt-3 text-2xl font-bold tracking-tight text-[#292929]">
              {currency(totals.income)}
            </p>
            <p className="relative z-10 mt-1 text-xs text-[#888]">
              {percentage(incomeChange)} no período
            </p>
            <span className="absolute -bottom-7 -right-4 size-20 rounded-tl-full bg-[#d5dde2]" />
          </article>

          <article className="relative min-w-0 overflow-hidden rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="relative z-10 flex items-center justify-between gap-3">
              <p className="text-sm font-medium text-[#777]">
                Total de despesas
              </p>
              <span className="flex size-8 items-center justify-center rounded-lg bg-[#fbedf0] text-[#970b2e]">
                <ArrowLeftRight size={17} />
              </span>
            </div>
            <p className="relative z-10 mt-3 text-2xl font-bold tracking-tight text-[#292929]">
              {currency(totals.expenses)}
            </p>
            <p className="relative z-10 mt-1 text-xs text-[#888]">
              {percentage(expenseChange)} no período
            </p>
            <span className="absolute -bottom-7 -right-4 size-20 rounded-tl-full bg-[#eacbd3]" />
          </article>

          <article className="relative min-w-0 overflow-hidden rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5 sm:col-span-2 xl:col-span-1">
            <div className="relative z-10 flex items-center justify-between gap-3">
              <p className="text-sm font-medium text-[#777]">
                Resultado líquido
              </p>
              <span className="flex size-8 items-center justify-center rounded-lg bg-[#eaf0f3] text-[#19435d]">
                <Wallet size={17} />
              </span>
            </div>
            <p
              className={`relative z-10 mt-3 text-2xl font-bold tracking-tight ${
                totals.balance < 0 ? "text-[#970b2e]" : "text-[#292929]"
              }`}
            >
              {currency(totals.balance)}
            </p>
            <p className="relative z-10 mt-1 text-xs text-[#888]">
              {totals.savings.toLocaleString("pt-BR", {
                maximumFractionDigits: 1,
              })}
              % de economia
            </p>
            <span className="absolute -bottom-7 -right-4 size-20 rounded-tl-full bg-[#d5dde2]" />
          </article>
        </div>

        {/* Gráficos */}
        <div className="grid grid-cols-1 items-start gap-4 xl:grid-cols-[1.65fr_1fr]">
          <section className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="border-b border-[#eeeeee] pb-4">
              <h2 className="text-base font-semibold text-[#333]">
                Resumo financeiro
              </h2>
              <p className="mt-1 text-xs text-[#888]">
                Receitas e despesas por mês
              </p>
            </div>

            <div className="mt-4 h-[260px] w-full sm:h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={chartData}
                  margin={{ top: 8, right: 4, left: -18, bottom: 0 }}
                  barGap={5}
                  barCategoryGap="28%"
                >
                  <CartesianGrid
                    stroke="#eceeee"
                    vertical={false}
                    strokeDasharray="0"
                  />
                  <XAxis
                    dataKey="month"
                    axisLine={{ stroke: "#d7d7d7" }}
                    tickLine={false}
                    tick={{ fill: "#888", fontSize: 11 }}
                  />
                  <YAxis
                    tickFormatter={(value: number) =>
                      value >= 1000 ? `${value / 1000}k` : String(value)
                    }
                    axisLine={false}
                    tickLine={false}
                    tick={{ fill: "#999", fontSize: 10 }}
                    width={40}
                  />
                  <Tooltip
                    formatter={(value) => currency(Number(value))}
                    contentStyle={{
                      borderRadius: 12,
                      border: "1px solid #dedede",
                      fontSize: 12,
                    }}
                  />
                  <Bar
                    dataKey="receitas"
                    name="Receitas"
                    fill="#bd0c36"
                    radius={[4, 4, 0, 0]}
                    maxBarSize={18}
                  />
                  <Bar
                    dataKey="despesas"
                    name="Despesas"
                    fill="#970b2e"
                    radius={[4, 4, 0, 0]}
                    maxBarSize={18}
                  />
                  <Legend
                    verticalAlign="bottom"
                    height={28}
                    iconType="circle"
                    iconSize={7}
                    wrapperStyle={{ fontSize: 11, paddingTop: 8 }}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </section>

          <section className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <h2 className="text-base font-semibold text-[#333]">
              Despesas por categoria
            </h2>
            <p className="mt-1 text-xs text-[#888]">
              Distribuição dos gastos
            </p>

            <div className="relative mx-auto mt-2 h-[220px] w-full max-w-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius="57%"
                    outerRadius="79%"
                    paddingAngle={2}
                    stroke="none"
                  >
                    {categoryData.map((item) => (
                      <Cell key={item.name} fill={item.color} />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(value) => currency(Number(value))}
                    contentStyle={{
                      borderRadius: 12,
                      border: "1px solid #dedede",
                      fontSize: 12,
                    }}
                  />
                </PieChart>
              </ResponsiveContainer>

              <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-base font-bold text-[#333]">
                  {currency(categoryTotal)}
                </span>
                <span className="mt-1 text-[10px] text-[#888]">
                  Total gasto
                </span>
              </div>
            </div>

            <div className="mt-1 space-y-3">
              {categoryData.map((category) => (
                <div
                  key={category.name}
                  className="flex items-center gap-2"
                >
                  <span
                    className="size-2 shrink-0 rounded-sm"
                    style={{ backgroundColor: category.color }}
                  />
                  <span className="min-w-0 flex-1 truncate text-xs text-[#666]">
                    {category.name}
                  </span>
                  <span className="text-xs font-semibold text-[#444]">
                    {categoryTotal > 0
                      ? Math.round((category.value / categoryTotal) * 100)
                      : 0}
                    %
                  </span>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* Detalhamento mensal */}
        <section className="overflow-hidden rounded-2xl border border-[#dedede] bg-white">
          <div className="flex items-center gap-3 border-b border-[#eeeeee] p-4 sm:p-5">
            <span className="flex size-9 items-center justify-center rounded-xl bg-[#fbedf0] text-[#bd0c36]">
              <FileBarChart size={18} />
            </span>
            <div>
              <h2 className="text-base font-semibold text-[#333]">
                Detalhamento mensal
              </h2>
              <p className="mt-1 text-xs text-[#888]">
                Confira o resultado de cada mês.
              </p>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full min-w-[560px] text-left">
              <thead>
                <tr className="border-b border-[#eeeeee] text-[10px] uppercase tracking-wider text-[#999]">
                  <th className="px-4 py-3 font-semibold sm:px-5">Mês</th>
                  <th className="px-4 py-3 font-semibold sm:px-5">Receitas</th>
                  <th className="px-4 py-3 font-semibold sm:px-5">Despesas</th>
                  <th className="px-4 py-3 font-semibold sm:px-5">Resultado</th>
                </tr>
              </thead>
              <tbody>
                {details.map((item) => {
                  const balance = item.income - item.expenses;

                  return (
                    <tr
                      key={item.month}
                      className="border-b border-[#f0f0f0] last:border-0"
                    >
                      <td className="px-4 py-4 text-sm font-medium text-[#444] sm:px-5">
                        {item.month}
                      </td>
                      <td className="px-4 py-4 text-sm text-[#19435d] sm:px-5">
                        {currency(item.income)}
                      </td>
                      <td className="px-4 py-4 text-sm text-[#970b2e] sm:px-5">
                        {currency(item.expenses)}
                      </td>
                      <td
                        className={`px-4 py-4 text-sm font-semibold sm:px-5 ${
                          balance < 0 ? "text-[#970b2e]" : "text-[#30885c]"
                        }`}
                      >
                        {currency(balance)}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </section>
      </section>
    </AppLayout>
  );
}