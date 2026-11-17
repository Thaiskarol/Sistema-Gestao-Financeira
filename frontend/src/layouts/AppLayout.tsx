
import { useState, type ReactNode } from "react";
import {
  Bell,
  Check,
  ChevronDown,
  LayoutDashboard,
  Menu,
  X,
  ArrowLeftRight,
  Tags,
  Wallet,
  ChartNoAxesCombined,
  Lightbulb,
  BriefcaseBusiness,
  Target,
  HeartPulse,
  UserRound,
  Landmark,
  LogOut,
  Settings,
} from "lucide-react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";

interface AppLayoutProps {
  children: ReactNode;
  subtitle?: string;
}

interface Notification {
  id: number;
  title: string;
  message: string;
  time: string;
  read: boolean;
}

const navigation = [
  {
    label: "Visão geral",
    path: "/dashboard",
    icon: LayoutDashboard,
  },
  {
    label: "Transações",
    path: "/transacoes",
    icon: ArrowLeftRight,
  },
  {
    label: "Categorias",
    path: "/categorias",
    icon: Tags,
  },
  {
    label: "Orçamento",
    path: "/orcamento",
    icon: Wallet,
  },
  {
    label: "Relatórios",
    path: "/relatorios",
    icon: ChartNoAxesCombined,
  },
  {
    label: "Insights",
    path: "/insights",
    icon: Lightbulb,
  },
  {
    label: "Renda",
    path: "/renda",
    icon: BriefcaseBusiness,
  },
  {
    label: "Investimentos",
    path: "/investimentos",
    icon: Landmark,
  },
  {
    label: "Metas",
    path: "/metas",
    icon: Target,
  },
  {
    label: "Saúde financeira",
    path: "/saude-financeira",
    icon: HeartPulse,
  },
];

const initialNotifications: Notification[] = [
  {
    id: 1,
    title: "Acompanhe sua economia",
    message: "Consulte a evolução dos seus gastos para identificar oportunidades de economia.",
    time: "Hoje",
    read: false,
  },
  {
    id: 2,
    title: "Revise suas assinaturas",
    message: "Confira suas despesas recorrentes e mantenha seus gastos organizados.",
    time: "Ontem",
    read: false,
  },
];

