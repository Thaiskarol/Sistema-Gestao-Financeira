import type { ReactNode } from "react";
import {
  Bell,
  ChartNoAxesCombined,
  CreditCard,
  LayoutDashboard,
  LogOut,
  PiggyBank,
  Settings,
  Target,
  Wallet,
} from "lucide-react";

type AppLayoutProps = {
  children: ReactNode;
};

const navigation = [
  { label: "Dashboard", icon: LayoutDashboard, active: true },
  { label: "Lançamentos", icon: CreditCard, active: false },
  { label: "Orçamentos", icon: Wallet, active: false },
  { label: "Relatórios", icon: ChartNoAxesCombined, active: false },
  { label: "Metas financeiras", icon: Target, active: false },
  { label: "Investimentos", icon: PiggyBank, active: false },
];

export function AppLayout({ children }: AppLayoutProps) {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 md:flex">
      {/* Menu lateral */}
      <aside className="flex w-full flex-col border-b border-slate-200 bg-white p-5 md:min-h-screen md:w-64 md:border-r md:border-b-0">
        <div className="mb-8 flex items-center gap-3">
          <div className="flex size-10 items-center justify-center rounded-xl bg-emerald-600 text-white">
            <Wallet size={22} />
          </div>

          <div>
            <h1 className="font-bold">Gestão Financeira</h1>
            <p className="text-xs text-slate-500">Organização e planejamento</p>
          </div>
        </div>

        <p className="mb-3 text-xs font-semibold uppercase tracking-wider text-slate-400">
          Menu principal
        </p>

        <nav
          aria-label="Navegação principal"
          className="flex flex-wrap gap-2 md:flex-col"
        >
          {navigation.map((item) => {
            const Icon = item.icon;

            return (
              <button
                key={item.label}
                type="button"
                disabled={!item.active}
                aria-current={item.active ? "page" : undefined}
                title={
                  item.active ? item.label : "Disponível em uma próxima etapa"
                }
                className={`flex items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition ${
                  item.active
                    ? "bg-emerald-50 font-semibold text-emerald-700"
                    : "cursor-not-allowed text-slate-400"
                }`}
              >
                <Icon size={19} />
                {item.label}
              </button>
            );
          })}
        </nav>

        {/* Opções secundárias */}
        <div className="mt-6 flex gap-2 md:mt-auto md:flex-col">
          <button
            type="button"
            disabled
            className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm text-slate-400"
          >
            <Settings size={19} />
            Configurações
          </button>

          <button
            type="button"
            disabled
            className="flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm text-slate-400"
          >
            <LogOut size={19} />
            Sair
          </button>
        </div>
      </aside>

      {/* Conteúdo principal */}
      <div className="min-w-0 flex-1">
        <header className="flex items-center justify-between border-b border-slate-200 bg-white px-5 py-4 md:px-8">
          <div>
            <p className="text-sm text-slate-500">Bem-vinda de volta!</p>
            <p className="font-semibold">Sua vida financeira em foco</p>
          </div>

          <button
            type="button"
            aria-label="Notificações"
            title="Notificações"
            className="rounded-xl border border-slate-200 p-2.5 text-slate-600 transition hover:bg-slate-50"
          >
            <Bell size={20} />
          </button>
        </header>

        <main className="p-5 md:p-8">{children}</main>
      </div>
    </div>
  );
}
