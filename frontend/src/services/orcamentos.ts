import { apiDelete, apiGet, apiPost, apiPut } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";

export type OrcamentoDetalhe = {
  id: number;
  categoriaId: number;
  categoriaNome: string;
  limite: number;
  valorConsumido: number;
  percentualUtilizado: number;
  status: string;
};

export const getOrcamentos = (mes: string) =>
  apiGet<OrcamentoDetalhe[]>("/orcamentos", { usuarioId: getUsuarioId(), mes });

export const criarOrcamento = (categoriaId: number, limite: number) =>
  apiPost("/orcamentos", { limite, categoriaId, usuarioId: getUsuarioId() });

export const atualizarOrcamento = (id: number, limite: number) =>
  apiPut(`/orcamentos/${id}`, { limite, usuarioId: getUsuarioId() });

export const excluirOrcamento = (id: number) =>
  apiDelete(`/orcamentos/${id}`, { usuarioId: getUsuarioId() });