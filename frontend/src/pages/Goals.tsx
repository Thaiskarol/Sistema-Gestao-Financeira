import { useState, type FormEvent } from "react";
import {
  Plus,
  Target,
  MoreHorizontal,
  ArrowRight,
  X,
  Car,
  Plane,
  House,
  ShieldCheck,
  CalendarDays,
  CheckCircle2,
} from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";

type Meta = {
  id: number;
  nome: string;
  categoria: string;
  objetivo: number;
  acumulado: number;
  prazo: string;
  cor: string;
  icone: "carro" | "viagem" | "imovel" | "reserva";
};

const metasIniciais: Meta[] = [
  {
    id: 1,
    nome: "Carro",
    categoria: "Veículo",
    objetivo: 45000,
    acumulado: 18200,
    prazo: "Nov 2027",
    cor: "navy",
    icone: "carro",
  },
  {
    id: 2,
    nome: "Viagem",
    categoria: "Lazer",
    objetivo: 18000,
    acumulado: 8450,
    prazo: "Jul 2026",
    cor: "navy",
    icone: "viagem",
  },
  {
    id: 3,
    nome: "Imóvel",
    categoria: "Moradia",
    objetivo: 150000,
    acumulado: 42000,
    prazo: "Dez 2029",
    cor: "wine",
    icone: "imovel",
  },
  {
    id: 4,
    nome: "Reserva de emergência",
    categoria: "Segurança financeira",
    objetivo: 20000,
    acumulado: 12500,
    prazo: "Dez 2026",
    cor: "green",
    icone: "reserva",
  },
];

const formatarMoeda = (valor: number) =>
  valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
    maximumFractionDigits: 0,
  });

function IconeMeta({ tipo }: { tipo: Meta["icone"] }) {
  const props = { size: 19, strokeWidth: 1.8 };

  switch (tipo) {
    case "carro":
      return <Car {...props} />;
    case "viagem":
      return <Plane {...props} />;
    case "imovel":
      return <House {...props} />;
    case "reserva":
      return <ShieldCheck {...props} />;
  }
}

