import { useState, type ReactNode } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import {
  Bell,
  ChartNoAxesCombined,
  ChevronDown,
  CreditCard,
  LayoutDashboard,
  LogOut,
  Menu,
  PiggyBank,
  Target,
  Wallet,
  X,
  Tags,
  Lightbulb,
  TrendingUp,
  HeartPulse,
  UserRound,
} from "lucide-react";

type AppLayoutProps = {
  children: ReactNode;
  subtitle?: string;
};

const navigation = [
  { label: "Dashboard", icon: LayoutDashboard, to: "/dashboard" },
  { label: "Transações", icon: CreditCard, to: "/transacoes" },
  { label: "Categorias", icon: Tags, to: "/categorias" },
  { label: "Orçamento", icon: Wallet, to: "/orcamento" },
  { label: "Relatórios", icon: ChartNoAxesCombined, to: "/relatorios" },
  { label: "Insights", icon: Lightbulb, to: "/insights" },
  { label: "Renda", icon: TrendingUp, to: "/renda" },
  { label: "Investimentos", icon: PiggyBank, to: "/investimentos" },
  { label: "Metas", icon: Target, to: "/metas" },
  { label: "Saúde Financeira", icon: HeartPulse, to: "/saude-financeira" },
  { label: "Perfil / Configurações", icon: UserRound, to: "/perfil" },
];

export function AppLayout({
  children,
  subtitle = "Visão financeira pessoal",
}: AppLayoutProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const currentPage =
    navigation.find((item) => item.to === location.pathname)?.label ??
    (location.pathname === "/integracao-bancaria"
      ? "Integração bancária"
      : "Dashboard");

  const sidebar = (
    <div className="flex h-full min-h-0 flex-col bg-white">
      <div className="flex min-h-[88px] items-center justify-between gap-2 border-b border-[#eeeeee] px-4">
        <NavLink
          to="/dashboard"
          onClick={() => setMenuOpen(false)}
          className="flex min-w-0 items-center gap-2.5"
        >
          <img
            src="/logo-sem-fundo.png"
            alt="Finanças"
            className="h-11 w-11 shrink-0 object-contain"
          />

          <span className="min-w-0">
            <span className="block text-base font-extrabold tracking-tight text-[#292929]">
              FINANÇAS
            </span>
            <span className="block text-[10px] text-[#777]">
              Sua vida financeira em equilíbrio
            </span>
          </span>
        </NavLink>

        <button
          type="button"
          aria-label="Fechar menu"
          onClick={() => setMenuOpen(false)}
          className="rounded-lg p-2 text-[#666] hover:bg-slate-100 lg:hidden"
        >
          <X size={20} />
        </button>
      </div>

      <nav
        aria-label="Navegação principal"
        className="flex-1 space-y-1 overflow-y-auto px-3 py-4"
      >
        {navigation.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => setMenuOpen(false)}
              className={({ isActive }) =>
                `flex min-h-11 items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition ${
                  isActive
                    ? "bg-[#fbedf0] font-semibold text-[#bd0c36]"
                    : "text-[#555] hover:bg-slate-50 hover:text-[#bd0c36]"
                }`
              }
            >
              <Icon size={18} strokeWidth={1.8} />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="border-t border-[#eeeeee] p-4">
        <div className="flex items-center gap-3">
          <span className="flex size-10 shrink-0 items-center justify-center rounded-full bg-[#fbedf0] text-xs font-bold text-[#bd0c36]">
            AM
          </span>

          <span className="min-w-0 flex-1">
            <span className="block truncate text-sm font-semibold text-[#333]">
              Ana Martins
            </span>
            <span className="block truncate text-xs text-[#888]">
              Minha conta
            </span>
          </span>

          <button
            type="button"
            aria-label="Opções da conta"
            onClick={() => navigate("/perfil")}
            className="rounded-lg p-1 text-[#888] hover:bg-slate-100"
          >
            <ChevronDown size={17} />
          </button>
        </div>

        <button
          type="button"
          onClick={() => {
            window.location.href = "/";
          }}
          className="mt-3 flex w-full items-center gap-2 rounded-lg px-2 py-2 text-xs text-[#777] hover:bg-slate-50"
        >
          <LogOut size={16} />
          Sair
        </button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-[#f7f7f7] text-[#292929]">
      <aside className="fixed inset-y-0 left-0 z-30 hidden w-[242px] border-r border-[#dedede] lg:block">
        {sidebar}
      </aside>

      {menuOpen && (
        <div className="fixed inset-0 z-50 lg:hidden">
          <button
            type="button"
            aria-label="Fechar menu"
            onClick={() => setMenuOpen(false)}
            className="absolute inset-0 bg-black/40"
          />

          <aside className="absolute inset-y-0 left-0 w-[min(300px,86vw)] shadow-xl">
            {sidebar}
          </aside>
        </div>
      )}

      <div className="min-w-0 lg:pl-[242px]">
        <header className="sticky top-0 z-20 flex min-h-[72px] items-center justify-between gap-3 border-b border-[#dedede] bg-white/95 px-4 backdrop-blur sm:px-6 lg:px-8">
          <div className="flex min-w-0 items-center gap-3">
            <button
              type="button"
              aria-label="Abrir menu"
              onClick={() => setMenuOpen(true)}
              className="rounded-lg border border-[#e5e5e5] p-2 hover:bg-slate-50 lg:hidden"
            >
              <Menu size={20} />
            </button>

            <div className="min-w-0">
              <p className="truncate text-sm font-semibold">{currentPage}</p>
              <p className="text-xs text-[#888]">{subtitle}</p>
            </div>
          </div>

          <div className="flex shrink-0 items-center gap-2 sm:gap-3">
            <button
              type="button"
              onClick={() => navigate("/integracao-bancaria")}
              title="Ir para integração bancária"
              className="hidden items-center gap-2 rounded-full px-3 py-2 text-[11px] text-[#777] transition hover:bg-[#fbedf0] hover:text-[#bd0c36] focus-visible:outline focus-visible:outline-2 focus-visible:outline-[#bd0c36] sm:inline-flex"
            >
              <span className="size-[7px] rounded-full bg-[#bd0c36]" />
              <span>Contas atualizadas</span>
            </button>

            <button
              type="button"
              aria-label="Notificações"
              className="relative rounded-xl border border-[#e5e5e5] p-2.5 text-[#555] hover:bg-slate-50"
            >
              <Bell size={18} />
              <span className="absolute right-2 top-2 size-1.5 rounded-full bg-[#bd0c36]" />
            </button>

            <button
              type="button"
              aria-label="Abrir perfil de Ana Martins"
              title="Perfil / Configurações"
              onClick={() => navigate("/perfil")}
              className="flex size-9 items-center justify-center rounded-full bg-[#fbedf0] text-xs font-bold text-[#bd0c36] transition hover:bg-[#f6dce2] focus-visible:outline focus-visible:outline-2 focus-visible:outline-[#bd0c36]"
            >
              AM
            </button>
          </div>
        </header>

        <main className="min-w-0 p-4 sm:p-6 lg:p-7">
          {children}
        </main>
      </div>
    </div>
  );
}