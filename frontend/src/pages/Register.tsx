import { Link } from "react-router-dom";
import { useState, type FormEvent } from "react";
import {
  ArrowRight,
  ArrowLeft,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  ShieldCheck,
  UserRound,
} from "lucide-react";

export function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState<"error" | "success">("error");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMessage("");

    if (name.trim().split(/\s+/).length < 2) {
      setMessage("Informe seu nome e sobrenome.");
      setMessageType("error");
      return;
    }

    if (password.length < 8) {
      setMessage("A senha deve ter pelo menos 8 caracteres.");
      setMessageType("error");
      return;
    }

    if (password !== confirmPassword) {
      setMessage("As senhas não coincidem. Confira os campos.");
      setMessageType("error");
      return;
    }

    if (!acceptedTerms) {
      setMessage("Você precisa aceitar os Termos e a Política de Privacidade.");
      setMessageType("error");
      return;
    }

    setMessage(
      "Validação concluída! O cadastro será habilitado quando conectarmos o backend.",
    );
    setMessageType("success");
  }

  return (
    <main className="min-h-screen bg-white lg:grid lg:grid-cols-2">
      {/* Painel institucional para telas grandes */}
      <section className="relative hidden min-h-screen flex-col overflow-hidden bg-[#C81035] px-8 py-6 text-white lg:flex xl:px-12">
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -right-32 top-24 size-80 rounded-full border border-white/10"
        />
        <div
          aria-hidden="true"
          className="pointer-events-none absolute -bottom-48 -left-24 size-136 rounded-full border border-white/10"
        />

        {/* Logo: clicar leva para o login */}
        <Link
          to="/"
          className="relative z-10 flex w-fit items-center gap-3"
          aria-label="Ir para a tela de login"
        >
          <img
            src="/logo.png"
            alt="Logo Gestão Financeira"
            className="size-11 object-contain"
          />

          <div>
            <p className="text-xl font-extrabold tracking-wide"> FINANÇAS </p>
            <p className="text-xs text-white/75">
              {" "}
              Sua vida financeira em equilíbrio{" "}
            </p>
          </div>
        </Link>

        <div className="relative z-10 my-auto max-w-xl py-8">
          <p className="mb-4 text-sm font-bold tracking-[0.18em] text-white/75">
            SEU FUTURO COMEÇA AQUI
          </p>

          <h1 className="text-3xl font-bold leading-tight xl:text-4xl">
            Organize hoje. Conquiste amanhã.
          </h1>

          <p className="mt-4 max-w-md text-base leading-relaxed text-white/85">
            Crie sua conta e comece a acompanhar suas receitas, despesas e metas
            financeiras em um só lugar.
          </p>
        </div>

        <div className="relative z-10 flex items-center gap-3 text-sm text-white/80">
          <ShieldCheck size={19} />
          <span>Suas finanças, organizadas em um só lugar</span>
        </div>
      </section>

      {/* Formulário de cadastro */}
      <section className="flex min-h-screen flex-col px-6 py-5 sm:px-10 sm:py-8 lg:justify-center lg:px-10 lg:py-3 xl:px-14">
        {/* Marca para dispositivos móveis */}
        <div className="mb-6 flex items-center gap-3 lg:hidden">
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
          {/* Voltar ao login */}
          <Link
            to="/"
            className="mb-4 flex w-fit items-center gap-2 text-sm font-medium text-gray-500 transition hover:text-[#C81035]"
          >
            <ArrowLeft size={18} />
            Voltar ao login
          </Link>

          {/* Título */}
          <header className="mb-5">
            <h2 className="text-2xl font-bold tracking-tight text-[#333333] sm:text-3xl">
              Crie sua conta
            </h2>

            <p className="mt-1 text-sm leading-relaxed text-gray-500 sm:text-base">
              Comece a cuidar melhor do seu dinheiro.
            </p>
          </header>

          <form onSubmit={handleSubmit} className="space-y-3">
            {/* Nome */}
            <div>
              <label
                htmlFor="register-name"
                className="mb-1.5 block text-sm font-medium text-gray-800"
              >
                Nome completo
              </label>

              <div className="relative">
                <UserRound
                  aria-hidden="true"
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
                />

                <input
                  id="register-name"
                  name="name"
                  type="text"
                  autoComplete="name"
                  placeholder="Digite seu nome completo"
                  value={name}
                  onChange={(event) => setName(event.target.value)}
                  required
                  className="h-10 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-4 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />
              </div>
            </div>

            {/* E-mail */}
            <div>
              <label
                htmlFor="register-email"
                className="mb-1.5 block text-sm font-medium text-gray-800"
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
                  id="register-email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  placeholder="seu@email.com"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  required
                  className="h-10 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-4 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />
              </div>
            </div>

            {/* Senha */}
            <div>
              <label
                htmlFor="register-password"
                className="mb-1.5 block text-sm font-medium text-gray-800"
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
                  id="register-password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  autoComplete="new-password"
                  placeholder="Mínimo de 8 caracteres"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  minLength={8}
                  required
                  className="h-10 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-12 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />

                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}
                  aria-pressed={showPassword}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 rounded-md p-1 text-gray-500 hover:text-gray-800"
                >
                  {showPassword ? <EyeOff size={19} /> : <Eye size={19} />}
                </button>
              </div>
            </div>

            {/* Confirmação de senha */}
            <div>
              <label
                htmlFor="register-confirm-password"
                className="mb-1.5 block text-sm font-medium text-gray-800"
              >
                Confirmar senha
              </label>

              <div className="relative">
                <LockKeyhole
                  aria-hidden="true"
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
                />

                <input
                  id="register-confirm-password"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  autoComplete="new-password"
                  placeholder="Digite a senha novamente"
                  value={confirmPassword}
                  onChange={(event) => setConfirmPassword(event.target.value)}
                  required
                  className="h-10 w-full rounded-xl border border-gray-300 bg-white pl-11 pr-12 text-base text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-[#C81035] focus:ring-2 focus:ring-[#C81035]/15"
                />

                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  aria-label={
                    showConfirmPassword
                      ? "Ocultar confirmação da senha"
                      : "Mostrar confirmação da senha"
                  }
                  aria-pressed={showConfirmPassword}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 rounded-md p-1 text-gray-500 hover:text-gray-800"
                >
                  {showConfirmPassword ? (
                    <EyeOff size={19} />
                  ) : (
                    <Eye size={19} />
                  )}
                </button>
              </div>
            </div>

            {/* Aceite dos termos */}
            <label className="flex cursor-pointer items-start gap-2.5 pt-1 text-sm leading-relaxed text-gray-600">
              <input
                type="checkbox"
                checked={acceptedTerms}
                onChange={(event) => setAcceptedTerms(event.target.checked)}
                className="mt-1 size-4 shrink-0 accent-[#C81035]"
              />
              <span>
                Li e aceito os Termos de Uso e a Política de Privacidade.
              </span>
            </label>

            {/* Mensagens de validação */}
            {message && (
              <p
                role="status"
                aria-live="polite"
                className={`rounded-lg px-3 py-2 text-sm leading-relaxed ${
                  messageType === "success"
                    ? "bg-green-50 text-green-800"
                    : "bg-rose-50 text-[#A70D2C]"
                }`}
              >
                {message}
              </p>
            )}

            {/* Botão de cadastro */}
            <button
              type="submit"
              className="flex h-11 w-full items-center justify-center gap-2 rounded-xl bg-[#C81035] px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-[#AD0D2D] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#C81035] active:scale-[0.99]"
            >
              Criar conta
              <ArrowRight size={18} />
            </button>
          </form>

          {/* Link para login */}
          <p className="mt-4 text-center text-sm text-gray-500">
            Já tem uma conta?{" "}
            <Link
              to="/"
              className="font-semibold text-[#A70D2C] hover:underline"
            >
              Entrar
            </Link>
          </p>

          {/* Termos: uma linha no desktop */}
          <p className="mt-3 text-center text-[11px] leading-relaxed text-gray-500 lg:whitespace-nowrap">
            Ao continuar, você concorda com nossos Termos e Política de
            Privacidade.
          </p>
        </div>
      </section>
    </main>
  );
}

export default Register;
