import { Link } from "react-router-dom";
import { useState, type FormEvent } from "react";
import {
  ArrowRight,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  ShieldCheck,
} from "lucide-react";

export function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [message, setMessage] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage("A autenticação será habilitada quando conectarmos o backend.");
  }

  return (
    <main className="min-h-screen bg-white lg:grid lg:grid-cols-2">
      {/* Painel institucional: aparece em telas grandes */}
      <section className="relative hidden min-h-screen flex-col overflow-hidden bg-[#C81035] px-8 py-6 text-white lg:flex xl:px-12">
        {/* Elementos decorativos */}
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -right-32 top-24 size-80 rounded-full border border-white/10"
        />
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -bottom-48 -left-24 size-136 rounded-full border border-white/10"
        />
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -bottom-32 -left-20 size-108 rounded-full bg-white/3"
        />

        {/* Logo */}
        <div className="relative z-10 flex items-center gap-3">
          <img
            src="/logo.png"
            alt="Logo Gestão Financeira"
            className="size-11 object-contain"
          />

          <div>
            <p className="text-xl font-extrabold tracking-wide">FINANÇAS</p>
            <p className="text-xs text-white/75">
              Sua vida financeira em equilíbrio
            </p>
          </div>
        </div>

        {/* Mensagem principal */}
        <div className="relative z-10 my-auto max-w-xl py-8">
          <p className="mb-4 text-sm font-bold tracking-[0.18em] text-white/75">
            CONTROLE, CLAREZA E CONFIANÇA
          </p>

          <h1 className="text-3xl font-bold leading-tight xl:text-4xl">
            Sua vida financeira em equilíbrio
          </h1>

          <p className="mt-4 max-w-md text-base leading-relaxed text-white/85">
            Organize suas escolhas e acompanhe seu futuro com inteligência.
          </p>
        </div>

        {/* Segurança */}
        <div className="relative z-10 flex items-center gap-3 text-sm text-white/80">
          <ShieldCheck size={19} />
          <span>Seus dados protegidos com segurança</span>
        </div>
      </section>

      {/* Área do formulário */}
      <section className="flex min-h-screen flex-col px-6 py-5 sm:px-10 sm:py-8 lg:justify-center lg:px-10 lg:py-4 xl:px-16">
        {/* Marca mobile */}
        <div className="mb-7 flex items-center gap-3 lg:hidden">
          <div className="flex size-11 shrink-0 items-center justify-center rounded-xl bg-[#C81035]">
            <div className="flex flex-col gap-1">
              <span className="h-1 w-5 rounded-full bg-white" />
              <span className="h-1 w-4 rounded-full bg-white" />
              <span className="h-1 w-3 rounded-full bg-white" />
            </div>
          </div>

          <div>
            <p className="text-xl font-extrabold tracking-wide text-[#C81035]">
              FINANÇAS
            </p>
            <p className="text-xs text-gray-500">
              Sua vida financeira em equilíbrio
            </p>
          </div>
        </div>

        <div className="mx-auto flex w-full max-w-md flex-1 flex-col justify-center lg:flex-none">
          {/* Título */}
          <header className="mb-6">
            <h2 className="text-2xl font-bold tracking-tight text-[#333333] sm:text-3xl">
              Bem-vinda de volta
            </h2>

            <p className="mt-2 text-sm leading-relaxed text-gray-500 sm:text-base">
              Acesse sua conta para cuidar do seu dinheiro.
            </p>
          </header>

          {/* Formulário */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label
                htmlFor="email"
                className="mb-2 block text-sm font-medium text-gray-800"
              >
                E-mail
              </label>

              <div className="relative">
                <Mail
                  aria-hidden="true"
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
                />

                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  placeholder="seu@email.com"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    setMessage("");
                  }}
                  required
                  className="h-11 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-4 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="password"
                className="mb-2 block text-sm font-medium text-gray-800"
              >
                Senha
              </label>

              <div className="relative">
                <LockKeyhole
                  aria-hidden="true"
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
                />

                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="current-password"
                  placeholder="Digite sua senha"
                  value={password}
                  onChange={(event) => {
                    setPassword(event.target.value);
                    setMessage("");
                  }}
                  required
                  className="h-11 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-12 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                  aria-pressed={showPassword}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 rounded-md p-1 text-gray-500 hover:text-gray-800 focus-visible:outline-2 focus-visible:outline-[#C81035]"
                >
                  {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
                </button>
              </div>
            </div>

            {/* Opções adicionais: ocultas no mobile */}
            <div className="hidden items-center justify-between gap-3 sm:flex">
              <label className="flex cursor-pointer items-center gap-2 text-sm text-gray-500">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(event) => setRememberMe(event.target.checked)}
                  className="size-4 accent-[#C81035]"
                />
                Lembrar de mim
              </label>

              <Link
                to="/esqueci-senha"
                className="text-sm font-semibold text-[#A70D2C] hover:underline"
              >
                Esqueci minha senha
              </Link>
            </div>

            {/* Recuperação visível no mobile */}
            <div className="-mt-1 flex justify-end sm:hidden">
              <Link
                to="/esqueci-senha"
                className="text-sm font-semibold text-[#A70D2C] hover:underline"
              >
                Esqueci minha senha
              </Link>
            </div>

            <button
              type="submit"
              className="flex h-11 w-full items-center justify-center gap-2 rounded-xl bg-[#C81035] px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-[#AD0D2D] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#C81035] active:scale-[0.99]"
            >
              Entrar
              <ArrowRight size={18} />
            </button>
          </form>

          {/* Mensagem enquanto a API não está conectada */}
          {message && (
            <p
              role="status"
              aria-live="polite"
              className="mt-3 rounded-lg bg-rose-50 px-4 py-3 text-sm leading-relaxed text-[#A70D2C]"
            >
              {message}
            </p>
          )}

          {/* Separador */}
          <div className="my-4 flex items-center gap-4">
            <span className="h-px flex-1 bg-gray-200" />
            <span className="text-sm text-gray-500">ou</span>
            <span className="h-px flex-1 bg-gray-200" />
          </div>

          {/* Cadastro */}
          <Link
            to="/cadastro"
            className="flex h-11 w-full items-center justify-center rounded-xl bg-[#FCECEF] px-4 text-sm font-semibold text-[#AD0D2D] transition hover:bg-[#F8DDE3] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#C81035]"
          >
            Criar conta
          </Link>

          {/* Termos: uma linha no desktop */}
          <p className="mt-4 text-center text-[11px] leading-relaxed text-gray-500 sm:text-xs lg:whitespace-nowrap">
            Ao continuar, você concorda com nossos Termos e Política de
            Privacidade.
          </p>
        </div>
      </section>
    </main>
  );
}

export default Login;
