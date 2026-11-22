import { apiDelete, apiPost, apiPut } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { Categoria } from "@/types/api";

export const criarCategoria = (nome: string) =>
  apiPost<Categoria>("/categorias", { nome, usuarioId: getUsuarioId() });

export const atualizarCategoria = (id: number, nome: string) =>
  apiPut<Categoria>(`/categorias/${id}`, { nome, usuarioId: getUsuarioId() });

export const excluirCategoria = (id: number) =>
  apiDelete(`/categorias/${id}`, { usuarioId: getUsuarioId() });