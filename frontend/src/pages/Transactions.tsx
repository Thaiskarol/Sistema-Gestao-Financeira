import { useState } from "react";
import {
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  Eye,
  Pencil,
  Search,
  Tag,
  Trash2,
  TrendingUp,
  X,
  Save,
} from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";

type Transaction = {
  id: number;
  description: string;
  category: string;
  date: string;
  amount: number;
};

const initialTransactions: Transaction[] = [
  {
    id: 1,
    description: "Supermercado Verde",
    category: "Alimentação",
    date: "2025-06-30T09:42:00",
    amount: -284.6,
  },
  {
    id: 2,
    description: "Salário mensal",
    category: "Renda",
    date: "2025-06-05T08:15:00",
    amount: 7850,
  },
  {
    id: 3,
    description: "Netflix",
    category: "Assinaturas",
    date: "2025-06-04T20:00:00",
    amount: -39.9,
  },
  {
    id: 4,
    description: "Posto Avenida",
    category: "Transporte",
    date: "2025-06-03T18:22:00",
    amount: -220,
  },
  {
    id: 5,
    description: "Rendimento CDB",
    category: "Investimentos",
    date: "2025-06-01T10:10:00",
    amount: 126.42,
  },
  {
    id: 6,
    description: "Farmácia Boa Saúde",
    category: "Saúde",
    date: "2025-06-28T11:30:00",
    amount: -78.5,
  },
  {
    id: 7,
    description: "Conta de energia",
    category: "Moradia",
    date: "2025-06-26T14:20:00",
    amount: -156.3,
  },
  {
    id: 8,
    description: "Restaurante Sabor",
    category: "Alimentação",
    date: "2025-06-24T12:45:00",
    amount: -86.9,
  },
  {
    id: 9,
    description: "Projeto freelance",
    category: "Renda",
    date: "2025-06-20T16:10:00",
    amount: 950,
  },
  {
    id: 10,
    description: "Internet fibra",
    category: "Moradia",
    date: "2025-06-18T09:00:00",
    amount: -109.9,
  },
  {
    id: 11,
    description: "Academia",
    category: "Saúde",
    date: "2025-06-12T07:30:00",
    amount: -89.9,
  },
  {
    id: 12,
    description: "Música Premium",
    category: "Assinaturas",
    date: "2025-06-10T19:15:00",
    amount: -21.9,
  },
];

const categories = [
  "Alimentação",
  "Assinaturas",
  "Transporte",
  "Renda",
  "Investimentos",
  "Saúde",
  "Moradia",
  "Lazer",
  "Outros",
];

const currency = (amount: number) =>
  amount.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

const formatAmount = (amount: number) =>
  amount >= 0 ? `+ ${currency(amount)}` : `-${currency(Math.abs(amount))}`;

const formatDate = (date: string) => {
  const value = new Date(date);
  const dayMonth = new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "short",
  })
    .format(value)
    .replace(".", "");

  const time = new Intl.DateTimeFormat("pt-BR", {
    hour: "2-digit",
    minute: "2-digit",
  }).format(value);

  return `${dayMonth} · ${time}`;
};

