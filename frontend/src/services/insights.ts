import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";

export type InsightApi = {
  tipo: string;
  categoria: string | null;
  percentual: number | null;
  mensagem: string;
};

export const getInsights = () =>
  apiGet<InsightApi[]>("/insights", {
    usuarioId: getUsuarioId(),
  });