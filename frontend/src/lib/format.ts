
export function rotuloMes(anoMes: string): string {
  return new Date(`${anoMes}-01T00:00:00`).toLocaleDateString("pt-BR", {
    month: "short",
  });
}

export function ultimoDiaDoMes(anoMes: string): string {
  const [ano, mes] = anoMes.split("-").map(Number);
  const dia = new Date(ano, mes, 0).getDate();

  return `${anoMes}-${String(dia).padStart(2, "0")}`;
}

export function baixarCsv(
  nomeArquivo: string,
  linhas: (string | number)[][],
) {
  const texto = linhas
    .map((linha) =>
      linha.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(";"),
    )
    .join("\r\n");

  const blob = new Blob(["\uFEFF" + texto], {
    type: "text/csv;charset=utf-8;",
  });

  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");

  link.href = url;
  link.download = nomeArquivo;
  link.click();

  URL.revokeObjectURL(url);
}

export function formatCurrency(value: number): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value);
}

export function formatDate(value: string | Date): string {
  const date =
    typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value)
      ? new Date(`${value}T00:00:00`)
      : new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "—";
  }

  return new Intl.DateTimeFormat("pt-BR").format(date);
}

// "2026-11" -> "Novembro"
export function nomeMes(anoMes: string): string {
  const nome = new Date(`${anoMes}-01T00:00:00`).toLocaleDateString("pt-BR", { month: "long" });
  return nome.charAt(0).toUpperCase() + nome.slice(1);
}