export default function Transactions() {
  const [transactions, setTransactions] =
    useState<Transaction[]>(initialTransactions);

  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("Todos os tipos");
  const [categoryFilter, setCategoryFilter] =
    useState("Todas as categorias");

  const [startDate, setStartDate] = useState("2025-06-01");
  const [endDate, setEndDate] = useState("2025-06-30");
  const [page, setPage] = useState(1);

  const [selected, setSelected] = useState<Transaction | null>(null);
  const [modalMode, setModalMode] = useState<"view" | "edit" | null>(null);
  const [editDescription, setEditDescription] = useState("");
  const [editCategory, setEditCategory] = useState("");

  const pageSize = 5;

  const filteredTransactions = transactions.filter((transaction) => {
    const searchMatch =
      transaction.description.toLowerCase().includes(search.toLowerCase()) ||
      transaction.category.toLowerCase().includes(search.toLowerCase());

    const typeMatch =
      typeFilter === "Todos os tipos" ||
      (typeFilter === "Entrada" && transaction.amount >= 0) ||
      (typeFilter === "Saída" && transaction.amount < 0);

    const categoryMatch =
      categoryFilter === "Todas as categorias" ||
      transaction.category === categoryFilter;

    const date = transaction.date.slice(0, 10);
    const startMatch = !startDate || date >= startDate;
    const endMatch = !endDate || date <= endDate;

    return (
      searchMatch &&
      typeMatch &&
      categoryMatch &&
      startMatch &&
      endMatch
    );
  });

  const totalPages = Math.max(
    1,
    Math.ceil(filteredTransactions.length / pageSize)
  );

  const currentPage = Math.min(page, totalPages);

  const visibleTransactions = filteredTransactions.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  function resetFilters() {
    setSearch("");
    setTypeFilter("Todos os tipos");
    setCategoryFilter("Todas as categorias");
    setStartDate("2025-06-01");
    setEndDate("2025-06-30");
    setPage(1);
  }

  function openView(transaction: Transaction) {
    setSelected(transaction);
    setModalMode("view");
  }

  function openEdit(transaction: Transaction) {
    setSelected(transaction);
    setEditDescription(transaction.description);
    setEditCategory(transaction.category);
    setModalMode("edit");
  }

  function saveEdit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selected || !editDescription.trim()) return;

    setTransactions((current) =>
      current.map((transaction) =>
        transaction.id === selected.id
          ? {
              ...transaction,
              description: editDescription.trim(),
              category: editCategory,
            }
          : transaction
      )
    );

    setSelected(null);
    setModalMode(null);
  }

  function deleteTransaction(transaction: Transaction) {
    const confirmed = window.confirm(
      `Deseja excluir a transação "${transaction.description}"?`
    );

    if (!confirmed) return;

    setTransactions((current) =>
      current.filter((item) => item.id !== transaction.id)
    );

    setPage(1);
  }

  return (
    <AppLayout subtitle="Junho de 2025">
      <section className="space-y-4">
        {/* Filtros */}
        <div className="flex flex-wrap items-center gap-2.5 rounded-2xl border border-[#dedede] bg-white p-3">
          <div className="flex h-10 min-w-[220px] flex-[2_1_250px] items-center gap-2 rounded-xl border border-[#dedede] px-3">
            <Search size={18} className="shrink-0 text-[#777]" />
            <input
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setPage(1);
              }}
              placeholder="Descrição ou estabelecimento"
              aria-label="Buscar transações"
              className="w-full min-w-0 bg-transparent text-sm outline-none placeholder:text-[#aaa]"
            />
          </div>

          <select
            aria-label="Filtrar por tipo"
            value={typeFilter}
            onChange={(event) => {
              setTypeFilter(event.target.value);
              setPage(1);
            }}
            className="h-10 min-w-[145px] flex-1 rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#666] outline-none focus:border-[#bd0c36]"
          >
            <option>Todos os tipos</option>
            <option>Entrada</option>
            <option>Saída</option>
          </select>

          <select
            aria-label="Filtrar por categoria"
            value={categoryFilter}
            onChange={(event) => {
              setCategoryFilter(event.target.value);
              setPage(1);
            }}
            className="h-10 min-w-[165px] flex-1 rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#666] outline-none focus:border-[#bd0c36]"
          >
            <option>Todas as categorias</option>
            {categories.map((category) => (
              <option key={category}>{category}</option>
            ))}
          </select>

          <div className="flex h-10 min-w-[230px] flex-[1.5_1_235px] items-center gap-2 rounded-xl border border-[#dedede] px-2.5">
            <CalendarDays size={17} className="shrink-0 text-[#777]" />
            <input
              aria-label="Data inicial"
              type="date"
              value={startDate}
              max={endDate || undefined}
              onChange={(event) => {
                setStartDate(event.target.value);
                setPage(1);
              }}
              className="min-w-0 w-full bg-transparent text-xs text-[#666] outline-none"
            />
            <span className="text-[#aaa]">—</span>
            <input
              aria-label="Data final"
              type="date"
              value={endDate}
              min={startDate || undefined}
              onChange={(event) => {
                setEndDate(event.target.value);
                setPage(1);
              }}
              className="min-w-0 w-full bg-transparent text-xs text-[#666] outline-none"
            />
          </div>

          <button
            type="button"
            onClick={resetFilters}
            className="h-10 rounded-lg px-2 text-xs font-semibold text-[#bd0c36] transition hover:bg-[#fbedf0]"
          >
            Limpar filtros
          </button>
        </div>

        {/* Tabela para telas médias e grandes */}
        <div className="hidden overflow-hidden rounded-2xl border border-[#dedede] bg-white md:block">
          <div className="overflow-x-auto px-5">
            <table className="w-full min-w-[680px] border-collapse text-left">
              <thead>
                <tr className="h-11 border-b border-[#dedede] text-[10px] font-semibold uppercase tracking-[0.08em] text-[#999]">
                  <th className="px-4">Movimentação</th>
                  <th className="w-[165px] px-4">Valor</th>
                  <th className="w-[150px] px-4 text-center">Ações</th>
                </tr>
              </thead>

              <tbody>
                {visibleTransactions.map((transaction) => {
                  const isIncome = transaction.amount >= 0;

                  return (
                    <tr
                      key={transaction.id}
                      className="h-[63px] border-b border-[#ededed] last:border-b-0 hover:bg-[#fdfbfb]"
                    >
                      <td className="px-0 py-2">
                        <div className="flex items-center gap-3">
                          <span
                            className={`flex size-[34px] shrink-0 items-center justify-center rounded-lg ${
                              isIncome
                                ? "bg-[#fbedf0] text-[#bd0c36]"
                                : "bg-[#edf0f2] text-[#3b5669]"
                            }`}
                          >
                            {isIncome ? (
                              <TrendingUp size={18} />
                            ) : (
                              <Tag size={18} />
                            )}
                          </span>

                          <div className="min-w-0">
                            <p className="truncate text-xs font-semibold text-[#333]">
                              {transaction.description}
                            </p>
                            <p className="mt-1 text-[10px] text-[#888]">
                              {transaction.category} · {formatDate(transaction.date)}
                            </p>
                          </div>
                        </div>
                      </td>

                      <td
                        className={`whitespace-nowrap px-4 text-xs font-bold ${
                          isIncome ? "text-[#214d65]" : "text-[#333]"
                        }`}
                      >
                        {formatAmount(transaction.amount)}
                      </td>

                      <td className="px-4">
                        <div className="flex items-center justify-center gap-3">
                          <button
                            type="button"
                            aria-label={`Visualizar ${transaction.description}`}
                            onClick={() => openView(transaction)}
                            className="rounded-md p-1.5 text-[#777] transition hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                          >
                            <Eye size={17} strokeWidth={1.6} />
                          </button>
                          <button
                            type="button"
                            aria-label={`Editar ${transaction.description}`}
                            onClick={() => openEdit(transaction)}
                            className="rounded-md p-1.5 text-[#777] transition hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                          >
                            <Pencil size={17} strokeWidth={1.6} />
                          </button>
                          <button
                            type="button"
                            aria-label={`Excluir ${transaction.description}`}
                            onClick={() => deleteTransaction(transaction)}
                            className="rounded-md p-1.5 text-[#777] transition hover:bg-red-50 hover:text-red-600"
                          >
                            <Trash2 size={17} strokeWidth={1.6} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>

            {visibleTransactions.length === 0 && (
              <div className="px-4 py-12 text-center text-sm text-[#888]">
                Nenhuma transação encontrada com esses filtros.
              </div>
            )}
          </div>

          <Pagination
            page={currentPage}
            totalPages={totalPages}
            total={filteredTransactions.length}
            onPageChange={setPage}
          />
        </div>

        {/* Cartões para celular */}
        <div className="space-y-3 md:hidden">
          {visibleTransactions.map((transaction) => {
            const isIncome = transaction.amount >= 0;

            return (
              <article
                key={transaction.id}
                className="rounded-2xl border border-[#dedede] bg-white p-4"
              >
                <div className="flex items-start gap-3">
                  <span
                    className={`flex size-10 shrink-0 items-center justify-center rounded-xl ${
                      isIncome
                        ? "bg-[#fbedf0] text-[#bd0c36]"
                        : "bg-[#edf0f2] text-[#3b5669]"
                    }`}
                  >
                    {isIncome ? <TrendingUp size={19} /> : <Tag size={19} />}
                  </span>

                  <div className="min-w-0 flex-1">
                    <p className="break-words text-sm font-semibold text-[#333]">
                      {transaction.description}
                    </p>
                    <p className="mt-1 text-xs text-[#888]">
                      {transaction.category} · {formatDate(transaction.date)}
                    </p>
                  </div>

                  <span
                    className={`whitespace-nowrap text-xs font-bold ${
                      isIncome ? "text-[#214d65]" : "text-[#333]"
                    }`}
                  >
                    {formatAmount(transaction.amount)}
                  </span>
                </div>

                <div className="mt-3 flex justify-end gap-2 border-t border-[#eee] pt-3">
                  <button
                    type="button"
                    onClick={() => openView(transaction)}
                    className="flex items-center gap-1.5 rounded-lg px-3 py-2 text-xs text-[#666] hover:bg-slate-50"
                  >
                    <Eye size={15} /> Visualizar
                  </button>
                  <button
                    type="button"
                    onClick={() => openEdit(transaction)}
                    className="flex items-center gap-1.5 rounded-lg px-3 py-2 text-xs text-[#666] hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                  >
                    <Pencil size={15} /> Editar
                  </button>
                  <button
                    type="button"
                    onClick={() => deleteTransaction(transaction)}
                    aria-label={`Excluir ${transaction.description}`}
                    className="rounded-lg p-2 text-[#777] hover:bg-red-50 hover:text-red-600"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
              </article>
            );
          })}

          {visibleTransactions.length === 0 && (
            <div className="rounded-2xl border border-[#dedede] bg-white px-4 py-12 text-center text-sm text-[#888]">
              Nenhuma transação encontrada com esses filtros.
            </div>
          )}

          <div className="rounded-xl border border-[#dedede] bg-white">
            <Pagination
              page={currentPage}
              totalPages={totalPages}
              total={filteredTransactions.length}
              onPageChange={setPage}
            />
          </div>
        </div>
      </section>

      {/* Modal de visualização e edição */}
      {selected && modalMode && (
        <div
          className="fixed inset-0 z-[60] flex items-center justify-center bg-black/40 p-4"
          role="presentation"
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="transaction-modal-title"
            className="w-full max-w-md rounded-2xl bg-white p-5 shadow-xl sm:p-6"
          >
            <div className="mb-5 flex items-center justify-between gap-3">
              <h2
                id="transaction-modal-title"
                className="text-base font-semibold text-[#292929]"
              >
                {modalMode === "view"
                  ? "Detalhes da transação"
                  : "Editar transação"}
              </h2>

              <button
                type="button"
                aria-label="Fechar"
                onClick={() => {
                  setSelected(null);
                  setModalMode(null);
                }}
                className="rounded-lg p-2 text-[#777] hover:bg-slate-100"
              >
                <X size={18} />
              </button>
            </div>

            {modalMode === "view" ? (
              <div className="space-y-4">
                <div>
                  <p className="text-xs text-[#888]">Descrição</p>
                  <p className="mt-1 break-words text-sm font-semibold">
                    {selected.description}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-[#888]">Categoria</p>
                  <p className="mt-1 text-sm">{selected.category}</p>
                </div>

                <div>
                  <p className="text-xs text-[#888]">Data e horário</p>
                  <p className="mt-1 text-sm">{formatDate(selected.date)}</p>
                </div>

                <div>
                  <p className="text-xs text-[#888]">Valor</p>
                  <p
                    className={`mt-1 text-lg font-bold ${
                      selected.amount >= 0
                        ? "text-[#214d65]"
                        : "text-[#333]"
                    }`}
                  >
                    {formatAmount(selected.amount)}
                  </p>
                </div>

                <button
                  type="button"
                  onClick={() => {
                    setSelected(null);
                    setModalMode(null);
                  }}
                  className="w-full rounded-xl bg-[#bd0c36] px-4 py-3 text-sm font-semibold text-white hover:bg-[#a80b30]"
                >
                  Fechar
                </button>
              </div>
            ) : (
              <form onSubmit={saveEdit} className="space-y-4">
                <div>
                  <label
                    htmlFor="transaction-description"
                    className="mb-1.5 block text-xs font-medium text-[#555]"
                  >
                    Descrição
                  </label>
                  <input
                    id="transaction-description"
                    required
                    maxLength={100}
                    value={editDescription}
                    onChange={(event) =>
                      setEditDescription(event.target.value)
                    }
                    className="w-full rounded-xl border border-[#dedede] px-3 py-2.5 text-sm outline-none focus:border-[#bd0c36]"
                  />
                </div>

                <div>
                  <label
                    htmlFor="transaction-category"
                    className="mb-1.5 block text-xs font-medium text-[#555]"
                  >
                    Categoria
                  </label>
                  <select
                    id="transaction-category"
                    value={editCategory}
                    onChange={(event) => setEditCategory(event.target.value)}
                    className="w-full rounded-xl border border-[#dedede] bg-white px-3 py-2.5 text-sm outline-none focus:border-[#bd0c36]"
                  >
                    {categories.map((category) => (
                      <option key={category} value={category}>
                        {category}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <p className="text-xs text-[#888]">Valor atual</p>
                  <p className="mt-1 text-sm font-semibold">
                    {formatAmount(selected.amount)}
                  </p>
                  <p className="mt-1 text-xs text-[#999]">
                    A edição do valor poderá ser adicionada depois.
                  </p>
                </div>

                <div className="flex flex-col-reverse gap-2 pt-2 sm:flex-row">
                  <button
                    type="button"
                    onClick={() => {
                      setSelected(null);
                      setModalMode(null);
                    }}
                    className="flex-1 rounded-xl border border-[#dedede] px-4 py-3 text-sm font-medium text-[#555] hover:bg-slate-50"
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="flex flex-1 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-4 py-3 text-sm font-semibold text-white hover:bg-[#a80b30]"
                  >
                    <Save size={16} /> Salvar alterações
                  </button>
                </div>
              </form>
            )}
          </section>
        </div>
      )}
    </AppLayout>
  );
}

type PaginationProps = {
  page: number;
  totalPages: number;
  total: number;
  onPageChange: (page: number) => void;
};

function Pagination({
  page,
  totalPages,
  total,
  onPageChange,
}: PaginationProps) {
  const start = total === 0 ? 0 : (page - 1) * 5 + 1;
  const end = Math.min(page * 5, total);

  const pages = Array.from({ length: totalPages }, (_, index) => index + 1);

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 border-t border-[#dedede] px-4 py-3 sm:px-5">
      <p className="text-[11px] text-[#888]">
        Mostrando {start}–{end} de {total}
      </p>

      <div className="flex items-center gap-1.5">
        <button
          type="button"
          aria-label="Página anterior"
          disabled={page <= 1}
          onClick={() => onPageChange(page - 1)}
          className="flex size-7 items-center justify-center rounded-lg border border-[#dedede] text-[#777] hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
        >
          <ChevronLeft size={15} />
        </button>

        {pages.map((item) => (
          <button
            key={item}
            type="button"
            aria-label={`Página ${item}`}
            aria-current={page === item ? "page" : undefined}
            onClick={() => onPageChange(item)}
            className={`flex size-7 items-center justify-center rounded-lg border text-xs transition ${
              page === item
                ? "border-[#bd0c36] bg-[#bd0c36] font-semibold text-white"
                : "border-[#dedede] text-[#666] hover:bg-slate-50"
            }`}
          >
            {item}
          </button>
        ))}

        <button
          type="button"
          aria-label="Próxima página"
          disabled={page >= totalPages}
          onClick={() => onPageChange(page + 1)}
          className="flex size-7 items-center justify-center rounded-lg border border-[#dedede] text-[#777] hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
        >
          <ChevronRight size={15} />
        </button>
      </div>
    </div>
  );
}