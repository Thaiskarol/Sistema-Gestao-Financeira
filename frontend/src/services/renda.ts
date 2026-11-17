import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { DistribuicaoFinanceira, RendaItem } from "@/types/api";

const u = () => ({ usuarioId: getUsuarioId() });

export const getDistribuicao = () =>
  apiGet<DistribuicaoFinanceira>("/distribuicao-financeira", u());

// Só funciona depois que o backend liberar o item 4 (listar rendas)
export const getRendas = () => apiGet<RendaItem[]>("/rendas", u());