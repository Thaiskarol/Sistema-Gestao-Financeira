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

  if (!usuario) {
    throw new Error("Usuário não logado.");
  }

  return usuario.id;
}

export function logout() {
  localStorage.removeItem(KEY);
}