export default function Goals() {
  const [metas, setMetas] = useState(metasIniciais);
  const [modalAberto, setModalAberto] = useState(false);
  const [metaSelecionada, setMetaSelecionada] = useState<Meta | null>(null);
  const [menuAberto, setMenuAberto] = useState<number | null>(null);

  const [nome, setNome] = useState("");
  const [objetivo, setObjetivo] = useState("");
  const [acumulado, setAcumulado] = useState("0");
  const [prazo, setPrazo] = useState("");
  const [categoria, setCategoria] = useState("Outros");
  const [icone, setIcone] = useState<Meta["icone"]>("reserva");

  const totalAcumulado = metas.reduce(
    (total, meta) => total + meta.acumulado,
    0
  );
  const totalObjetivo = metas.reduce(
    (total, meta) => total + meta.objetivo,
    0
  );
  const progressoGeral =
    totalObjetivo > 0
      ? Math.round((totalAcumulado / totalObjetivo) * 100)
      : 0;

  function criarMeta(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const valorObjetivo = Number(objetivo);
    const valorAcumulado = Number(acumulado || 0);

    if (
      !nome.trim() ||
      !prazo ||
      !Number.isFinite(valorObjetivo) ||
      valorObjetivo <= 0 ||
      !Number.isFinite(valorAcumulado) ||
      valorAcumulado < 0
    ) {
      return;
    }

    setMetas((atuais) => [
      ...atuais,
      {
        id: Date.now(),
        nome: nome.trim(),
        categoria,
        objetivo: valorObjetivo,
        acumulado: valorAcumulado,
        prazo: new Date(`${prazo}-02T12:00:00`).toLocaleDateString("pt-BR", {
          month: "short",
          year: "numeric",
        }),
        cor: "navy",
        icone,
      },
    ]);

    setNome("");
    setObjetivo("");
    setAcumulado("0");
    setPrazo("");
    setCategoria("Outros");
    setIcone("reserva");
    setModalAberto(false);
  }

  function excluirMeta(id: number) {
    setMetas((atuais) => atuais.filter((meta) => meta.id !== id));
    setMenuAberto(null);
    setMetaSelecionada(null);
  }

  return (
    <AppLayout>
       <div className="w-full min-w-0 bg-[#f5f5f5]">
          <div className="w-full min-w-0 pb-8">
          {/* Cabeçalho */}
          <header className="mb-7 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-[10px] font-bold uppercase tracking-[0.17em] text-[#8c092e]">
                Visão financeira
              </p>
              <h1 className="mt-1 text-3xl font-bold tracking-tight text-[#292929]">
                Metas
              </h1>
              <p className="mt-1 text-sm text-[#777]">
                Transforme seus planos em conquistas possíveis.
              </p>
            </div>

            <button
              type="button"
              onClick={() => setModalAberto(true)}
              className="inline-flex min-h-11 items-center justify-center gap-2 self-start rounded-lg bg-[#c90832] px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a8072b] sm:self-auto"
            >
              <Plus size={18} />
              Nova meta
            </button>
          </header>

          {/* Título da seção */}
          <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
            <div>
              <h2 className="text-base font-semibold text-[#333]">
                Suas metas
              </h2>
              <p className="mt-0.5 text-xs text-[#888]">
                Acompanhe seu progresso financeiro
              </p>
            </div>
            <span className="rounded-full bg-white px-3 py-1.5 text-xs text-[#777] ring-1 ring-[#e5e5e5]">
              {metas.length} {metas.length === 1 ? "meta ativa" : "metas ativas"}
            </span>
          </div>

          {/* Resumo geral */}
          <section className="mb-5 rounded-2xl border border-[#dedede] bg-white p-5 sm:p-6">
            <div className="mb-5 flex items-center gap-2">
              <span className="flex size-9 items-center justify-center rounded-xl bg-[#f8e9ed] text-[#a30a32]">
                <Target size={19} />
              </span>
              <div>
                <h3 className="text-sm font-semibold text-[#333]">
                  Progresso geral
                </h3>
                <p className="text-xs text-[#888]">
                  Total acumulado em todas as metas
                </p>
              </div>
            </div>

            <div className="grid grid-cols-1 gap-5 sm:grid-cols-3 sm:gap-6">
              <div className="min-w-0 sm:border-r sm:border-[#e5e5e5] sm:pr-5">
                <p className="text-xs text-[#888]">Valor acumulado</p>
                <p className="mt-1 break-words text-2xl font-bold text-[#333]">
                  {formatarMoeda(totalAcumulado)}
                </p>
              </div>

              <div className="min-w-0 sm:border-r sm:border-[#e5e5e5] sm:pr-5">
                <p className="text-xs text-[#888]">Valor total desejado</p>
                <p className="mt-1 break-words text-2xl font-bold text-[#333]">
                  {formatarMoeda(totalObjetivo)}
                </p>
              </div>

              <div>
                <div className="flex items-center justify-between gap-2">
                  <p className="text-xs text-[#888]">Progresso das metas</p>
                  <span className="text-sm font-bold text-[#333]">
                    {progressoGeral}%
                  </span>
                </div>
                <div
                  className="mt-3 h-2 overflow-hidden rounded-full bg-[#edf0ef]"
                  role="progressbar"
                  aria-label="Progresso geral das metas"
                  aria-valuenow={Math.min(progressoGeral, 100)}
                  aria-valuemin={0}
                  aria-valuemax={100}
                >
                  <div
                    className="h-full rounded-full bg-[#32885e] transition-all"
                    style={{ width: `${Math.min(progressoGeral, 100)}%` }}
                  />
                </div>
              </div>
            </div>
          </section>

          {/* Grade de metas */}
          {metas.length === 0 ? (
            <section className="rounded-2xl border border-dashed border-[#d5d5d5] bg-white px-5 py-12 text-center">
              <Target className="mx-auto text-[#8c092e]" size={32} />
              <h3 className="mt-3 font-semibold text-[#333]">
                Você ainda não tem metas
              </h3>
              <p className="mt-1 text-sm text-[#888]">
                Crie sua primeira meta e comece a acompanhar seu progresso.
              </p>
              <button
                type="button"
                onClick={() => setModalAberto(true)}
                className="mt-4 rounded-lg bg-[#c90832] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#a8072b]"
              >
                Criar primeira meta
              </button>
            </section>
          ) : (
            <section className="grid grid-cols-1 gap-4 md:grid-cols-2">
              {metas.map((meta) => {
                const progresso =
                  meta.objetivo > 0
                    ? Math.round((meta.acumulado / meta.objetivo) * 100)
                    : 0;
                const progressoVisual = Math.min(progresso, 100);

                const corIcone =
                  meta.cor === "wine"
                    ? "bg-[#92072d]"
                    : meta.cor === "green"
                      ? "bg-[#32885e]"
                      : "bg-[#193f59]";

                return (
                  <article
                    key={meta.id}
                    className="min-w-0 rounded-2xl border border-[#dedede] bg-white p-5 transition-shadow hover:shadow-sm sm:p-6"
                  >
                    <div className="mb-3 flex items-start justify-between">
                      <span
                        className={`flex size-10 items-center justify-center rounded-xl text-white ${corIcone}`}
                      >
                        <IconeMeta tipo={meta.icone} />
                      </span>

                      <div className="relative">
                        <button
                          type="button"
                          aria-label={`Opções da meta ${meta.nome}`}
                          aria-expanded={menuAberto === meta.id}
                          onClick={() =>
                            setMenuAberto((atual) =>
                              atual === meta.id ? null : meta.id
                            )
                          }
                          className="rounded-full p-1.5 text-[#777] transition hover:bg-[#f3f3f3]"
                        >
                          <MoreHorizontal size={22} />
                        </button>

                        {menuAberto === meta.id && (
                          <div className="absolute right-0 top-9 z-10 w-44 rounded-xl border border-[#e5e5e5] bg-white p-1.5 shadow-lg">
                            <button
                              type="button"
                              onClick={() => {
                                setMetaSelecionada(meta);
                                setMenuAberto(null);
                              }}
                              className="w-full rounded-lg px-3 py-2 text-left text-sm text-[#333] hover:bg-[#f5f5f5]"
                            >
                              Ver detalhes
                            </button>
                            <button
                              type="button"
                              onClick={() => excluirMeta(meta.id)}
                              className="w-full rounded-lg px-3 py-2 text-left text-sm text-[#b4233d] hover:bg-[#fff1f2]"
                            >
                              Excluir meta
                            </button>
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="flex flex-wrap items-start justify-between gap-2">
                      <div className="min-w-0">
                        <h3 className="break-words text-base font-semibold text-[#303030]">
                          {meta.nome}
                        </h3>
                        <p className="mt-0.5 text-xs text-[#888]">
                          Prazo: {meta.prazo}
                        </p>
                      </div>
                      <span className="rounded-full bg-[#f5f5f5] px-2.5 py-1 text-[10px] text-[#777]">
                        {meta.categoria}
                      </span>
                    </div>

                    <div className="mt-5 flex items-end justify-between gap-3">
                      <div className="min-w-0">
                        <p className="text-[11px] text-[#888]">Acumulado</p>
                        <p className="mt-1 break-words text-xs font-semibold text-[#333] sm:text-sm">
                          {formatarMoeda(meta.acumulado)}
                        </p>
                      </div>
                      <div className="min-w-0 text-right">
                        <p className="text-[11px] text-[#888]">Objetivo</p>
                        <p className="mt-1 break-words text-xs font-semibold text-[#333] sm:text-sm">
                          {formatarMoeda(meta.objetivo)}
                        </p>
                      </div>
                    </div>

                    <div
                      className="mt-3 h-1.5 overflow-hidden rounded-full bg-[#edf0ef]"
                      role="progressbar"
                      aria-label={`Progresso da meta ${meta.nome}`}
                      aria-valuenow={progressoVisual}
                      aria-valuemin={0}
                      aria-valuemax={100}
                    >
                      <div
                        className={`h-full rounded-full transition-all ${corIcone}`}
                        style={{ width: `${progressoVisual}%` }}
                      />
                    </div>

                    <div className="mt-2.5 flex flex-wrap items-center justify-between gap-2">
                      <p className="text-[11px] text-[#888]">
                        {progresso}% concluído
                      </p>
                      <button
                        type="button"
                        onClick={() => setMetaSelecionada(meta)}
                        className="inline-flex items-center gap-1 text-xs font-semibold text-[#8c092e] hover:text-[#c90832]"
                      >
                        Ver detalhes
                        <ArrowRight size={14} />
                      </button>
                    </div>
                  </article>
                );
              })}
            </section>
          )}
        </div>

        {/* Modal de nova meta */}
        {modalAberto && (
          <div
            className="fixed inset-0 z-50 flex items-end justify-center bg-black/40 p-0 sm:items-center sm:p-4"
            onMouseDown={(event) => {
              if (event.target === event.currentTarget) {
                setModalAberto(false);
              }
            }}
          >
            <section
              role="dialog"
              aria-modal="true"
              aria-labelledby="titulo-nova-meta"
              className="max-h-[92dvh] w-full overflow-y-auto rounded-t-2xl bg-white p-5 shadow-xl sm:max-w-lg sm:rounded-2xl sm:p-6"
            >
              <div className="mb-5 flex items-center justify-between gap-3">
                <div>
                  <h2
                    id="titulo-nova-meta"
                    className="text-xl font-bold text-[#292929]"
                  >
                    Nova meta
                  </h2>
                  <p className="mt-1 text-sm text-[#888]">
                    Defina o que você deseja conquistar.
                  </p>
                </div>
                <button
                  type="button"
                  aria-label="Fechar"
                  onClick={() => setModalAberto(false)}
                  className="rounded-lg p-2 text-[#777] hover:bg-[#f3f3f3]"
                >
                  <X size={19} />
                </button>
              </div>

              <form onSubmit={criarMeta} className="space-y-4">
                <div>
                  <label
                    htmlFor="nome-meta"
                    className="mb-1.5 block text-sm font-medium text-[#444]"
                  >
                    Nome da meta
                  </label>
                  <input
                    id="nome-meta"
                    required
                    maxLength={80}
                    value={nome}
                    onChange={(event) => setNome(event.target.value)}
                    placeholder="Ex.: Comprar um carro"
                    className="w-full rounded-lg border border-[#ddd] px-3 py-2.5 text-sm outline-none focus:border-[#193f59] focus:ring-2 focus:ring-[#193f59]/10"
                  />
                </div>

                <div>
                  <label
                    htmlFor="categoria-meta"
                    className="mb-1.5 block text-sm font-medium text-[#444]"
                  >
                    Categoria
                  </label>
                  <select
                    id="categoria-meta"
                    value={categoria}
                    onChange={(event) => setCategoria(event.target.value)}
                    className="w-full rounded-lg border border-[#ddd] bg-white px-3 py-2.5 text-sm outline-none focus:border-[#193f59]"
                  >
                    <option>Outros</option>
                    <option>Veículo</option>
                    <option>Lazer</option>
                    <option>Moradia</option>
                    <option>Segurança financeira</option>
                    <option>Educação</option>
                  </select>
                </div>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div>
                    <label
                      htmlFor="objetivo-meta"
                      className="mb-1.5 block text-sm font-medium text-[#444]"
                    >
                      Valor do objetivo (R$)
                    </label>
                    <input
                      id="objetivo-meta"
                      required
                      type="number"
                      min="0.01"
                      step="0.01"
                      value={objetivo}
                      onChange={(event) => setObjetivo(event.target.value)}
                      placeholder="15000"
                      className="w-full rounded-lg border border-[#ddd] px-3 py-2.5 text-sm outline-none focus:border-[#193f59]"
                    />
                  </div>

                  <div>
                    <label
                      htmlFor="acumulado-meta"
                      className="mb-1.5 block text-sm font-medium text-[#444]"
                    >
                      Já acumulado (R$)
                    </label>
                    <input
                      id="acumulado-meta"
                      required
                      type="number"
                      min="0"
                      step="0.01"
                      value={acumulado}
                      onChange={(event) => setAcumulado(event.target.value)}
                      className="w-full rounded-lg border border-[#ddd] px-3 py-2.5 text-sm outline-none focus:border-[#193f59]"
                    />
                  </div>
                </div>

                <div>
                  <label
                    htmlFor="prazo-meta"
                    className="mb-1.5 block text-sm font-medium text-[#444]"
                  >
                    Prazo previsto
                  </label>
                  <input
                    id="prazo-meta"
                    required
                    type="month"
                    value={prazo}
                    onChange={(event) => setPrazo(event.target.value)}
                    className="w-full rounded-lg border border-[#ddd] px-3 py-2.5 text-sm outline-none focus:border-[#193f59]"
                  />
                </div>

                <div>
                  <p className="mb-2 text-sm font-medium text-[#444]">
                    Ícone da meta
                  </p>
                  <div className="grid grid-cols-4 gap-2">
                    {(
                      [
                        ["carro", "Carro"],
                        ["viagem", "Viagem"],
                        ["imovel", "Imóvel"],
                        ["reserva", "Reserva"],
                      ] as const
                    ).map(([valor, rotulo]) => (
                      <button
                        key={valor}
                        type="button"
                        onClick={() => setIcone(valor)}
                        aria-pressed={icone === valor}
                        className={`flex flex-col items-center gap-2 rounded-xl border p-3 text-xs transition ${
                          icone === valor
                            ? "border-[#193f59] bg-[#edf3f6] text-[#193f59]"
                            : "border-[#e5e5e5] text-[#777] hover:bg-[#f8f8f8]"
                        }`}
                      >
                        <IconeMeta tipo={valor} />
                        {rotulo}
                      </button>
                    ))}
                  </div>
                </div>

                <div className="flex flex-col-reverse gap-2 pt-2 sm:flex-row sm:justify-end">
                  <button
                    type="button"
                    onClick={() => setModalAberto(false)}
                    className="rounded-lg border border-[#ddd] px-4 py-2.5 text-sm font-medium text-[#555] hover:bg-[#f7f7f7]"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="inline-flex items-center justify-center gap-2 rounded-lg bg-[#c90832] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#a8072b]"
                  >
                    <Plus size={16} />
                    Criar meta
                  </button>
                </div>
              </form>
            </section>
          </div>
        )}

        {/* Modal de detalhes */}
        {metaSelecionada && (
          <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/40 p-0 sm:items-center sm:p-4">
            <section
              role="dialog"
              aria-modal="true"
              aria-labelledby="titulo-detalhes-meta"
              className="w-full rounded-t-2xl bg-white p-6 shadow-xl sm:max-w-md sm:rounded-2xl"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-3">
                  <span className="flex size-11 items-center justify-center rounded-xl bg-[#193f59] text-white">
                    <IconeMeta tipo={metaSelecionada.icone} />
                  </span>
                  <div>
                    <h2
                      id="titulo-detalhes-meta"
                      className="text-lg font-bold text-[#292929]"
                    >
                      {metaSelecionada.nome}
                    </h2>
                    <p className="text-xs text-[#888]">
                      {metaSelecionada.categoria}
                    </p>
                  </div>
                </div>
                <button
                  type="button"
                  aria-label="Fechar detalhes"
                  onClick={() => setMetaSelecionada(null)}
                  className="rounded-lg p-2 text-[#777] hover:bg-[#f3f3f3]"
                >
                  <X size={19} />
                </button>
              </div>

              <div className="mt-6 rounded-xl bg-[#f7f8f8] p-4">
                <p className="text-xs text-[#888]">Valor acumulado</p>
                <p className="mt-1 text-2xl font-bold text-[#193f59]">
                  {formatarMoeda(metaSelecionada.acumulado)}
                </p>
                <p className="mt-2 text-sm text-[#777]">
                  de {formatarMoeda(metaSelecionada.objetivo)} desejados
                </p>
                <div className="mt-3 h-2 overflow-hidden rounded-full bg-[#e5e9e8]">
                  <div
                    className="h-full rounded-full bg-[#32885e]"
                    style={{
                      width: `${Math.min(
                        Math.round(
                          (metaSelecionada.acumulado /
                            metaSelecionada.objetivo) *
                            100
                        ),
                        100
                      )}%`,
                    }}
                  />
                </div>
              </div>

              <div className="mt-4 flex items-center gap-2 text-sm text-[#666]">
                <CalendarDays size={17} />
                Prazo: {metaSelecionada.prazo}
              </div>

              {metaSelecionada.acumulado >= metaSelecionada.objetivo && (
                <p className="mt-4 flex items-center gap-2 text-sm font-medium text-[#32885e]">
                  <CheckCircle2 size={17} />
                  Objetivo financeiro alcançado!
                </p>
              )}

              <button
                type="button"
                onClick={() => setMetaSelecionada(null)}
                className="mt-6 w-full rounded-lg bg-[#193f59] px-4 py-3 text-sm font-semibold text-white hover:bg-[#123047]"
              >
                Fechar detalhes
              </button>
            </section>
          </div>
        )}
      </div>
    </AppLayout>
  );
}

