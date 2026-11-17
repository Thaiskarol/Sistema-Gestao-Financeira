import { apiGet } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";
import type { EvolucaoPatrimonial, ReservaEmergencia } from "@/types/api";

const u = () => ({ usuarioId: getUsuarioId() });

export const getEvolucaoPatrimonial = () =>
  apiGet<EvolucaoPatrimonial[]>("/patrimonio/evolucao", u());

export const getReservaEmergencia = () =>
  apiGet<ReservaEmergencia>("/recomendacoes/reserva-emergencia", u());