import { useState, type FormEvent } from "react";
import { ArrowLeft, Mail, ShieldCheck } from "lucide-react";
import { Link } from "react-router-dom";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setMessage(
      "Solicitação validada! O envio do e-mail de recuperação será habilitado quando conectarmos o backend.",
    );
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

        {/* Logo clicável: volta para o login */}
        <Link
          to="/"
          className="relative z-10 flex w-fit items-center gap-3"
          aria-label="Ir para a tela de login"
        >
          <img
            src="/logo_branca.png"
            alt="Logo Gestão Financeira"
            className="size-11 object-contain"
          />
          <div>
            <p className="text-xl font-extrabold tracking-wide">FINANÇAS</p>
            <p className="text-xs text-white/75">
              Sua vida financeira em equilíbrio
            </p>
          </div>
        </Link>

        {/* Mensagem principal */}
        <div className="relative z-10 my-auto max-w-xl py-8">
          <p className="mb-4 text-sm font-bold tracking-[0.18em] text-white/75">
            SUA SEGURANÇA EM PRIMEIRO LUGAR
          </p>

          <h1 className="text-3xl font-bold leading-tight xl:text-4xl">
            Recuperar o acesso também faz parte do cuidado com suas finanças.
          </h1>

          <p className="mt-4 max-w-md text-base leading-relaxed text-white/85">
            Esquecer uma senha acontece. Informe seu e-mail e siga as etapas
            para recuperar o acesso à sua conta com tranquilidade.
          </p>
        </div>

        {/* Segurança */}
        <div className="relative z-10 flex items-center gap-3 text-sm text-white/80">
          <ShieldCheck size={19} />
          <span>Seus dados protegidos com segurança</span>
        </div>
      </section>

      {/* Área de recuperação de senha */}
      <section className="flex min-h-screen flex-col px-6 py-5 sm:px-10 sm:py-8 lg:justify-center lg:px-10 lg:py-4 xl:px-16">
        {/* Marca mobile */}
        <Link
          to="/"
          className="mb-6 flex w-fit items-center gap-3 lg:hidden"
          aria-label="Voltar à página de login"
        >
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
        </Link>

        <div className="mx-auto flex w-full max-w-md flex-1 flex-col justify-center lg:flex-none">
          {/* Voltar ao login */}
          <div className="mb-5">
            <Link
              to="/"
              className="inline-flex items-center gap-2 text-sm text-gray-600 transition hover:text-[#C81035]"
            >
              <ArrowLeft size={18} />
              Voltar ao login
            </Link>
          </div>

          {/* Introdução */}
          <header className="mb-5">
            <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-red-50 text-[#C81035]">
              <Mail size={24} />
            </div>

            <h2 className="text-2xl font-bold tracking-tight text-[#333333] sm:text-3xl">
              Esqueceu sua senha?
            </h2>

            <p className="mt-2 text-sm leading-relaxed text-gray-500 sm:text-base">
              Não se preocupe! Informe o e-mail associado à sua conta. Vamos
              preparar a recuperação da sua senha para que você possa voltar a
              acessar suas informações financeiras.
            </p>
          </header>

          {/* Formulário de recuperação */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label
                htmlFor="forgot-email"
                className="mb-1.5 block text-sm font-medium text-gray-800"
              >
                E-mail cadastrado
              </label>

              <div className="relative">
                <Mail
                  aria-hidden="true"
                  size={18}
                  className="absolute left-3.5 top-1/2 -translate-y-1/2 text-gray-400"
                />

                <input
                  id="forgot-email"
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

            <button
              type="submit"
              className="flex h-11 w-full items-center justify-center rounded-xl bg-[#C81035] px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-[#AD0D2D] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[#C81035] active:scale-[0.99]"
            >
              Recuperar senha
            </button>
          </form>

          {/* Mensagem enquanto o backend não está conectado */}
          {message && (
            <p
              role="status"
              aria-live="polite"
              className="mt-3 rounded-lg bg-rose-50 px-3 py-2 text-sm leading-relaxed text-[#A70D2C]"
            >
              {message}
            </p>
          )}

          {/* Retorno ao login */}
          <p className="mt-5 text-center text-sm text-gray-600">
            Lembrou sua senha?{" "}
            <Link
              to="/"
              className="font-semibold text-[#C81035] transition hover:text-red-800"
            >
              Entrar
            </Link>
          </p>

          {/* Termos: uma linha no desktop */}
          <p className="mt-3 text-center text-[11px] leading-relaxed text-gray-500 sm:text-xs lg:whitespace-nowrap">
            Ao continuar, você concorda com nossos Termos e Política de
            Privacidade.
          </p>
        </div>
      </section>
    </main>
  );
}

export default ForgotPassword;
