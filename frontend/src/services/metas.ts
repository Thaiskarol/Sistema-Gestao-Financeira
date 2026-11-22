import { apiDelete, apiGet, apiPost } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { Meta } from "@/types/api";

export const getMetas = () =>
  apiGet<Meta[]>("/metas", {
    usuarioId: getUsuarioId(),
  });

export const criarMeta = (dados: {
  nome: string;
  valorObjetivo: number;
  dataDesejada: string;
  valorAcumulado: number;
}) =>
  apiPost<Meta>("/metas", {
    ...dados,
    usuarioId: getUsuarioId(),
  });
export const excluirMeta = (id: number) =>
  apiDelete(`/metas/${id}`, { usuarioId: getUsuarioId() });