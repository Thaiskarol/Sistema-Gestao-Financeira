
const KEY = "financialUser";

export type UsuarioLogado = {
  id: number;
  nome: string;
  email: string;
};

export function saveUsuario(usuario: UsuarioLogado) {
  localStorage.setItem(KEY, JSON.stringify(usuario));
}

export function getUsuario(): UsuarioLogado | null {
  const raw = localStorage.getItem(KEY);
  return raw ? (JSON.parse(raw) as UsuarioLogado) : null;
}

export function getUsuarioId(): number {
  const usuario = getUsuario();

  if (usuario) return usuario.id;

  const teste = Number(import.meta.env.VITE_USUARIO_ID_TESTE);

  if (teste) return teste; // temporário, até o login real ficar pronto

  throw new Error("Usuário não logado.");
}

export function logout() {
  localStorage.removeItem(KEY);
}
