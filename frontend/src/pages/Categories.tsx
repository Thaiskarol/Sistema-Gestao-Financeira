import { useMemo, useState, type FormEvent } from "react";
import {
  Plus,
  Tag,
  Pencil,
  Trash2,
  X,
  Search,
  Utensils,
  House,
  CarFront,
  HeartPulse,
  GraduationCap,
  Gamepad2,
  ShoppingBag,
  TrendingUp,
  Receipt,
  Wrench,
  CircleEllipsis,
  Save,
} from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";

type Category = {
  id: number;
  name: string;
  transactions: number;
  amount: number;
  color: string;
  type: "Despesa" | "Receita";
};

const initialCategories: Category[] = [
  { id: 1, name: "Alimentação", transactions: 18, amount: 1210.4, color: "#970b2e", type: "Despesa" },
  { id: 2, name: "Moradia", transactions: 5, amount: 1820, color: "#19435d", type: "Despesa" },
  { id: 3, name: "Transporte", transactions: 9, amount: 780.25, color: "#e9ad00", type: "Despesa" },
  { id: 4, name: "Saúde", transactions: 3, amount: 340, color: "#30885c", type: "Despesa" },
  { id: 5, name: "Educação", transactions: 2, amount: 290, color: "#19435d", type: "Despesa" },
  { id: 6, name: "Lazer", transactions: 7, amount: 520.8, color: "#970b2e", type: "Despesa" },
  { id: 7, name: "Compras", transactions: 4, amount: 410, color: "#e9ad00", type: "Despesa" },
  { id: 8, name: "Investimentos", transactions: 3, amount: 850, color: "#19435d", type: "Receita" },
  { id: 9, name: "Assinaturas", transactions: 6, amount: 186.9, color: "#333333", type: "Despesa" },
  { id: 10, name: "Impostos", transactions: 2, amount: 630, color: "#970b2e", type: "Despesa" },
  { id: 11, name: "Contas e Serviços", transactions: 8, amount: 760, color: "#30885c", type: "Despesa" },
  { id: 12, name: "Outros", transactions: 3, amount: 150, color: "#333333", type: "Despesa" },
];

const colorOptions = [
  "#970b2e",
  "#19435d",
  "#e9ad00",
  "#30885c",
  "#333333",
  "#bd0c36",
];

const iconForCategory = (name: string) => {
  const normalized = name.toLowerCase();

  if (normalized.includes("alimenta")) return Utensils;
  if (normalized.includes("moradia")) return House;
  if (normalized.includes("transport")) return CarFront;
  if (normalized.includes("saúde") || normalized.includes("saude")) return HeartPulse;
  if (normalized.includes("educa")) return GraduationCap;
  if (normalized.includes("lazer")) return Gamepad2;
  if (normalized.includes("compra")) return ShoppingBag;
  if (normalized.includes("invest")) return TrendingUp;
  if (normalized.includes("imposto")) return Receipt;
  if (normalized.includes("serviço") || normalized.includes("servico")) return Wrench;
  if (normalized.includes("assinatura")) return Tag;

  return CircleEllipsis;
};

const currency = (value: number) =>
  value.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });

