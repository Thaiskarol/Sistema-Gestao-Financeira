import { useMemo, useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Plus,
  Tag,
  Pencil,
  Trash2,
  X,
  Wallet,
  CircleAlert,
  CheckCircle2,
  Save,
} from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";
import { mesAtual } from "@/lib/format";
import { getCategorias } from "@/services/transacoes";
import {
  getOrcamentos,
  criarOrcamento,
  atualizarOrcamento,
  excluirOrcamento,
} from "@/services/orcamentos";

type Budget = {
  id: number;
  categoriaId: number;
  category: string;
  limit: number;
  spent: number;
};

const currency = (value: number) =>
  value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

function getPercentage(spent: number, limit: number) {
  return limit > 0 ? Math.round((spent / limit) * 100) : 0;
}

function getStatus(spent: number, limit: number) {
  const percentage = getPercentage(spent, limit);

  if (percentage >= 90) {
    return {
      label: "Risco",
      color: "#970b2e",
      background: "#fbedf0",
    };
  }

  if (percentage >= 75) {
    return {
      label: "Atenção",
      color: "#d99a00",
      background: "#fff5df",
    };
  }

  return {
    label: "Dentro do limite",
    color: "#30885c",
    background: "#e8f5ed",
  };
}

export default function Budgets() {
  const [month, setMonth] = useState(mesAtual());
  const [categoriaId, setCategoriaId] = useState("");
  const queryClient = useQueryClient();

  const orcamentosQ = useQuery({
    queryKey: ["orcamentos", month],
    queryFn: () => getOrcamentos(month),
  });

  const categoriasQ = useQuery({
    queryKey: ["categorias"],
    queryFn: getCategorias,
  });

  const budgets: Budget[] = useMemo(
    () =>
      (orcamentosQ.data ?? []).map((o) => ({
        id: o.id,
        categoriaId: o.categoriaId,
        category: o.categoriaNome,
        limit: Number(o.limite),
        spent: Number(o.valorConsumido),
      })),
    [orcamentosQ.data],
  );

  const salvar = useMutation({
    mutationFn: (v: {
      id: number | null;
      categoriaId: number;
      limite: number;
    }) =>
      v.id === null
        ? criarOrcamento(v.categoriaId, v.limite)
        : atualizarOrcamento(v.id, v.limite),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orcamentos"] });
      setModalOpen(false);
    },
    onError: (e) => setError((e as Error).message),
  });

  const excluir = useMutation({
    mutationFn: excluirOrcamento,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: ["orcamentos"] }),
    onError: (e) => window.alert((e as Error).message),
  });

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [limit, setLimit] = useState("");
  const [error, setError] = useState("");

  const totalLimit = useMemo(
    () => budgets.reduce((total, budget) => total + budget.limit, 0),
    [budgets],
  );

  const totalSpent = useMemo(
    () => budgets.reduce((total, budget) => total + budget.spent, 0),
    [budgets],
  );

  const remaining = totalLimit - totalSpent;
  const overallPercentage =
    totalLimit > 0 ? Math.round((totalSpent / totalLimit) * 100) : 0;

  const monthLabel = useMemo(() => {
    const [year, monthNumber] = month.split("-").map(Number);
    const date = new Date(year, monthNumber - 1, 1);

    return date.toLocaleDateString("pt-BR", {
      month: "long",
      year: "numeric",
    });
  }, [month]);

  function openNewBudget() {
    setEditingId(null);
    setCategoriaId("");
    setLimit("");
    setError("");
    setModalOpen(true);
  }

  function openEditBudget(budget: Budget) {
    setEditingId(budget.id);
    setCategoriaId(String(budget.categoriaId));
    setLimit(String(budget.limit));
    setError("");
    setModalOpen(true);
  }

  function saveBudget(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const numericLimit = Number(limit.replace(",", "."));
    const idCategoria = Number(categoriaId);

    if (!idCategoria) {
      setError("Informe a categoria.");
      return;
    }

    if (!Number.isFinite(numericLimit) || numericLimit <= 0) {
      setError("Informe um limite maior que zero.");
      return;
    }

    const duplicate = budgets.some(
      (b) => b.categoriaId === idCategoria && b.id !== editingId,
    );

    if (duplicate) {
      setError("Já existe um orçamento para essa categoria.");
      return;
    }

    salvar.mutate({
      id: editingId,
      categoriaId: idCategoria,
      limite: numericLimit,
    });
  }

  function deleteBudget(budget: Budget) {
    if (
      !window.confirm(
        `Deseja excluir o orçamento de "${budget.category}"?`,
      )
    ) {
      return;
    }

    excluir.mutate(budget.id);
  }

  return (
    <AppLayout>
      <section className="space-y-6">
        {/* Título e seleção do período */}
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-[0.16em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="mt-1 text-2xl font-bold tracking-tight text-[#292929] sm:text-[28px]">
              Orçamento
            </h1>
            <p className="mt-1 text-sm text-[#777]">
              Planeje seus limites e acompanhe cada categoria.
            </p>
          </div>

          <label className="flex flex-col gap-1 text-xs text-[#777] sm:items-end">
            <span className="sm:sr-only">Mês do orçamento</span>
            <input
              type="month"
              value={month}
              onChange={(event) => setMonth(event.target.value)}
              className="min-h-10 w-full rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#555] outline-none focus:border-[#bd0c36] sm:w-auto"
            />
          </label>
        </div>

        {/* Resumo financeiro */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-3">
          <article className="rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm text-[#777]">Limite total</p>
              <span className="flex size-9 items-center justify-center rounded-xl bg-[#fbedf0] text-[#bd0c36]">
                <Wallet size={18} />
              </span>
            </div>
            <p className="mt-3 text-2xl font-bold tracking-tight text-[#292929]">
              {currency(totalLimit)}
            </p>
            <p className="mt-1 text-xs text-[#888]">
              Soma dos orçamentos cadastrados
            </p>
          </article>

          <article className="rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm text-[#777]">Total utilizado</p>
              <span className="flex size-9 items-center justify-center rounded-xl bg-[#fff5df] text-[#d99a00]">
                <CircleAlert size={18} />
              </span>
            </div>
            <p className="mt-3 text-2xl font-bold tracking-tight text-[#292929]">
              {currency(totalSpent)}
            </p>
            <p className="mt-1 text-xs text-[#888]">
              {overallPercentage}% do limite total
            </p>
            <div className="mt-3 h-2 overflow-hidden rounded-full bg-[#edf0ef]">
              <div
                className="h-full rounded-full transition-all"
                style={{
                  width: `${Math.min(overallPercentage, 100)}%`,
                  backgroundColor:
                    overallPercentage >= 90 ? "#970b2e" : "#e5a900",
                }}
              />
            </div>
          </article>

          <article className="rounded-2xl border border-[#dedede] bg-white p-4 sm:p-5 sm:col-span-2 xl:col-span-1">
            <div className="flex items-center justify-between gap-3">
              <p className="text-sm text-[#777]">Saldo disponível</p>
              <span className="flex size-9 items-center justify-center rounded-xl bg-[#e8f5ed] text-[#30885c]">
                <CheckCircle2 size={18} />
              </span>
            </div>
            <p
              className={`mt-3 text-2xl font-bold tracking-tight ${
                remaining < 0 ? "text-[#970b2e]" : "text-[#30885c]"
              }`}
            >
              {currency(remaining)}
            </p>
            <p className="mt-1 text-xs text-[#888]">
              {remaining < 0
                ? "Valor acima dos limites cadastrados"
                : "Valor restante dentro dos limites"}
            </p>
          </article>
        </div>

        {/* Orçamento por categoria */}
        <section className="overflow-hidden rounded-2xl border border-[#dedede] bg-white">
          {orcamentosQ.error && (
            <p
              role="alert"
              className="rounded-lg bg-rose-50 p-3 text-sm text-[#a30f35]"
            >
              {(orcamentosQ.error as Error).message}
            </p>
          )}

          <div className="flex flex-col justify-between gap-3 p-4 sm:flex-row sm:items-center sm:px-5 sm:py-5">
            <div>
              <h2 className="text-base font-semibold text-[#333]">
                Orçamento por categoria
              </h2>
              <p className="mt-1 text-xs text-[#888]">
                Acompanhamento de {monthLabel}
              </p>
            </div>

            <button
              type="button"
              onClick={openNewBudget}
              className="inline-flex min-h-10 items-center justify-center gap-2 self-start rounded-xl bg-[#fbedf0] px-4 py-2.5 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f8dfe5] sm:self-auto"
            >
              <Plus size={17} />
              Novo orçamento
            </button>
          </div>

          {budgets.length > 0 ? (
            <>
              {/* Cabeçalho da tabela: telas maiores */}
              <div className="hidden grid-cols-[minmax(120px,1fr)_minmax(190px,1.1fr)_minmax(130px,1.2fr)_88px] gap-4 border-b border-[#dedede] px-5 py-3 text-[10px] font-semibold uppercase tracking-wider text-[#999] lg:grid">
                <span>Categoria</span>
                <span>Limite / Gasto / Restante</span>
                <span>Percentual</span>
                <span>Status</span>
              </div>

              <div className="divide-y divide-[#eeeeee] px-4 sm:px-5">
                {budgets.map((budget) => {
                  const percentage = getPercentage(
                    budget.spent,
                    budget.limit,
                  );
                  const status = getStatus(budget.spent, budget.limit);
                  const categoryRemaining = budget.limit - budget.spent;

                  return (
                    <article key={budget.id} className="py-4">
                      {/* Desktop */}
                      <div className="hidden grid-cols-[minmax(120px,1fr)_minmax(190px,1.1fr)_minmax(130px,1.2fr)_88px] items-center gap-4 lg:grid">
                        <div className="flex min-w-0 items-center gap-2.5">
                          <span className="flex size-8 shrink-0 items-center justify-center rounded-lg bg-[#fbedf0] text-[#bd0c36]">
                            <Tag size={15} />
                          </span>
                          <span className="truncate text-sm font-semibold text-[#333]">
                            {budget.category}
                          </span>
                        </div>

                        <div className="min-w-0">
                          <p className="text-xs leading-6 text-[#555]">
                            {currency(budget.spent)} / {currency(budget.limit)}
                          </p>
                          <p
                            className={`text-[11px] ${
                              categoryRemaining < 0
                                ? "text-[#970b2e]"
                                : "text-[#888]"
                            }`}
                          >
                            {categoryRemaining < 0
                              ? `Excedido em ${currency(Math.abs(categoryRemaining))}`
                              : `Restante: ${currency(categoryRemaining)}`}
                          </p>
                        </div>

                        <div className="flex min-w-0 items-center gap-2">
                          <div className="h-1.5 min-w-0 flex-1 overflow-hidden rounded-full bg-[#edf0ef]">
                            <div
                              className="h-full rounded-full transition-all"
                              style={{
                                width: `${Math.min(percentage, 100)}%`,
                                backgroundColor: status.color,
                              }}
                            />
                          </div>
                          <span className="w-8 text-right text-[10px] text-[#888]">
                            {percentage}%
                          </span>
                        </div>

                        <span
                          className="w-fit rounded-full px-2 py-1 text-[10px] font-semibold"
                          style={{
                            color: status.color,
                            backgroundColor: status.background,
                          }}
                        >
                          {status.label}
                        </span>
                      </div>

                      {/* Celular e tablet */}
                      <div className="space-y-3 lg:hidden">
                        <div className="flex items-start gap-2.5">
                          <span className="flex size-9 shrink-0 items-center justify-center rounded-lg bg-[#fbedf0] text-[#bd0c36]">
                            <Tag size={16} />
                          </span>

                          <div className="min-w-0 flex-1">
                            <p className="break-words text-sm font-semibold text-[#333]">
                              {budget.category}
                            </p>
                            <p className="mt-1 text-xs text-[#777]">
                              {currency(budget.spent)} de{" "}
                              {currency(budget.limit)}
                            </p>
                          </div>

                          <span
                            className="shrink-0 rounded-full px-2 py-1 text-[10px] font-semibold"
                            style={{
                              color: status.color,
                              backgroundColor: status.background,
                            }}
                          >
                            {status.label}
                          </span>
                        </div>

                        <div className="flex items-center gap-2">
                          <div className="h-2 min-w-0 flex-1 overflow-hidden rounded-full bg-[#edf0ef]">
                            <div
                              className="h-full rounded-full transition-all"
                              style={{
                                width: `${Math.min(percentage, 100)}%`,
                                backgroundColor: status.color,
                              }}
                            />
                          </div>
                          <span className="w-9 text-right text-xs text-[#777]">
                            {percentage}%
                          </span>
                        </div>

                        <div className="flex flex-wrap items-center justify-between gap-2">
                          <p
                            className={`text-xs ${
                              categoryRemaining < 0
                                ? "text-[#970b2e]"
                                : "text-[#777]"
                            }`}
                          >
                            {categoryRemaining < 0
                              ? `Excedido em ${currency(Math.abs(categoryRemaining))}`
                              : `Restante: ${currency(categoryRemaining)}`}
                          </p>

                          <div className="flex items-center gap-1">
                            <button
                              type="button"
                              aria-label={`Editar orçamento de ${budget.category}`}
                              onClick={() => openEditBudget(budget)}
                              className="rounded-lg p-2 text-[#777] hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                            >
                              <Pencil size={16} />
                            </button>
                            <button
                              type="button"
                              aria-label={`Excluir orçamento de ${budget.category}`}
                              onClick={() => deleteBudget(budget)}
                              className="rounded-lg p-2 text-[#777] hover:bg-red-50 hover:text-red-600"
                            >
                              <Trash2 size={16} />
                            </button>
                          </div>
                        </div>
                      </div>

                      {/* Ações no desktop */}
                      <div className="mt-2 hidden justify-end gap-1 lg:flex">
                        <button
                          type="button"
                          aria-label={`Editar orçamento de ${budget.category}`}
                          onClick={() => openEditBudget(budget)}
                          className="rounded-lg p-1.5 text-[#888] hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                        >
                          <Pencil size={14} />
                        </button>
                        <button
                          type="button"
                          aria-label={`Excluir orçamento de ${budget.category}`}
                          onClick={() => deleteBudget(budget)}
                          className="rounded-lg p-1.5 text-[#888] hover:bg-red-50 hover:text-red-600"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </article>
                  );
                })}
              </div>
            </>
          ) : (
            <div className="px-4 py-12 text-center">
              <Wallet size={30} className="mx-auto text-[#aaa]" />
              <p className="mt-3 text-sm font-semibold text-[#444]">
                Nenhum orçamento cadastrado
              </p>
              <p className="mt-1 text-xs text-[#888]">
                Adicione um orçamento para começar a acompanhar seus limites.
              </p>
              <button
                type="button"
                onClick={openNewBudget}
                className="mt-4 rounded-xl bg-[#bd0c36] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#a80b30]"
              >
                Novo orçamento
              </button>
            </div>
          )}
        </section>
      </section>

      {/* Modal de criação e edição */}
      {modalOpen && (
        <div
          className="fixed inset-0 z-[60] flex items-center justify-center overflow-y-auto bg-black/40 p-4"
          onClick={(event) => {
            if (event.target === event.currentTarget) {
              setModalOpen(false);
            }
          }}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="budget-modal-title"
            className="my-auto w-full max-w-md rounded-2xl bg-white p-5 shadow-xl sm:p-6"
          >
            <div className="mb-5 flex items-center justify-between gap-3">
              <div>
                <h2
                  id="budget-modal-title"
                  className="text-lg font-semibold text-[#292929]"
                >
                  {editingId === null
                    ? "Novo orçamento"
                    : "Editar orçamento"}
                </h2>
                <p className="mt-1 text-xs text-[#888]">
                  Defina um limite para a categoria.
                </p>
              </div>

              <button
                type="button"
                aria-label="Fechar formulário"
                onClick={() => setModalOpen(false)}
                className="rounded-lg p-2 text-[#777] hover:bg-slate-100"
              >
                <X size={19} />
              </button>
            </div>

            <form onSubmit={saveBudget} className="space-y-4">
              <div>
                <label
                  htmlFor="budget-category"
                  className="mb-1.5 block text-xs font-medium text-[#555]"
                >
                  Categoria
                </label>
                <select
                  id="budget-category"
                  required
                  disabled={editingId !== null}
                  value={categoriaId}
                  onChange={(e) => {
                    setCategoriaId(e.target.value);
                    setError("");
                  }}
                  className="w-full rounded-xl border border-[#dedede] bg-white px-3 py-3 text-sm outline-none focus:border-[#bd0c36]"
                >
                  <option value="">Selecione</option>
                  {(categoriasQ.data ?? []).map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label
                  htmlFor="budget-limit"
                  className="mb-1.5 block text-xs font-medium text-[#555]"
                >
                  Limite mensal (R$)
                </label>
                <input
                  id="budget-limit"
                  type="number"
                  required
                  min="0.01"
                  step="0.01"
                  value={limit}
                  onChange={(event) => {
                    setLimit(event.target.value);
                    setError("");
                  }}
                  placeholder="Ex.: 1500,00"
                  className="w-full rounded-xl border border-[#dedede] px-3 py-3 text-sm outline-none focus:border-[#bd0c36]"
                />
              </div>

              {error && (
                <p role="alert" className="text-xs text-red-600">
                  {error}
                </p>
              )}

              <div className="flex flex-col-reverse gap-2 pt-2 sm:flex-row">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="flex-1 rounded-xl border border-[#dedede] px-4 py-3 text-sm font-medium text-[#555] hover:bg-slate-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-3 text-sm font-semibold text-white hover:bg-[#a80b30]"
                >
                  <Save size={16} />
                  Salvar orçamento
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </AppLayout>
  );
}