export function AppLayout({
  children,
  subtitle = "Visão financeira pessoal",
}: AppLayoutProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [notifications, setNotifications] =
    useState<Notification[]>(initialNotifications);

  const location = useLocation();
  const navigate = useNavigate();

  const currentPage =
    navigation.find((item) => item.path === location.pathname)?.label ??
    (location.pathname === "/perfil" ? "Perfil" : "Sistema financeiro");

  const unreadCount = notifications.filter(
    (notification) => !notification.read
  ).length;

  function closeMenus() {
    setMenuOpen(false);
    setProfileOpen(false);
    setNotificationsOpen(false);
  }

  function handleLogout() {
    closeMenus();
    navigate("/");
  }

  function markAsRead(id: number) {
    setNotifications((current) =>
      current.map((notification) =>
        notification.id === id
          ? { ...notification, read: true }
          : notification
      )
    );
  }

  function markAllAsRead() {
    setNotifications((current) =>
      current.map((notification) => ({
        ...notification,
        read: true,
      }))
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      {menuOpen && (
        <button
          type="button"
          aria-label="Fechar menu"
          onClick={() => setMenuOpen(false)}
          className="fixed inset-0 z-40 bg-slate-950/40 backdrop-blur-[2px] lg:hidden"
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-[250px] flex-col border-r border-slate-200 bg-white transition-transform duration-300 lg:translate-x-0 ${
          menuOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex h-[76px] items-center justify-between border-b border-slate-100 px-6">
          <button
            type="button"
            onClick={() => {
              navigate("/dashboard");
              closeMenus();
            }}
            className="flex items-center gap-3 text-left"
          >
            <img
              src="/logo-sem-fundo.png"
              alt="Logo"
              className="h-10 w-10 object-contain"
            />

            <div>
              <p className="text-sm font-bold tracking-[0.12em] text-slate-800">
                FINANÇAS
              </p>
              <p className="text-[11px] text-slate-400">
                Gestão financeira
              </p>
            </div>
          </button>

          <button
            type="button"
            aria-label="Fechar menu"
            onClick={() => setMenuOpen(false)}
            className="rounded-lg p-2 text-slate-400 transition-colors hover:bg-[#FDF5F6] hover:text-[#BD0C36] lg:hidden"
          >
            <X size={19} />
          </button>
        </div>

        <div className="px-4 pb-2 pt-7">
          <p className="px-3 text-[10px] font-semibold uppercase tracking-[0.16em] text-slate-400">
            Menu principal
          </p>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto px-3 pb-5">
          {navigation.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={closeMenus}
                className={({ isActive }) =>
                  `group flex items-center gap-3 rounded-xl px-3 py-2.5 text-[13px] font-medium transition-colors ${
                    isActive
                      ? "bg-[#FCEFF1] text-[#BD0C36]"
                      : "text-slate-500 hover:bg-[#FDF5F6] hover:text-[#BD0C36]"
                  }`
                }
              >
                <Icon size={18} strokeWidth={1.8} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        <div className="border-t border-slate-100 p-3">
          <NavLink
            to="/perfil"
            onClick={closeMenus}
            className={({ isActive }) =>
              `flex items-center gap-3 rounded-xl px-3 py-3 transition-colors ${
                isActive
                  ? "bg-[#FCEFF1]"
                  : "hover:bg-[#FDF5F6]"
              }`
            }
          >
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-100 text-xs font-semibold text-slate-700">
             AM
            </div>

            <div className="min-w-0 flex-1">
              <p className="truncate text-sm font-semibold text-slate-800">
                Ana Martins
              </p>
              <p className="text-xs text-slate-400">Minha conta</p>
            </div>

            <Settings size={16} className="text-slate-400" />
          </NavLink>

          <button
            type="button"
            onClick={handleLogout}
            className="mt-1 flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-xs font-medium text-slate-400 transition-colors hover:bg-red-50 hover:text-red-600"
          >
            <LogOut size={16} />
            Sair
          </button>
        </div>
      </aside>

      <div className="min-h-screen lg:pl-[250px]">
        <header className="sticky top-0 z-30 flex min-h-[76px] items-center justify-between border-b border-slate-200/80 bg-white/95 px-4 backdrop-blur-md sm:px-6 lg:px-8">
          <div className="flex min-w-0 items-center gap-3">
            <button
              type="button"
              aria-label="Abrir menu"
              onClick={() => setMenuOpen(true)}
              className="rounded-xl border border-slate-200 p-2 text-slate-600 transition-colors hover:border-[#F2C9D1] hover:bg-[#FDF5F6] hover:text-[#BD0C36] lg:hidden"
            >
              <Menu size={20} />
            </button>

            <div className="min-w-0">
              <h1 className="truncate text-base font-semibold text-slate-800 sm:text-lg">
                {currentPage}
              </h1>

              <p className="hidden text-xs text-slate-400 sm:block">
                {subtitle}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2 sm:gap-4">
            <button
              type="button"
              onClick={() => navigate("/integracao-bancaria")}
              className="hidden items-center gap-2 rounded-full border border-slate-200 px-3 py-2 text-xs font-medium text-slate-500 transition-colors hover:border-slate-300 hover:bg-slate-50 sm:flex"
            >
              <span className="h-2 w-2 rounded-full bg-emerald-500" />
              Contas atualizadas
            </button>

            {/* Notificações */}
            <div className="relative">
              <button
                type="button"
                aria-label="Notificações"
                aria-expanded={notificationsOpen}
                onClick={() => {
                  setNotificationsOpen((open) => !open);
                  setProfileOpen(false);
                }}
                className="relative rounded-xl p-2 text-slate-400 transition-colors hover:bg-[#FDF5F6] hover:text-[#BD0C36]"
              >
                <Bell size={19} />

                {unreadCount > 0 && (
                  <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-[#BD0C36] px-1 text-[9px] font-semibold text-white ring-2 ring-white">
                    {unreadCount}
                  </span>
                )}
              </button>

              {notificationsOpen && (
                <>
                  <button
                    type="button"
                    aria-label="Fechar notificações"
                    onClick={() => setNotificationsOpen(false)}
                    className="fixed inset-0 z-40 cursor-default"
                  />

                  <div className="absolute right-0 top-12 z-50 w-[min(340px,calc(100vw-32px))] rounded-2xl border border-slate-200 bg-white p-4 shadow-xl">
                    <div className="mb-4 flex items-center justify-between gap-3">
                      <div>
                        <h3 className="text-sm font-semibold text-slate-800">
                          Notificações
                        </h3>
                        <p className="mt-1 text-xs text-slate-400">
                          {unreadCount} não lidas
                        </p>
                      </div>

                      <button
                        type="button"
                        aria-label="Fechar notificações"
                        onClick={() => setNotificationsOpen(false)}
                        className="rounded-lg p-1.5 text-slate-400 transition-colors hover:bg-slate-100"
                      >
                        <X size={17} />
                      </button>
                    </div>

                    {notifications.length > 0 ? (
                      <div className="max-h-80 space-y-2 overflow-y-auto">
                        {notifications.map((notification) => (
                          <div
                            key={notification.id}
                            className={`rounded-xl p-3 ${
                              notification.read
                                ? "bg-white"
                                : "bg-[#FDF5F6]"
                            }`}
                          >
                            <div className="flex gap-3">
                              <div className="mt-0.5 shrink-0 rounded-lg bg-[#FCEFF1] p-2 text-[#BD0C36]">
                                <Bell size={16} />
                              </div>

                              <div className="min-w-0 flex-1">
                                <div className="flex items-start justify-between gap-2">
                                  <p className="text-xs font-semibold text-slate-700">
                                    {notification.title}
                                  </p>

                                  {!notification.read && (
                                    <span className="mt-1 h-2 w-2 shrink-0 rounded-full bg-[#BD0C36]" />
                                  )}
                                </div>

                                <p className="mt-1 text-xs leading-relaxed text-slate-500">
                                  {notification.message}
                                </p>

                                <div className="mt-2 flex items-center justify-between gap-2">
                                  <span className="text-[10px] text-slate-400">
                                    {notification.time}
                                  </span>

                                  {!notification.read && (
                                    <button
                                      type="button"
                                      onClick={() =>
                                        markAsRead(notification.id)
                                      }
                                      className="text-[10px] font-medium text-[#BD0C36] transition-colors hover:underline"
                                    >
                                      Marcar como lida
                                    </button>
                                  )}
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="py-8 text-center">
                        <Bell
                          size={25}
                          className="mx-auto text-slate-300"
                        />
                        <p className="mt-2 text-sm font-medium text-slate-600">
                          Nenhuma notificação
                        </p>
                        <p className="mt-1 text-xs text-slate-400">
                          Você está em dia!
                        </p>
                      </div>
                    )}

                    {unreadCount > 0 && (
                      <button
                        type="button"
                        onClick={markAllAsRead}
                        className="mt-3 flex w-full items-center justify-center gap-2 border-t border-slate-100 pt-3 text-xs font-medium text-[#BD0C36] transition-colors hover:underline"
                      >
                        <Check size={14} />
                        Marcar todas como lidas
                      </button>
                    )}
                  </div>
                </>
              )}
            </div>

            {/* Menu do perfil */}
            <div className="relative">
              <button
                type="button"
                onClick={() => {
                  setProfileOpen((open) => !open);
                  setNotificationsOpen(false);
                }}
                aria-expanded={profileOpen}
                className="flex items-center gap-2 rounded-xl p-1.5 transition-colors hover:bg-slate-100"              >
                <span className="flex h-8 w-8 items-center justify-center rounded-full bg-slate-900 text-[11px] font-semibold text-white">
                 AM
                </span>

                <ChevronDown
                  size={15}
                  className="hidden text-slate-400 sm:block"
                />
              </button>

              {profileOpen && (
                <>
                  <button
                    type="button"
                    aria-label="Fechar opções da conta"
                    onClick={() => setProfileOpen(false)}
                    className="fixed inset-0 z-40 cursor-default"
                  />

                  <div className="absolute right-0 top-12 z-50 w-48 rounded-xl border border-slate-200 bg-white p-1.5 shadow-lg">
                    <button
                      type="button"
                      onClick={() => {
                        navigate("/perfil");
                        setProfileOpen(false);
                      }}
                      className="flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-left text-sm text-slate-600 transition-colors hover:bg-[#FDF5F6] hover:text-[#BD0C36]"
                    >
                      <UserRound size={16} />
                      Meu perfil
                    </button>

                    <button
                      type="button"
                      onClick={handleLogout}
                      className="flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-left text-sm text-rose-600 transition-colors hover:bg-rose-50"
                    >
                      <LogOut size={16} />
                      Sair
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </header>

        <main className="mx-auto w-full max-w-[1600px] p-4 sm:p-6 lg:p-8">
          {children}
        </main>
      </div>
    </div>
  );
}