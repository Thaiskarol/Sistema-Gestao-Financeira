
import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type {
  GrupoValor,
  ResumoFinanceiro,
  ValorMensal,
} from "@/types/api";

const u = () => ({
  usuarioId: getUsuarioId(),
});

export const getResumo = () =>
  apiGet<ResumoFinanceiro>("/consulta/resumo", u());

export const getEvolucaoReceitas = () =>
  apiGet<ValorMensal[]>("/evolucao/receitas", u());

export const getEvolucaoDespesas = () =>
  apiGet<ValorMensal[]>("/evolucao/despesas", u());

export const getGastosPorCategoria = () =>
  apiGet<GrupoValor[]>("/gastos/por-categoria", u());
