import { useState, type ReactNode, type FormEvent } from "react";
import {
  ArrowUpRight,
  HeartPulse,
  Plus,
  BriefcaseBusiness,
  TrendingUp,
  ShieldCheck,
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

const formatarMoeda = (valor: number) =>
  valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

const historicoPatrimonio = [
  { mes: "Dez", valor: 1800 },
  { mes: "Jan", valor: 3200 },
  { mes: "Fev", valor: 4700 },
  { mes: "Mar", valor: 6250 },
  { mes: "Abr", valor: 7200 },
  { mes: "Mai", valor: 8300 },
  { mes: "Jun", valor: 9400 },
];

const carteira = [
  { nome: "Renda fixa", valor: 25608, cor: "bg-[#193f59]" },
  { nome: "Fundos", valor: 8536, cor: "bg-[#32885e]" },
  { nome: "Ações", valor: 6402, cor: "bg-[#193f59]" },
  { nome: "Criptoativos", valor: 2134, cor: "bg-[#e5a900]" },
];

type SummaryCardProps = {
  titulo: string;
  valor: number;
  descricao: string;
  icon: ReactNode;
  iconClass: string;
};

function SummaryCard({
  titulo,
  valor,
  descricao,
  icon,
  iconClass,
}: SummaryCardProps) {
  return (
    <article className="relative min-w-0 overflow-hidden rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
      <div className="flex items-start justify-between gap-3">
        <p className="text-sm font-medium text-[#777]">{titulo}</p>
        <span
          className={`flex size-9 shrink-0 items-center justify-center rounded-xl ${iconClass}`}
        >
          {icon}
        </span>
      </div>

      <p className="mt-2 break-words text-2xl font-bold tracking-tight text-[#292929] sm:text-[27px]">
        {formatarMoeda(valor)}
      </p>

      <p className="mt-2 text-xs leading-5 text-[#888]">{descricao}</p>
      <div className="absolute -bottom-9 -right-5 size-20 rounded-full bg-[#d3dbe0]/80" />
    </article>
  );
}

export default function Investimentos() {
  const [periodo, setPeriodo] = useState("12");
  const [modalAberto, setModalAberto] = useState(false);
  const [tipoInvestimento, setTipoInvestimento] = useState("Renda fixa");
  const [nomeInvestimento, setNomeInvestimento] = useState("");
  const [valorInvestimento, setValorInvestimento] = useState("");
  const [investimentosAdicionados, setInvestimentosAdicionados] = useState<
    { nome: string; tipo: string; valor: number }[]
  >([]);

  const totalCarteira = carteira.reduce((total, item) => total + item.valor, 0);
  const totalAdicionado = investimentosAdicionados.reduce(
    (total, item) => total + item.valor,
    0,
  );
  const patrimonioTotal = totalCarteira + totalAdicionado;
  const reservaEmergencia = 12500;

  const dadosGrafico =
    periodo === "6"
      ? historicoPatrimonio.slice(-6)
      : historicoPatrimonio;

  function adicionarInvestimento(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const valor = Number(valorInvestimento.replace(",", "."));

    if (!nomeInvestimento.trim() || !Number.isFinite(valor) || valor <= 0) {
      return;
    }

    setInvestimentosAdicionados((atuais) => [
      ...atuais,
      {
        nome: nomeInvestimento.trim(),
        tipo: tipoInvestimento,
        valor,
      },
    ]);

    setNomeInvestimento("");
    setValorInvestimento("");
    setModalAberto(false);
  }

  return (
    <AppLayout>
      <div className="mx-auto w-full max-w-[1500px] space-y-5">
        {/* Título */}
        <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="mb-1 text-[10px] font-bold uppercase tracking-[0.18em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-[30px]">
              Investimentos
            </h1>
            <p className="mt-1 text-sm text-[#777] sm:text-base">
              Acompanhe seu patrimônio e fortaleça seu futuro.
            </p>
          </div>

          <button
            type="button"
            onClick={() => setModalAberto(true)}
            className="inline-flex min-h-11 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-3 text-sm font-semibold text-white shadow-lg shadow-[#bd0c36]/10 transition hover:bg-[#a5092e]"
          >
            <Plus size={18} />
            Registrar investimento
          </button>
        </section>

        {/* Indicadores */}
        <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <SummaryCard
            titulo="Total investido"
            valor={totalCarteira + totalAdicionado}
            descricao="Soma dos investimentos registrados"
            icon={<TrendingUp size={19} />}
            iconClass="bg-[#eaf0f5] text-[#193f59]"
          />

          <SummaryCard
            titulo="Patrimônio total"
            valor={patrimonioTotal + reservaEmergencia}
            descricao="Investimentos e reserva de emergência"
            icon={<BriefcaseBusiness size={19} />}
            iconClass="bg-[#eaf0f5] text-[#193f59]"
          />

          <SummaryCard
            titulo="Reserva de emergência"
            valor={reservaEmergencia}
            descricao="Valor separado para imprevistos"
            icon={<HeartPulse size={19} />}
            iconClass="bg-[#fbedf0] text-[#bd0c36]"
          />
        </section>

        {/* Gráfico e carteira */}
        <section className="grid min-w-0 grid-cols-1 gap-4 xl:grid-cols-[minmax(0,1.7fr)_minmax(300px,1fr)]">
          <article className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
            <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
              <h2 className="text-base font-semibold text-[#303030]">
                Evolução do patrimônio
              </h2>

              <select
                aria-label="Período do gráfico"
                value={periodo}
                onChange={(event) => setPeriodo(event.target.value)}
                className="min-h-9 rounded-lg border border-[#dedede] bg-white px-3 text-xs text-[#666] outline-none focus:border-[#bd0c36]"
              >
                <option value="6">6 meses</option>
                <option value="12">12 meses</option>
              </select>
            </div>

            <div className="mt-5 flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold text-[#303030]">
                {formatarMoeda(
                  dadosGrafico[dadosGrafico.length - 1]?.valor ?? 0,
                )}
              </span>
              <span className="inline-flex items-center gap-1 rounded-full bg-[#eaf5ef] px-2 py-1 text-[10px] font-semibold text-[#39845b]">
                <ArrowUpRight size={12} />
                Evolução 
              </span>
            </div>

            <div className="mt-4 h-[240px] min-w-0 sm:h-[290px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart
                  data={dadosGrafico}
                  margin={{ top: 10, right: 8, left: -12, bottom: 0 }}
                >
                  <CartesianGrid
                    stroke="#e7e7e7"
                    strokeDasharray="0"
                    vertical={false}
                  />
                  <XAxis
                    dataKey="mes"
                    tick={{ fill: "#999", fontSize: 11 }}
                    axisLine={{ stroke: "#dedede" }}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fill: "#999", fontSize: 10 }}
                    axisLine={false}
                    tickLine={false}
                    tickFormatter={(valor: number) =>
                      valor === 0 ? "0" : `${valor / 1000}k`
                    }
                    width={38}
                  />
                  <Tooltip
                    formatter={(valor) => [
                      formatarMoeda(Number(valor)),
                      "Patrimônio",
                    ]}
                    contentStyle={{
                      borderRadius: 12,
                      border: "1px solid #dedede",
                      fontSize: 12,
                    }}
                  />
                  <Line
                    type="monotone"
                    dataKey="valor"
                    stroke="#193f59"
                    strokeWidth={2.5}
                    dot={{
                      r: 3,
                      fill: "#ffffff",
                      stroke: "#193f59",
                      strokeWidth: 2,
                    }}
                    activeDot={{ r: 5 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>

            
          </article>

          <div className="min-w-0 space-y-4">
            <article className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="flex items-center justify-between gap-3">
                <h2 className="text-base font-semibold text-[#303030]">
                  Carteira
                </h2>
                <span className="rounded-full bg-[#eaf0f5] px-2 py-1 text-[10px] font-semibold text-[#193f59]">
                  {carteira.length} tipos
                </span>
              </div>

              <div className="mt-5 space-y-4">
                {carteira.map((item) => {
                  const percentual =
                    totalCarteira > 0
                      ? (item.valor / totalCarteira) * 100
                      : 0;

                  return (
                    <div key={item.nome}>
                      <div className="flex items-center justify-between gap-3">
                        <span className="flex min-w-0 items-center gap-2 text-xs text-[#555]">
                          <span
                            className={`size-2 shrink-0 rounded-sm ${item.cor}`}
                          />
                          <span className="truncate">{item.nome}</span>
                        </span>
                        <span className="shrink-0 text-xs font-semibold text-[#333]">
                          {formatarMoeda(item.valor)}
                        </span>
                      </div>

                      <div
                        className="mt-2 h-1.5 overflow-hidden rounded-full bg-[#edf0f0]"
                        role="progressbar"
                        aria-label={`Participação de ${item.nome}`}
                        aria-valuenow={Math.round(percentual)}
                        aria-valuemin={0}
                        aria-valuemax={100}
                      >
                        <div
                          className={`h-full rounded-full ${item.cor}`}
                          style={{ width: `${percentual}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="mt-5 border-t border-[#eeeeee] pt-4">
                <div className="flex items-center justify-between gap-3">
                  <span className="text-xs text-[#777]">Total da carteira</span>
                  <span className="text-sm font-bold text-[#303030]">
                    {formatarMoeda(totalCarteira)}
                  </span>
                </div>
              </div>
            </article>

            {/* Reserva de emergência */}
            <article className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
              <div className="flex items-center gap-3">
                <span className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-[#eaf5ef] text-[#32885e]">
                  <ShieldCheck size={20} />
                </span>
                <div>
                  <h2 className="text-base font-semibold text-[#303030]">
                    Reserva de emergência
                  </h2>
                  <p className="mt-1 text-xs text-[#888]">
                    Proteção para imprevistos
                  </p>
                </div>
              </div>

              <p className="mt-5 text-xl font-bold text-[#303030]">
                {formatarMoeda(reservaEmergencia)}
              </p>

              <div className="mt-3 h-2 overflow-hidden rounded-full bg-[#edf0f0]">
                <div className="h-full w-[65%] rounded-full bg-[#32885e]" />
              </div>

              <div className="mt-3 flex justify-between gap-3 text-[11px] text-[#888]">
                <p>Progresso</p>
                <p>65%</p>
              </div>

              <p className="mt-2 text-xs leading-4 text-[#888]">
                Defina uma meta de reserva adequada às suas despesas mensais.
              </p>
            </article>
          </div>
        </section>

        {/* Investimentos registrados localmente */}
        {investimentosAdicionados.length > 0 && (
          <section className="rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
            <h2 className="text-base font-semibold text-[#303030]">
              Investimentos registrados nesta sessão
            </h2>

            <div className="mt-4 divide-y divide-[#eeeeee]">
              {investimentosAdicionados.map((item, index) => (
                <div
                  key={`${item.nome}-${index}`}
                  className="flex flex-col justify-between gap-2 py-3 sm:flex-row sm:items-center"
                >
                  <div>
                    <p className="text-sm font-semibold text-[#333]">
                      {item.nome}
                    </p>
                    <p className="mt-1 text-xs text-[#888]">{item.tipo}</p>
                  </div>
                  <p className="text-sm font-semibold text-[#193f59]">
                    {formatarMoeda(item.valor)}
                  </p>
                </div>
              ))}
            </div>
          </section>
        )}
      </div>

      {/* Modal para registrar investimento */}
      {modalAberto && (
        <div
          className="fixed inset-0 z-[60] flex items-center justify-center overflow-y-auto bg-black/40 p-4"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              setModalAberto(false);
            }
          }}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="titulo-modal-investimento"
            className="w-full max-w-md rounded-2xl bg-white p-5 shadow-xl sm:p-6"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <h2
                  id="titulo-modal-investimento"
                  className="text-lg font-bold text-[#292929]"
                >
                  Registrar investimento
                </h2>
                <p className="mt-1 text-sm text-[#777]">
                  Informe os dados do investimento.
                </p>
              </div>

              <button
                type="button"
                aria-label="Fechar formulário"
                onClick={() => setModalAberto(false)}
                className="rounded-lg px-2 py-1 text-xl text-[#777] hover:bg-slate-100"
              >
                ×
              </button>
            </div>

            <form onSubmit={adicionarInvestimento} className="mt-5 space-y-4">
              <div>
                <label
                  htmlFor="nome-investimento"
                  className="mb-1.5 block text-sm font-medium text-[#444]"
                >
                  Nome do investimento
                </label>
                <input
                  id="nome-investimento"
                  value={nomeInvestimento}
                  onChange={(event) => setNomeInvestimento(event.target.value)}
                  placeholder="Ex.: CDB, Tesouro Direto"
                  required
                  className="min-h-11 w-full rounded-xl border border-[#dedede] px-3 text-sm outline-none focus:border-[#bd0c36] focus:ring-2 focus:ring-[#bd0c36]/10"
                />
              </div>

              <div>
                <label
                  htmlFor="tipo-investimento"
                  className="mb-1.5 block text-sm font-medium text-[#444]"
                >
                  Tipo
                </label>
                <select
                  id="tipo-investimento"
                  value={tipoInvestimento}
                  onChange={(event) => setTipoInvestimento(event.target.value)}
                  className="min-h-11 w-full rounded-xl border border-[#dedede] bg-white px-3 text-sm outline-none focus:border-[#bd0c36]"
                >
                  <option>Renda fixa</option>
                  <option>Fundos</option>
                  <option>Ações</option>
                  <option>Criptoativos</option>
                  <option>Outros</option>
                </select>
              </div>

              <div>
                <label
                  htmlFor="valor-investimento"
                  className="mb-1.5 block text-sm font-medium text-[#444]"
                >
                  Valor investido (R$)
                </label>
                <input
                  id="valor-investimento"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={valorInvestimento}
                  onChange={(event) => setValorInvestimento(event.target.value)}
                  placeholder="Ex.: 1500,00"
                  required
                  className="min-h-11 w-full rounded-xl border border-[#dedede] px-3 text-sm outline-none focus:border-[#bd0c36] focus:ring-2 focus:ring-[#bd0c36]/10"
                />
              </div>

              <div className="flex flex-col-reverse gap-2 pt-2 sm:flex-row sm:justify-end">
                <button
                  type="button"
                  onClick={() => setModalAberto(false)}
                  className="min-h-10 rounded-xl border border-[#dedede] px-4 py-2 text-sm font-semibold text-[#555] hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="inline-flex min-h-10 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-2 text-sm font-semibold text-white hover:bg-[#a5092e]"
                >
                  <Plus size={16} />
                  Registrar
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </AppLayout>
  );
}