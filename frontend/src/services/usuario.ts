import { apiGet, apiPatch, apiPut } from "@/lib/api";
import { getUsuarioId } from "@/lib/session";

export type Perfil = {
  id: number;
  nome: string;
  email: string;
  dataNascimento: string;
  telefone: string | null;
};

export const getPerfil = () =>
  apiGet<Perfil>(`/usuarios/${getUsuarioId()}`);

export const atualizarPerfil = (d: {
  nome: string;
  email: string;
  telefone: string | null;
  dataNascimento: string;
}) => apiPut<Perfil>(`/usuarios/${getUsuarioId()}`, d);

export const alterarSenha = (senhaAtual: string, novaSenha: string) =>
  apiPatch(`/usuarios/${getUsuarioId()}/senha`, { senhaAtual, novaSenha });