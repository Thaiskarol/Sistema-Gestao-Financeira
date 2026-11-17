
import { apiDelete, apiGet, apiPost, apiPut } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type {
  Categoria,
  TipoTransacao,
  Transacao,
} from "@/types/api";

export const getTransacoes = () =>
  apiGet<Transacao[]>("/transacoes", {
    usuarioId: getUsuarioId(),
  });

export const getCategorias = () =>
  apiGet<Categoria[]>("/categorias");

export type NovaTransacao = {
  descricao: string;
  estabelecimento: string | null;
  valor: number;
  dataTransacao: string;
  recorrencia: string | null;
  tipo: TipoTransacao;
  categoriaId: number;
};

export const criarTransacao = (dados: NovaTransacao) =>
  apiPost<Transacao>("/transacoes", {
    ...dados,
    usuarioId: getUsuarioId(),
  });

export const atualizarTransacao = (
  id: number,
  dados: {
    descricao: string;
    recorrencia: string | null;
    categoriaId: number;
  },
) =>
  apiPut<Transacao>(`/transacoes/${id}`, {
    ...dados,
    usuarioId: getUsuarioId(),
  });

export const excluirTransacao = (id: number) =>
  apiDelete(`/transacoes/${id}`, {
    usuarioId: getUsuarioId(),
  });
