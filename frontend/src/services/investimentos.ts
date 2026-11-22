
import { apiDelete, apiGet, apiPost } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { EvolucaoPatrimonial, ReservaEmergencia } from "@/types/api";

const u = () => ({ usuarioId: getUsuarioId() });

export type TipoInvestimento =
  | "RENDA_FIXA"
  | "FUNDOS"
  | "ACOES"
  | "CRIPTOATIVOS"
  | "OUTROS";

export type Investimento = {
  id: number;
  nome: string;
  tipo: TipoInvestimento;
  tipoDescricao: string;
  valor: number;
  dataRegistro: string;
};

export type Carteira = {
  itens: {
    tipo: TipoInvestimento;
    nome: string;
    valor: number;
    percentual: number;
  }[];
  total: number;
};

export const getInvestimentos = () =>
  apiGet<Investimento[]>("/investimentos", u());

export const getCarteira = () =>
  apiGet<Carteira>("/investimentos/carteira", u());

export const registrarInvestimento = (d: {
  nome: string;
  tipo: TipoInvestimento;
  valor: number;
}) =>
  apiPost<Investimento>("/investimentos", {
    ...d,
    usuarioId: getUsuarioId(),
  });

export const excluirInvestimento = (id: number) =>
  apiDelete(`/investimentos/${id}`, u());

export const getEvolucaoPatrimonial = () =>
  apiGet<EvolucaoPatrimonial[]>("/patrimonio/evolucao", u());

export const getReservaEmergencia = () =>
  apiGet<ReservaEmergencia>("/recomendacoes/reserva-emergencia", u());