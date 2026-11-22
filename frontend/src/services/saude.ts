import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { ClassificacaoScore, RecomendacaoGeral, Score } from "@/types/api";

const u = () => ({ usuarioId: getUsuarioId() });

export const getScore = () => apiGet<Score>("/score", u());
export const getClassificacao = () => apiGet<ClassificacaoScore>("/score/classificacao", u());

export type ScoreHistorico = {
  mes: string;
  score: number;
  nivel: string;
  nivelDescricao: string;
};

export const getHistoricoScore = () =>
  apiGet<ScoreHistorico[]>("/score/historico", u());

export const getRecomendacoes = () =>
  apiGet<RecomendacaoGeral>("/recomendacoes/geral", u());