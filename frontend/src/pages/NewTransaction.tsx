
import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Check, CalendarDays } from "lucide-react";
import { AppLayout } from "../layouts/AppLayout";

type TransactionType = "Despesa" | "Receita";

export default function NewTransaction() {
  const navigate = useNavigate();

  const [type, setType] = useState<TransactionType>("Despesa");
  const [description, setDescription] = useState("");
  const [value, setValue] = useState("");
  const [date, setDate] = useState(() =>
    new Date().toLocaleDateString("en-CA")
  );
  const [category, setCategory] = useState("Alimentação");
  const [establishment, setEstablishment] = useState("");
  const [recurrence, setRecurrence] = useState("Não se repete");
  const [notes, setNotes] = useState("");
  const [error, setError] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const amount = Number(value.replace(",", "."));

    if (!description.trim() || !value || !date || amount <= 0) {
      setError("Preencha a descrição, o valor e a data com informações válidas.");
      return;
    }

    const savedTransaction = {
      id: crypto.randomUUID(),
      type,
      description: description.trim(),
      value: amount,
      date,
      category,
      establishment: establishment.trim(),
      recurrence,
      notes: notes.trim(),
      createdAt: new Date().toISOString(),
    };

    try {
      const stored = localStorage.getItem("finance-transactions");
      const transactions = stored ? JSON.parse(stored) : [];

      if (!Array.isArray(transactions)) {
        throw new Error("Formato de dados inválido.");
      }

      localStorage.setItem(
        "finance-transactions",
        JSON.stringify([...transactions, savedTransaction])
      );

      navigate("/transacoes");
    } catch {
      setError("Não foi possível salvar a transação. Tente novamente.");
    }
  }

  const inputClass =
    "mt-2 min-h-11 w-full min-w-0 rounded-xl border border-[#d8d8d8] bg-white px-3.5 py-3 text-sm text-[#333] outline-none transition placeholder:text-[#a0a0a0] focus:border-[#a30f35] focus:ring-2 focus:ring-rose-100";

  const labelClass = "block text-sm font-medium text-[#333]";

  return (
    <AppLayout subtitle="Junho de 2025">
      <div className="mx-auto w-full max-w-[1100px] space-y-5">
        <section>
          <p className="mb-1 text-[11px] font-bold uppercase tracking-[0.16em] text-[#a30f35]">
            Visão financeira
          </p>
          <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-3xl">
            Nova transação
          </h1>
          <p className="mt-1 text-sm text-[#777]">
            Registre uma nova movimentação financeira.
          </p>
        </section>

        <form
          onSubmit={handleSubmit}
          className="rounded-xl border border-[#dedede] bg-white p-4 sm:p-6 lg:p-7"
        >
          <div
            className="mb-6 grid w-full max-w-[260px] grid-cols-2 rounded-xl bg-[#f5f5f5] p-1"
            aria-label="Tipo de transação"
          >
            {(["Despesa", "Receita"] as const).map((item) => (
              <button
                key={item}
                type="button"
                onClick={() => setType(item)}
                aria-pressed={type === item}
                className={`min-h-10 rounded-lg px-3 text-sm font-semibold transition ${
                  type === item
                    ? "bg-white text-[#8c092e] shadow-sm"
                    : "text-[#777] hover:text-[#333]"
                }`}
              >
                {item}
              </button>
            ))}
          </div>

          <div className="grid grid-cols-1 gap-x-5 gap-y-5 md:grid-cols-2">
            <label className={labelClass}>
              Descrição *
              <input
                className={inputClass}
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder="Ex.: Compra do mês"
                required
              />
            </label>

            <label className={labelClass}>
              Valor *
              <input
                className={inputClass}
                type="number"
                min="0.01"
                step="0.01"
                value={value}
                onChange={(event) => setValue(event.target.value)}
                placeholder="R$ 0,00"
                required
              />
            </label>

            <label className={labelClass}>
              Data *
              <span className="relative block">
                <CalendarDays
                  size={17}
                  className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-[#888]"
                />
                <input
                  className={`${inputClass} pl-10`}
                  type="date"
                  value={date}
                  onChange={(event) => setDate(event.target.value)}
                  required
                />
              </span>
            </label>

            <label className={labelClass}>
              Categoria
              <select
                className={inputClass}
                value={category}
                onChange={(event) => setCategory(event.target.value)}
              >
                <option>Alimentação</option>
                <option>Moradia</option>
                <option>Transporte</option>
                <option>Lazer</option>
                <option>Saúde</option>
                <option>Educação</option>
                <option>Assinaturas</option>
                <option>Salário</option>
                <option>Investimentos</option>
                <option>Outros</option>
              </select>
            </label>

            <label className={labelClass}>
              Estabelecimento
              <input
                className={inputClass}
                value={establishment}
                onChange={(event) => setEstablishment(event.target.value)}
                placeholder="Opcional"
              />
            </label>

            <label className={labelClass}>
              Recorrência
              <select
                className={inputClass}
                value={recurrence}
                onChange={(event) => setRecurrence(event.target.value)}
              >
                <option>Não se repete</option>
                <option>Diária</option>
                <option>Semanal</option>
                <option>Mensal</option>
                <option>Anual</option>
              </select>
            </label>

            <label className={`${labelClass} md:col-span-2`}>
              Observações
              <textarea
                className={`${inputClass} min-h-24 resize-y`}
                value={notes}
                onChange={(event) => setNotes(event.target.value)}
                placeholder="Adicione uma observação (opcional)"
                rows={3}
              />
            </label>
          </div>

          {error && (
            <p
              role="alert"
              className="mt-4 rounded-lg bg-rose-50 p-3 text-sm text-[#a30f35]"
            >
              {error}
            </p>
          )}

          <div className="mt-6 flex flex-col-reverse gap-3 border-t border-[#dedede] pt-5 sm:flex-row">
            <button
              type="button"
              onClick={() => navigate("/dashboard")}
              className="min-h-11 flex-1 rounded-xl bg-[#fbedf0] px-4 py-3 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f8dfe5]"
            >
              Cancelar
            </button>

            <button
              type="submit"
              className="inline-flex min-h-11 items-center justify-center gap-2 rounded-xl bg-[#bd0c36] px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a20b2e] sm:min-w-[180px]"
            >
              <Check size={17} />
              Salvar transação
            </button>
          </div>
        </form>
      </div>
    </AppLayout>
  );
}