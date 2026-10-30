import { useState, type FormEvent } from "react";
import {
  UserRound,
  Lightbulb,
  Bell,
  LockKeyhole,
  ArrowRight,
  Camera,
  LogOut,
  Check,
  Sun,
  Moon,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { AppLayout } from "../layouts/AppLayout";

type Section = "perfil" | "preferencias" | "notificacoes" | "seguranca";

const sections = [
  { id: "perfil" as const, label: "Meu perfil", icon: UserRound },
  { id: "preferencias" as const, label: "Preferências", icon: Lightbulb },
  { id: "notificacoes" as const, label: "Notificações", icon: Bell },
  { id: "seguranca" as const, label: "Segurança", icon: LockKeyhole },
];

const inputClass =
  "mt-2 min-h-11 w-full min-w-0 rounded-xl border border-[#d8d8d8] bg-white px-3.5 py-3 text-sm text-[#333] outline-none transition placeholder:text-[#999] focus:border-[#bd0c36] focus:ring-2 focus:ring-rose-100";

const initialProfile = {
  name: "Ana Martins",
  email: "ana.martins@email.com",
  phone: "",
  birthDate: "",
};

function Field({
  label,
  value,
  onChange,
  type = "text",
  placeholder,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  placeholder?: string;
}) {
  return (
    <label className="block min-w-0 text-sm font-medium text-[#333]">
      {label}
      <input
        className={inputClass}
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
      />
    </label>
  );
}

function Toggle({
  title,
  description,
  checked,
  onChange,
}: {
  title: string;
  description: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
}) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-[#eeeeee] py-4 last:border-b-0">
      <div className="min-w-0">
        <p className="text-sm font-semibold text-[#333]">{title}</p>
        <p className="mt-1 text-xs leading-relaxed text-[#888]">
          {description}
        </p>
      </div>
      <button
        type="button"
        role="switch"
        aria-checked={checked}
        aria-label={title}
        onClick={() => onChange(!checked)}
        className={`relative h-6 w-11 shrink-0 rounded-full transition ${
          checked ? "bg-[#bd0c36]" : "bg-[#d5d5d5]"
        }`}
      >
        <span
          className={`absolute top-1 size-4 rounded-full bg-white shadow-sm transition-all ${
            checked ? "left-6" : "left-1"
          }`}
        />
      </button>
    </div>
  );
}

