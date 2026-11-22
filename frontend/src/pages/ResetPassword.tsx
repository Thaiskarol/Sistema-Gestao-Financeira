
import { useState, type FormEvent } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { apiPost } from "@/lib/api";

const input =
  "h-11 w-full rounded-xl border border-gray-300 px-4 text-base outline-none focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15";

export default function ResetPassword() {
  const [params] = useSearchParams();
  const token = params.get("token") ?? "";
  const navigate = useNavigate();
  const [senha, setSenha] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");

    if (senha.length < 8)
      return setErro("A senha deve ter pelo menos 8 caracteres.");

    if (senha !== confirmar)
      return setErro("As senhas não conferem.");

    setEnviando(true);

    try {
      await apiPost("/auth/redefinir-senha", { token, novaSenha: senha });
      window.alert("Senha redefinida com sucesso. Entre com a nova senha.");
      navigate("/", { replace: true });
    } catch (e) {
      setErro((e as Error).message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-white px-6">
      <div className="w-full max-w-md">
        <h1 className="text-2xl font-bold text-[#333]">Redefinir senha</h1>

        {!token ? (
          <p className="mt-4 rounded-lg bg-rose-50 p-3 text-sm text-[#A70D2C]">
            Link inválido ou expirado.{" "}
            <Link
              to="/esqueci-senha"
              className="font-semibold underline"
            >
              Solicite um novo
            </Link>
            .
          </p>
        ) : (
          <form onSubmit={handleSubmit} className="mt-6 space-y-4">
            <input
              className={input}
              type="password"
              placeholder="Nova senha"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
              minLength={8}
              maxLength={72}
            />

            <input
              className={input}
              type="password"
              placeholder="Confirmar nova senha"
              value={confirmar}
              onChange={(e) => setConfirmar(e.target.value)}
              required
            />

            {erro && (
              <p
                role="alert"
                className="rounded-lg bg-rose-50 p-3 text-sm text-[#A70D2C]"
              >
                {erro}
              </p>
            )}

            <button
              type="submit"
              disabled={enviando}
              className="h-11 w-full rounded-xl bg-[#C81035] text-sm font-semibold text-white hover:bg-[#AD0D2D]"
            >
              {enviando ? "Salvando..." : "Salvar nova senha"}
            </button>
          </form>
        )}

        <p className="mt-5 text-center text-sm">
          <Link to="/" className="text-[#C81035]">
            Voltar ao login
          </Link>
        </p>
      </div>
    </main>
  );
}