export default function Categories() {
  const [categories, setCategories] = useState<Category[]>(initialCategories);
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("Todas");
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [name, setName] = useState("");
  const [type, setType] = useState<"Despesa" | "Receita">("Despesa");
  const [color, setColor] = useState(colorOptions[0]);
  const [error, setError] = useState("");

  const filteredCategories = useMemo(
    () =>
      categories.filter((category) => {
        const matchesSearch = category.name
          .toLowerCase()
          .includes(search.trim().toLowerCase());

        const matchesType =
          typeFilter === "Todas" || category.type === typeFilter;

        return matchesSearch && matchesType;
      }),
    [categories, search, typeFilter]
  );

  function openNewCategory() {
    setEditingId(null);
    setName("");
    setType("Despesa");
    setColor(colorOptions[0]);
    setError("");
    setModalOpen(true);
  }

  function openEditCategory(category: Category) {
    setEditingId(category.id);
    setName(category.name);
    setType(category.type);
    setColor(category.color);
    setError("");
    setModalOpen(true);
  }

  function saveCategory(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const cleanName = name.trim();

    if (!cleanName) {
      setError("Informe o nome da categoria.");
      return;
    }

    const duplicate = categories.some(
      (category) =>
        category.name.toLowerCase() === cleanName.toLowerCase() &&
        category.id !== editingId
    );

    if (duplicate) {
      setError("Já existe uma categoria com esse nome.");
      return;
    }

    if (editingId !== null) {
      setCategories((current) =>
        current.map((category) =>
          category.id === editingId
            ? { ...category, name: cleanName, type, color }
            : category
        )
      );
    } else {
      setCategories((current) => [
        ...current,
        {
          id: Date.now(),
          name: cleanName,
          transactions: 0,
          amount: 0,
          color,
          type,
        },
      ]);
    }

    setModalOpen(false);
  }

  function deleteCategory(category: Category) {
    const confirmed = window.confirm(
      `Deseja excluir a categoria "${category.name}"?`
    );

    if (!confirmed) return;

    setCategories((current) =>
      current.filter((item) => item.id !== category.id)
    );
  }

  return (
    <AppLayout subtitle="Junho de 2025">
      <section className="space-y-6">
        {/* Título e ação principal */}
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="text-[10px] font-bold uppercase tracking-[0.16em] text-[#970b2e]">
              Visão financeira
            </p>
            <h1 className="mt-1 text-2xl font-bold tracking-tight text-[#292929] sm:text-[28px]">
              Categorias
            </h1>
            <p className="mt-1 text-sm text-[#777]">
              Organize suas despesas e receitas do seu jeito.
            </p>
          </div>

          <button
            type="button"
            onClick={openNewCategory}
            className="inline-flex min-h-11 items-center justify-center gap-2 self-start rounded-xl bg-[#bd0c36] px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a80b30] focus:outline-none focus:ring-2 focus:ring-[#bd0c36]/30 sm:self-auto"
          >
            <Plus size={18} />
            Adicionar categoria
          </button>
        </div>

        {/* Busca e filtro */}
        <div className="flex flex-col gap-3 rounded-2xl border border-[#dedede] bg-white p-3 sm:flex-row sm:items-center">
          <div className="flex h-10 min-w-0 flex-1 items-center gap-2 rounded-xl border border-[#dedede] px-3">
            <Search size={17} className="shrink-0 text-[#888]" />
            <input
              type="search"
              aria-label="Buscar categoria"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Buscar categoria..."
              className="w-full min-w-0 bg-transparent text-sm outline-none placeholder:text-[#aaa]"
            />
          </div>

          <select
            aria-label="Filtrar tipo de categoria"
            value={typeFilter}
            onChange={(event) => setTypeFilter(event.target.value)}
            className="h-10 rounded-xl border border-[#dedede] bg-white px-3 text-sm text-[#555] outline-none focus:border-[#bd0c36] sm:w-48"
          >
            <option value="Todas">Todas as categorias</option>
            <option value="Despesa">Despesas</option>
            <option value="Receita">Receitas</option>
          </select>
        </div>

        {/* Grade de categorias */}
        {filteredCategories.length > 0 ? (
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:gap-3.5">
            {filteredCategories.map((category) => {
              const Icon = iconForCategory(category.name);

              return (
                <article
                  key={category.id}
                  className="flex min-h-[90px] min-w-0 items-center gap-3 rounded-2xl border border-[#dedede] bg-white p-3.5 shadow-[0_1px_2px_rgba(0,0,0,0.02)] transition hover:border-[#d2c6c9] sm:gap-3.5 sm:px-4"
                >
                  <span
                    className="flex size-10 shrink-0 items-center justify-center rounded-xl text-white sm:size-[42px]"
                    style={{ backgroundColor: category.color }}
                  >
                    <Icon size={19} strokeWidth={1.7} />
                  </span>

                  <div className="min-w-0 flex-1">
                    <h2 className="break-words text-sm font-semibold text-[#333]">
                      {category.name}
                    </h2>
                    <p className="mt-1 text-xs text-[#888]">
                      {category.transactions}{" "}
                      {category.transactions === 1
                        ? "transação"
                        : "transações"}
                    </p>
                  </div>

                  <p className="shrink-0 text-right text-xs font-bold text-[#333] sm:text-sm">
                    {currency(category.amount)}
                  </p>

                  <div className="flex shrink-0 items-center gap-0.5 sm:gap-1">
                    <button
                      type="button"
                      aria-label={`Editar ${category.name}`}
                      title="Editar categoria"
                      onClick={() => openEditCategory(category)}
                      className="rounded-lg p-2 text-[#777] transition hover:bg-[#fbedf0] hover:text-[#bd0c36]"
                    >
                      <Pencil size={16} strokeWidth={1.6} />
                    </button>
                    <button
                      type="button"
                      aria-label={`Excluir ${category.name}`}
                      title="Excluir categoria"
                      onClick={() => deleteCategory(category)}
                      className="rounded-lg p-2 text-[#777] transition hover:bg-red-50 hover:text-red-600"
                    >
                      <Trash2 size={16} strokeWidth={1.6} />
                    </button>
                  </div>
                </article>
              );
            })}
          </div>
        ) : (
          <div className="rounded-2xl border border-[#dedede] bg-white px-4 py-14 text-center">
            <Tag size={30} className="mx-auto text-[#aaa]" />
            <p className="mt-3 text-sm font-semibold text-[#444]">
              Nenhuma categoria encontrada
            </p>
            <p className="mt-1 text-xs text-[#888]">
              Tente mudar a busca ou adicionar uma nova categoria.
            </p>
            <button
              type="button"
              onClick={openNewCategory}
              className="mt-4 rounded-xl bg-[#bd0c36] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#a80b30]"
            >
              Adicionar categoria
            </button>
          </div>
        )}

        <p className="text-xs text-[#888]">
          Exibindo {filteredCategories.length}{" "}
          {filteredCategories.length === 1 ? "categoria" : "categorias"}
        </p>
      </section>

      {/* Modal para adicionar e editar */}
      {modalOpen && (
        <div
          className="fixed inset-0 z-[60] flex items-center justify-center overflow-y-auto bg-black/40 p-4"
          onClick={(event) => {
            if (event.target === event.currentTarget) setModalOpen(false);
          }}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="category-modal-title"
            className="my-auto w-full max-w-md rounded-2xl bg-white p-5 shadow-xl sm:p-6"
          >
            <div className="mb-5 flex items-center justify-between gap-3">
              <div>
                <h2
                  id="category-modal-title"
                  className="text-lg font-semibold text-[#292929]"
                >
                  {editingId === null
                    ? "Adicionar categoria"
                    : "Editar categoria"}
                </h2>
                <p className="mt-1 text-xs text-[#888]">
                  Personalize a identificação da categoria.
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

            <form onSubmit={saveCategory} className="space-y-5">
              <div>
                <label
                  htmlFor="category-name"
                  className="mb-1.5 block text-xs font-medium text-[#555]"
                >
                  Nome da categoria
                </label>
                <input
                  id="category-name"
                  autoFocus
                  required
                  maxLength={50}
                  value={name}
                  onChange={(event) => {
                    setName(event.target.value);
                    setError("");
                  }}
                  placeholder="Ex.: Alimentação"
                  className="w-full rounded-xl border border-[#dedede] px-3 py-3 text-sm outline-none focus:border-[#bd0c36]"
                />
              </div>

              <div>
                <label
                  htmlFor="category-type"
                  className="mb-1.5 block text-xs font-medium text-[#555]"
                >
                  Tipo
                </label>
                <select
                  id="category-type"
                  value={type}
                  onChange={(event) =>
                    setType(event.target.value as "Despesa" | "Receita")
                  }
                  className="w-full rounded-xl border border-[#dedede] bg-white px-3 py-3 text-sm outline-none focus:border-[#bd0c36]"
                >
                  <option value="Despesa">Despesa</option>
                  <option value="Receita">Receita</option>
                </select>
              </div>

              <fieldset>
                <legend className="mb-2 text-xs font-medium text-[#555]">
                  Cor da categoria
                </legend>
                <div className="flex flex-wrap gap-3">
                  {colorOptions.map((option) => (
                    <button
                      key={option}
                      type="button"
                      aria-label={`Selecionar cor ${option}`}
                      aria-pressed={color === option}
                      onClick={() => setColor(option)}
                      className={`flex size-9 items-center justify-center rounded-full transition ${
                        color === option
                          ? "ring-2 ring-[#bd0c36] ring-offset-2"
                          : "hover:scale-105"
                      }`}
                      style={{ backgroundColor: option }}
                    >
                      {color === option && (
                        <span className="text-sm font-bold text-white">✓</span>
                      )}
                    </button>
                  ))}
                </div>
              </fieldset>

              {error && (
                <p role="alert" className="text-xs text-red-600">
                  {error}
                </p>
              )}

              <div className="flex flex-col-reverse gap-2 pt-1 sm:flex-row">
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
                  Salvar categoria
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </AppLayout>
  );
}