import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { ClassificacaoScore, Score } from "@/types/api";

const u = () => ({ usuarioId: getUsuarioId() });

export const getScore = () => apiGet<Score>("/score", u());
export const getClassificacao = () => apiGet<ClassificacaoScore>("/score/classificacao", u());