export default function Profile() {
  const navigate = useNavigate();

  const [section, setSection] = useState<Section>("perfil");
  const [profile, setProfile] = useState(initialProfile);
  const [savedMessage, setSavedMessage] = useState("");

  const [preferences, setPreferences] = useState({
    currency: "BRL",
    monthStart: "1",
    compactMode: true,
    theme: (localStorage.getItem("financialTheme") === "dark"
    ? "dark"
    : "light") as "light" | "dark",
  });

  const [notifications, setNotifications] = useState({
    budgetAlerts: true,
    weeklySummary: true,
    newInsights: true,
    emailNotifications: false,
  });

  const [security, setSecurity] = useState({
    twoFactor: true,
  });

  const [password, setPassword] = useState({
    current: "",
    newPassword: "",
    confirm: "",
  });

  const [securityModal, setSecurityModal] = useState<
    "password" | "devices" | null
  >(null);

  const [devices, setDevices] = useState([
    {
      id: 1,
      name: "Computador atual",
      browser: "Google Chrome",
      location: "Jaboatão dos Guararapes, PE",
      lastAccess: "Agora",
      current: true,
    },
    {
      id: 2,
      name: "Celular",
      browser: "Safari",
      location: "Recife, PE",
      lastAccess: "Há 2 horas",
      current: false,
    },
  ]);

  function updateProfile(
    field: keyof typeof initialProfile,
    value: string,
  ) {
    setProfile((current) => ({ ...current, [field]: value }));
    setSavedMessage("");
  }

  function updateTheme(theme: "light" | "dark") {
  setPreferences((current) => ({
    ...current,
    theme,
  }));

  localStorage.setItem("financialTheme", theme);

  document.documentElement.classList.toggle(
    "dark",
    theme === "dark",
  );

  setSavedMessage("");
}

  function updateTwoFactor(checked: boolean) {
    setSecurity((current) => ({
      ...current,
      twoFactor: checked,
    }));
    setSavedMessage("");
  }

  function disconnectDevice(id: number) {
    setDevices((current) =>
      current.filter((device) => device.id !== id),
    );
    setSavedMessage("Dispositivo desconectado nesta demonstração.");
  }

  function endAllSessions() {
    const confirmed = window.confirm(
      "Deseja realmente encerrar todas as sessões?",
    );

    if (!confirmed) return;

    setDevices((current) =>
      current.filter((device) => device.current),
    );

    setSavedMessage(
      "As outras sessões foram encerradas. A sessão atual permanece ativa nesta demonstração.",
    );
  }

  function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    try {
      localStorage.setItem("financialProfile", JSON.stringify(profile));
      localStorage.setItem(
        "financialPreferences",
        JSON.stringify(preferences),
      );
      localStorage.setItem(
        "financialNotifications",
        JSON.stringify(notifications),
      );
      localStorage.setItem("financialSecurity", JSON.stringify(security));
      setSavedMessage("Alterações salvas neste navegador.");
    } catch {
      setSavedMessage("Não foi possível salvar as alterações.");
    }
  }

  function renderContent() {
    switch (section) {
      case "perfil":
        return (
          <>
            <h2 className="text-base font-semibold text-[#333]">Meu perfil</h2>

            <div className="mt-5 flex flex-wrap items-center gap-4 border-b border-[#dedede] pb-6">
              <div className="flex size-16 shrink-0 items-center justify-center rounded-2xl bg-[#fbedf0] text-xl font-bold text-[#8c092e]">
                AM
              </div>
              <div className="min-w-0">
                <p className="text-sm font-semibold text-[#333]">
                  {profile.name || "Seu nome"}
                </p>
                <p className="mt-1 break-all text-xs text-[#888]">
                  {profile.email || "Seu e-mail"}
                </p>
                <button
                  type="button"
                  onClick={() =>
                    setSavedMessage(
                      "O envio de foto de perfil ainda não está configurado.",
                    )
                  }
                  className="mt-3 inline-flex min-h-10 items-center gap-2 rounded-lg bg-[#fbedf0] px-4 py-2 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f8dfe5]"
                >
                  <Camera size={16} />
                  Alterar foto
                </button>
              </div>
            </div>

            <div className="grid grid-cols-1 gap-5 pt-5 md:grid-cols-2">
              <Field
                label="Nome completo"
                value={profile.name}
                onChange={(value) => updateProfile("name", value)}
                placeholder="Seu nome completo"
              />
              <Field
                label="E-mail"
                type="email"
                value={profile.email}
                onChange={(value) => updateProfile("email", value)}
                placeholder="seu@email.com"
              />
              <Field
                label="Telefone"
                type="tel"
                value={profile.phone}
                onChange={(value) => updateProfile("phone", value)}
                placeholder="(00) 00000-0000"
              />
              <Field
                label="Data de nascimento"
                type="date"
                value={profile.birthDate}
                onChange={(value) => updateProfile("birthDate", value)}
              />
            </div>
          </>
        );

      case "preferencias":
        return (
          <>
            <h2 className="text-base font-semibold text-[#333]">
              Preferências
            </h2>

            <div className="mt-4">
              <div className="flex flex-col gap-3 border-t border-[#dedede] py-4 sm:flex-row sm:items-center sm:justify-between">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Moeda principal
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    Real brasileiro (BRL)
                  </p>
                </div>

                <select
                  aria-label="Moeda principal"
                  className="min-h-10 w-full rounded-xl border border-[#d8d8d8] bg-white px-3.5 py-2 text-sm text-[#333] outline-none focus:border-[#bd0c36] focus:ring-2 focus:ring-rose-100 sm:w-[210px] sm:shrink-0"
                  value={preferences.currency}
                  onChange={(event) =>
                    setPreferences({
                      ...preferences,
                      currency: event.target.value,
                    })
                  }
                >
                  <option value="BRL">BRL — Real brasileiro</option>
                  <option value="USD">USD — Dólar americano</option>
                  <option value="EUR">EUR — Euro</option>
                </select>
              </div>

              <div className="flex flex-col gap-3 border-t border-[#dedede] py-4 sm:flex-row sm:items-center sm:justify-between">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Início do mês financeiro
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    Define o período dos relatórios
                  </p>
                </div>

                <select
                  aria-label="Início do mês financeiro"
                  className="min-h-10 w-full rounded-xl border border-[#d8d8d8] bg-white px-3.5 py-2 text-sm text-[#333] outline-none focus:border-[#bd0c36] focus:ring-2 focus:ring-rose-100 sm:w-[210px] sm:shrink-0"
                  value={preferences.monthStart}
                  onChange={(event) =>
                    setPreferences({
                      ...preferences,
                      monthStart: event.target.value,
                    })
                  }
                >
                  <option value="1">Dia 1 de cada mês</option>
                  <option value="5">Dia 5 de cada mês</option>
                  <option value="10">Dia 10 de cada mês</option>
                  <option value="15">Dia 15 de cada mês</option>
                  <option value="20">Dia 20 de cada mês</option>
                  <option value="25">Dia 25 de cada mês</option>
                </select>
              </div>

              <div className="flex flex-col gap-3 border-t border-[#dedede] py-4 sm:flex-row sm:items-center sm:justify-between">
  <div className="min-w-0">
    <p className="text-sm font-semibold text-[#333]">
      Aparência do sistema
    </p>
    <p className="mt-1 text-xs text-[#888]">
      Escolha entre o modo claro e o modo escuro
    </p>
  </div>

  <div className="flex w-full gap-2 sm:w-auto sm:shrink-0">
    <button
      type="button"
      onClick={() => updateTheme("light")}
      aria-pressed={preferences.theme === "light"}
      className={`flex min-h-10 flex-1 items-center justify-center gap-2 rounded-lg border px-3 py-2 text-sm font-medium transition sm:flex-none ${
        preferences.theme === "light"
          ? "border-[#bd0c36] bg-[#fbedf0] text-[#bd0c36]"
          : "border-[#d8d8d8] bg-white text-[#555] hover:bg-slate-50"
      }`}
    >
      <Sun size={17} />
      Claro
    </button>

    <button
      type="button"
      onClick={() => updateTheme("dark")}
      aria-pressed={preferences.theme === "dark"}
      className={`flex min-h-10 flex-1 items-center justify-center gap-2 rounded-lg border px-3 py-2 text-sm font-medium transition sm:flex-none ${
        preferences.theme === "dark"
          ? "border-[#bd0c36] bg-[#fbedf0] text-[#bd0c36]"
          : "border-[#d8d8d8] bg-white text-[#555] hover:bg-slate-50"
      }`}
    >
      <Moon size={17} />
      Escuro
    </button>
  </div>
</div>

              <div className="flex items-center justify-between gap-4 border-t border-[#dedede] py-4">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Modo compacto
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    Exibir mais informações por tela
                  </p>
                </div>

                <button
                  type="button"
                  role="switch"
                  aria-checked={preferences.compactMode}
                  aria-label="Modo compacto"
                  onClick={() =>
                    setPreferences({
                      ...preferences,
                      compactMode: !preferences.compactMode,
                    })
                  }
                  className={`relative h-[22px] w-[40px] shrink-0 rounded-full transition-colors ${
                    preferences.compactMode
                      ? "bg-[#bd0c36]"
                      : "bg-[#d5d5d5]"
                  }`}
                >
                  <span
                    className={`absolute top-[3px] size-4 rounded-full bg-white shadow-sm transition-all ${
                      preferences.compactMode
                        ? "left-[21px]"
                        : "left-[3px]"
                    }`}
                  />
                </button>
              </div>
            </div>
          </>
        );

      case "notificacoes":
        return (
          <>
            <h2 className="text-base font-semibold text-[#333]">
              Notificações
            </h2>

            <div className="mt-4">
              <Toggle
                title="Alertas de orçamento"
                description="Avise quando uma categoria estiver no limite"
                checked={notifications.budgetAlerts}
                onChange={(checked) =>
                  setNotifications((current) => ({
                    ...current,
                    budgetAlerts: checked,
                  }))
                }
              />

              <Toggle
                title="Resumo semanal"
                description="Receba um resumo todo domingo"
                checked={notifications.weeklySummary}
                onChange={(checked) =>
                  setNotifications((current) => ({
                    ...current,
                    weeklySummary: checked,
                  }))
                }
              />

              <Toggle
                title="Novos insights"
                description="Análises e recomendações personalizadas"
                checked={notifications.newInsights}
                onChange={(checked) =>
                  setNotifications((current) => ({
                    ...current,
                    newInsights: checked,
                  }))
                }
              />

              <Toggle
                title="Notificações por e-mail"
                description="Receba também no e-mail cadastrado"
                checked={notifications.emailNotifications}
                onChange={(checked) =>
                  setNotifications((current) => ({
                    ...current,
                    emailNotifications: checked,
                  }))
                }
              />
            </div>
          </>
        );

      case "seguranca":
        return (
          <>
            <h2 className="text-base font-semibold text-[#333]">
              Segurança
            </h2>

            <div className="mt-4 border-t border-[#dedede]">
              {/* Senha */}
              <div className="flex flex-col gap-3 border-b border-[#dedede] py-5 sm:flex-row sm:items-center sm:justify-between">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Senha
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    Alterada há 4 meses
                  </p>
                </div>

                <button
                  type="button"
                  onClick={() => {
                    setPassword({
                      current: "",
                      newPassword: "",
                      confirm: "",
                    });
                    setSecurityModal("password");
                  }}
                  className="min-h-10 w-full rounded-lg bg-[#fbedf0] px-4 py-2 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f8dfe5] sm:w-auto sm:shrink-0"
                >
                  Alterar senha
                </button>
              </div>

              {/* Autenticação em duas etapas */}
              <div className="flex items-center justify-between gap-4 border-b border-[#dedede] py-5">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Autenticação em duas etapas
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    Adicione uma camada extra de proteção
                  </p>
                </div>

                <button
                  type="button"
                  role="switch"
                  aria-checked={security.twoFactor}
                  aria-label="Autenticação em duas etapas"
                  onClick={() => updateTwoFactor(!security.twoFactor)}
                  className={`relative h-[22px] w-[40px] shrink-0 rounded-full transition-colors ${
                    security.twoFactor
                      ? "bg-[#bd0c36]"
                      : "bg-[#d5d5d5]"
                  }`}
                >
                  <span
                    className={`absolute top-[3px] size-4 rounded-full bg-white shadow-sm transition-all ${
                      security.twoFactor
                        ? "left-[21px]"
                        : "left-[3px]"
                    }`}
                  />
                </button>
              </div>

              {/* Dispositivos conectados */}
              <div className="flex flex-col gap-3 border-b border-[#dedede] py-5 sm:flex-row sm:items-center sm:justify-between">
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-[#333]">
                    Dispositivos conectados
                  </p>
                  <p className="mt-1 text-xs text-[#888]">
                    {devices.length} dispositivos com sessão ativa
                  </p>
                </div>

                <button
                  type="button"
                  onClick={() => setSecurityModal("devices")}
                  className="min-h-10 w-full rounded-lg bg-[#fbedf0] px-4 py-2 text-sm font-semibold text-[#bd0c36] transition hover:bg-[#f8dfe5] sm:w-auto sm:shrink-0"
                >
                  Gerenciar
                </button>
              </div>
            </div>

            {/* Encerrar sessões */}
            <div className="mt-6 flex flex-col gap-4 rounded-xl border border-[#f2d4dc] bg-[#fff5f7] p-4 sm:flex-row sm:items-center sm:justify-between">
              <div className="min-w-0">
                <p className="text-sm font-semibold text-[#8c092e]">
                  Encerrar todas as sessões
                </p>
                <p className="mt-1 text-xs leading-relaxed text-[#888]">
                  Você precisará entrar novamente em todos os dispositivos.
                </p>
              </div>

              <button
                type="button"
                onClick={endAllSessions}
                className="min-h-10 w-full shrink-0 rounded-lg px-3 py-2 text-sm font-bold text-[#a20b2e] transition hover:bg-[#fbedf0] sm:w-auto"
              >
                Encerrar sessões
              </button>
            </div>

            {/* Modal de alteração de senha */}
            {securityModal === "password" && (
              <div
                className="fixed inset-0 z-[100] flex items-center justify-center overflow-y-auto bg-black/40 p-4"
                onClick={() => setSecurityModal(null)}
              >
                <div
                  role="dialog"
                  aria-modal="true"
                  aria-labelledby="password-modal-title"
                  className="my-auto w-full max-w-md rounded-2xl bg-white p-5 shadow-xl sm:p-6"
                  onClick={(event) => event.stopPropagation()}
                >
                  <h3
                    id="password-modal-title"
                    className="text-lg font-bold text-[#333]"
                  >
                    Alterar senha
                  </h3>
                  <p className="mt-1 text-sm text-[#888]">
                    Preencha os campos para solicitar a alteração.
                  </p>

                  <div className="mt-5 space-y-4">
                    <label className="block text-sm font-medium text-[#333]">
                      Senha atual
                      <input
                        required
                        type="password"
                        autoComplete="current-password"
                        value={password.current}
                        onChange={(event) =>
                          setPassword((current) => ({
                            ...current,
                            current: event.target.value,
                          }))
                        }
                        className={inputClass}
                      />
                    </label>

                    <label className="block text-sm font-medium text-[#333]">
                      Nova senha
                      <input
                        required
                        minLength={8}
                        type="password"
                        autoComplete="new-password"
                        value={password.newPassword}
                        onChange={(event) =>
                          setPassword((current) => ({
                            ...current,
                            newPassword: event.target.value,
                          }))
                        }
                        className={inputClass}
                      />
                    </label>

                    <label className="block text-sm font-medium text-[#333]">
                      Confirmar nova senha
                      <input
                        required
                        minLength={8}
                        type="password"
                        autoComplete="new-password"
                        value={password.confirm}
                        onChange={(event) =>
                          setPassword((current) => ({
                            ...current,
                            confirm: event.target.value,
                          }))
                        }
                        className={inputClass}
                      />
                    </label>

                    <div className="flex flex-col-reverse gap-2 pt-2 sm:flex-row sm:justify-end">
                      <button
                        type="button"
                        onClick={() => setSecurityModal(null)}
                        className="min-h-10 rounded-lg border border-[#dedede] px-4 py-2 text-sm font-semibold text-[#666] hover:bg-slate-50"
                      >
                        Cancelar
                      </button>

                      <button
                        type="button"
                        onClick={() => {
                          if (
                            !password.current ||
                            !password.newPassword ||
                            !password.confirm
                          ) {
                            setSavedMessage(
                              "Preencha todos os campos da alteração de senha.",
                            );
                            return;
                          }

                          if (password.newPassword.length < 8) {
                            setSavedMessage(
                              "A nova senha deve ter pelo menos 8 caracteres.",
                            );
                            return;
                          }

                          if (
                            password.newPassword !== password.confirm
                          ) {
                            setSavedMessage(
                              "A confirmação da nova senha não corresponde.",
                            );
                            return;
                          }

                          setSecurityModal(null);
                          setPassword({
                            current: "",
                            newPassword: "",
                            confirm: "",
                          });
                          setSavedMessage(
                            "Formulário validado. A alteração real da senha depende do serviço de autenticação.",
                          );
                        }}
                        className="min-h-10 rounded-lg bg-[#bd0c36] px-4 py-2 text-sm font-semibold text-white hover:bg-[#a20b2e]"
                      >
                        Continuar
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Modal de dispositivos */}
            {securityModal === "devices" && (
              <div
                className="fixed inset-0 z-[100] flex items-center justify-center overflow-y-auto bg-black/40 p-4"
                onClick={() => setSecurityModal(null)}
              >
                <div
                  role="dialog"
                  aria-modal="true"
                  aria-labelledby="devices-modal-title"
                  className="my-auto w-full max-w-lg rounded-2xl bg-white p-5 shadow-xl sm:p-6"
                  onClick={(event) => event.stopPropagation()}
                >
                  <h3
                    id="devices-modal-title"
                    className="text-lg font-bold text-[#333]"
                  >
                    Dispositivos conectados
                  </h3>
                  <p className="mt-1 text-sm text-[#888]">
                    Confira os dispositivos com sessões de demonstração.
                  </p>

                  <div className="mt-5 divide-y divide-[#eeeeee]">
                    {devices.length === 0 ? (
                      <p className="py-5 text-sm text-[#888]">
                        Nenhum dispositivo conectado.
                      </p>
                    ) : (
                      devices.map((device) => (
                        <div
                          key={device.id}
                          className="flex flex-col gap-3 py-4 sm:flex-row sm:items-center sm:justify-between"
                        >
                          <div className="min-w-0">
                            <p className="text-sm font-semibold text-[#333]">
                              {device.name}
                              {device.current && (
                                <span className="ml-2 text-xs font-medium text-emerald-700">
                                  Atual
                                </span>
                              )}
                            </p>
                            <p className="mt-1 text-xs text-[#888]">
                              {device.browser}
                            </p>
                            <p className="mt-1 text-xs text-[#888]">
                              {device.location} • {device.lastAccess}
                            </p>
                          </div>

                          {!device.current && (
                            <button
                              type="button"
                              onClick={() => disconnectDevice(device.id)}
                              className="min-h-9 self-start rounded-lg bg-[#fbedf0] px-3 py-2 text-xs font-semibold text-[#bd0c36] hover:bg-[#f8dfe5] sm:self-center"
                            >
                              Desconectar
                            </button>
                          )}
                        </div>
                      ))
                    )}
                  </div>

                  <div className="mt-4 flex justify-end border-t border-[#dedede] pt-4">
                    <button
                      type="button"
                      onClick={() => setSecurityModal(null)}
                      className="min-h-10 rounded-lg bg-[#bd0c36] px-5 py-2 text-sm font-semibold text-white hover:bg-[#a20b2e]"
                    >
                      Fechar
                    </button>
                  </div>
                </div>
              </div>
            )}
          </>
        );
    }
  }

  return (
    <AppLayout >
      <div className="w-full min-w-0 space-y-5 pb-8">
        <header>
          <p className="mb-1 text-[11px] font-bold uppercase tracking-[0.16em] text-[#8c092e]">
            Visão financeira
          </p>
          <h1 className="text-2xl font-bold tracking-tight text-[#292929] sm:text-3xl">
            Perfil / Configurações
          </h1>
          <p className="mt-2 text-sm text-[#777]">
            Gerencie seus dados, preferências e segurança.
          </p>
        </header>

        <form onSubmit={save}>
          <div className="grid grid-cols-1 items-start gap-4 lg:grid-cols-[240px_minmax(0,1fr)] xl:gap-5">
            <nav
              aria-label="Seções de perfil"
              className="rounded-xl border border-[#dedede] bg-white p-2"
            >
              {sections.map((item) => {
                const Icon = item.icon;
                const active = section === item.id;

                return (
                  <button
                    key={item.id}
                    type="button"
                    onClick={() => {
                      setSection(item.id);
                      setSavedMessage("");
                    }}
                    aria-current={active ? "page" : undefined}
                    className={`flex min-h-11 w-full items-center gap-3 rounded-lg px-3 py-3 text-left text-xs transition ${
                      active
                        ? "bg-[#fbedf0] font-semibold text-[#8c092e]"
                        : "text-[#777] hover:bg-slate-50 hover:text-[#333]"
                    }`}
                  >
                    <Icon size={17} strokeWidth={1.7} />
                    <span className="min-w-0 flex-1">{item.label}</span>
                    <ArrowRight size={15} />
                  </button>
                );
              })}

              <div className="my-3 border-t border-[#dedede]" />

              <button
                type="button"
                onClick={() => navigate("/")}
                className="flex min-h-10 w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-xs text-[#8c092e] hover:bg-[#fbedf0]"
              >
                <LogOut size={15} />
                Sair da conta
              </button>
            </nav>

            <section className="min-w-0 rounded-xl border border-[#dedede] bg-white p-4 sm:p-6">
              {renderContent()}

              {savedMessage && (
                <p
                  role="status"
                  className="mt-5 rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700"
                >
                  {savedMessage}
                </p>
              )}

              <div className="mt-5 flex flex-col-reverse justify-end gap-3 border-t border-[#dedede] pt-4 sm:flex-row">
                <button
                  type="submit"
                  className="inline-flex min-h-11 items-center justify-center gap-2 rounded-lg bg-[#bd0c36] px-5 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-[#a20b2e]"
                >
                  <Check size={17} />
                  Salvar alterações
                </button>
              </div>
            </section>
          </div>
        </form>
      </div>
    </AppLayout>
  );
}