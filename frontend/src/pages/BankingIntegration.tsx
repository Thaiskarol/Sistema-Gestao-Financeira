import { useState } from "react";
import {
  ArrowLeftRight,
  ArrowRight,
  CheckCircle2,
  CircleAlert,
  Landmark,
  MoreHorizontal,
  Plus,
  Search,
  ShieldCheck,
  X,
} from "lucide-react";

type Status = "connected" | "error" | "syncing";

type Institution = {
  id: number;
  initials: string;
  name: string;
  accountType: string;
  accountNumber: string;
  status: Status;
  lastSync: string;
  color: string;
};

const initialInstitutions: Institution[] = [
  {
    id: 1,
    initials: "BH",
    name: "Banco Horizonte",
    accountType: "Conta corrente",
    accountNumber: "4589",
    status: "connected",
    lastSync: "há 4 min",
    color: "#19425e",
  },
  {
    id: 2,
    initials: "BC",
    name: "Banco Centralis",
    accountType: "Conta corrente",
    accountNumber: "1062",
    status: "connected",
    lastSync: "há 12 min",
    color: "#970b2b",
  },
  {
    id: 3,
    initials: "IM",
    name: "Invest Mais",
    accountType: "Investimentos",
    accountNumber: "7811",
    status: "error",
    lastSync: "ontem",
    color: "#343434",
  },
];

export default function IntegracaoBancaria() {
  const [institutions, setInstitutions] = useState<Institution[]>(initialInstitutions);
  const [searchOpen, setSearchOpen] = useState(false);
  const [search, setSearch] = useState("");
  const [menuOpen, setMenuOpen] = useState<number | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [name, setName] = useState("");
  const [accountType, setAccountType] = useState("Conta corrente");
  const [message, setMessage] = useState("");

  const filteredInstitutions = institutions.filter((institution) =>
    institution.name.toLowerCase().includes(search.toLowerCase())
  );

  function synchronize(id: number) {
    setInstitutions((current) =>
      current.map((item) =>
        item.id === id ? { ...item, status: "syncing" } : item
      )
    );

    window.setTimeout(() => {
      setInstitutions((current) =>
        current.map((item) =>
          item.id === id
            ? { ...item, status: "connected", lastSync: "agora" }
            : item
        )
      );
    }, 1200);
  }

  function connectInstitution(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const trimmedName = name.trim();

    if (
      institutions.some(
        (item) => item.name.toLowerCase() === trimmedName.toLowerCase()
      )
    ) {
      setMessage("Essa instituição já está cadastrada.");
      return;
    }

    const initials = trimmedName
      .split(/\s+/)
      .slice(0, 2)
      .map((part) => part[0])
      .join("")
      .toUpperCase();

    setInstitutions((current) => [
      ...current,
      {
        id: Math.max(0, ...current.map((item) => item.id)) + 1,
        initials,
        name: trimmedName,
        accountType,
        accountNumber: "0000",
        status: "connected",
        lastSync: "agora",
        color: "#19425e",
      },
    ]);

    setName("");
    setMessage("");
    setModalOpen(false);
  }

  function disconnect(id: number) {
    setInstitutions((current) => current.filter((item) => item.id !== id));
    setMenuOpen(null);
  }

  const statusStyles: Record<Status, string> = {
    connected: "bg-[#edf5f0] text-[#647a6d]",
    error: "bg-[#fff3e2] text-[#806a4b]",
    syncing: "bg-[#eaf2fa] text-[#315e83]",
  };

  const statusNames: Record<Status, string> = {
    connected: "Conectado",
    error: "Erro",
    syncing: "Sincronizando",
  };

  return (
    <div className="min-w-0 font-sans text-[#343434]">
      

      {/* Conteúdo principal */}
      <div className="px-4 pb-8 sm:px-6 lg:px-[30px]">
        {/* Banner de segurança */}
        <section className="flex flex-wrap items-center gap-3 rounded-b-[13px] bg-[#19425e] p-5 text-white sm:gap-4 sm:px-6 sm:py-[22px]">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-white/10">
            <Landmark size={27} strokeWidth={1.7} />
          </div>

          <div className="min-w-0 flex-1">
            <h3 className="mb-1 text-[13px] font-bold">
              Seus dados, sempre sob seu controle
            </h3>
            <p className="text-xs leading-5 text-[#d4e0e8]">
              Conexões seguras, protegidas e autorizadas por você.
              Revogue o acesso quando quiser.
            </p>
          </div>

          <button
            onClick={() =>
              setMessage(
                "As conexões reais exigem autorização segura e consentimento do titular."
              )
            }
            className="ml-auto inline-flex shrink-0 items-center gap-1 py-2 text-[10px] text-white hover:underline"
          >
            Saiba mais <ArrowRight size={15} />
          </button>
        </section>

        {message && !modalOpen && (
          <div className="mt-4 flex items-center gap-2 rounded-lg border border-[#dbe8e0] bg-[#f0f7f2] p-3 text-xs text-[#365e46]">
            <ShieldCheck size={17} className="shrink-0" />
            <span className="flex-1">{message}</span>
            <button onClick={() => setMessage("")} aria-label="Fechar mensagem">
              <X size={16} />
            </button>
          </div>
        )}

        {/* Instituições conectadas */}
<section className="mt-6">
  <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
    <div>
      <h2 className="text-base font-bold">
        Instituições conectadas
      </h2>
      <p className="mt-1 text-xs text-[#858585]">
        Gerencie suas contas bancárias e sincronizações.
      </p>
    </div>

    <div className="flex flex-wrap items-center gap-2">
      {searchOpen && (
        <input
          autoFocus
          type="text"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Buscar instituição..."
          className="h-10 min-w-0 flex-1 rounded-lg border border-gray-200 px-3 text-sm outline-none focus:border-[#c60835] sm:w-48"
        />
      )}

      <button
        type="button"
        onClick={() => {
          if (searchOpen) {
            setSearch("");
          }
          setSearchOpen(!searchOpen);
        }}
        aria-label="Pesquisar instituição"
        className="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50"
      >
        {searchOpen ? <X size={18} /> : <Search size={18} />}
      </button>

      <span className="text-[11px] text-[#858585]">
        {institutions.length} instituições
      </span>

      <button
        type="button"
        onClick={() => {
          setMessage("");
          setModalOpen(true);
        }}
        className="inline-flex min-h-[40px] items-center justify-center gap-2 rounded-lg bg-[#c60835] px-4 text-xs font-semibold text-white transition hover:bg-[#a9082d]"
      >
        <Plus size={17} />
        Conectar instituição
      </button>
    </div>
  </div>

  <div className="flex flex-col gap-[10px]">

  </div>
          <div className="flex flex-col gap-[10px]">
            {filteredInstitutions.map((institution) => (
              <article
                key={institution.id}
                className="grid grid-cols-[40px_minmax(0,1fr)_25px] items-center gap-3 rounded-[13px] border border-[#dedede] bg-white p-4 shadow-sm transition hover:border-[#d3c9cc] sm:p-[18px] lg:grid-cols-[40px_minmax(150px,1fr)_minmax(175px,0.75fr)_auto_25px] lg:gap-[13px]"
              >
                <div
                  style={{ backgroundColor: institution.color }}
                  className="flex h-10 w-10 items-center justify-center rounded-xl text-[11px] font-extrabold text-white"
                >
                  {institution.initials}
                </div>

                <div className="min-w-0">
                  <h3 className="mb-1 break-words text-sm font-bold">
                    {institution.name}
                  </h3>
                  <p className="text-xs leading-5 text-[#828282]">
                    {institution.accountType} •••• {institution.accountNumber}
                  </p>
                </div>

                <div className="col-start-2 flex min-w-0 flex-col items-start gap-1.5 lg:col-start-auto">
                  <span
                    className={`inline-flex min-h-[29px] items-center gap-1 rounded-full px-2 text-[10px] ${statusStyles[institution.status]}`}
                  >
                    {institution.status === "connected" && (
                      <CheckCircle2 size={12} />
                    )}
                    {institution.status === "error" && (
                      <CircleAlert size={12} />
                    )}
                    {institution.status === "syncing" && (
                      <ArrowLeftRight size={12} className="animate-spin" />
                    )}
                    {statusNames[institution.status]}
                  </span>
                  <span className="break-words text-[10px] leading-4 text-[#898989]">
                    Última sincronização: {institution.lastSync}
                  </span>
                </div>

                <button
                  disabled={institution.status === "syncing"}
                  onClick={() => synchronize(institution.id)}
                  className="col-span-3 inline-flex min-h-[43px] items-center justify-center gap-2 rounded-lg bg-[#fcecf0] px-3 text-xs font-bold text-[#b80c35] transition hover:bg-[#f8dce4] disabled:cursor-wait disabled:opacity-70 lg:col-span-1"
                >
                  <ArrowLeftRight size={17} />
                  {institution.status === "syncing"
                    ? "Sincronizando..."
                    : "Sincronizar agora"}
                </button>

                <div className="relative col-start-3 row-start-1 lg:col-start-auto lg:row-auto">
                  <button
                    aria-label={`Opções de ${institution.name}`}
                    aria-expanded={menuOpen === institution.id}
                    onClick={() =>
                      setMenuOpen(
                        menuOpen === institution.id ? null : institution.id
                      )
                    }
                    className="flex h-9 w-6 items-center justify-center rounded-md text-[#777] hover:bg-[#f4f4f4]"
                  >
                    <MoreHorizontal size={21} />
                  </button>

                  {menuOpen === institution.id && (
                    <div className="absolute right-0 top-10 z-20 w-52 rounded-xl border border-[#e4e4e4] bg-white p-1.5 shadow-xl">
                      <button
                        onClick={() => synchronize(institution.id)}
                        className="flex min-h-10 w-full items-center gap-2 rounded-lg px-2 text-left text-xs hover:bg-[#f6f6f6]"
                      >
                        <ArrowLeftRight size={15} />
                        Sincronizar conta
                      </button>
                      <button
                        onClick={() => disconnect(institution.id)}
                        className="flex min-h-10 w-full items-center gap-2 rounded-lg px-2 text-left text-xs text-[#b80c35] hover:bg-[#fcecf0]"
                      >
                        <X size={15} />
                        Desconectar instituição
                      </button>
                    </div>
                  )}
                </div>
              </article>
            ))}

            {filteredInstitutions.length === 0 && (
              <div className="flex flex-col items-center rounded-xl border border-dashed border-[#d8d8d8] bg-white px-5 py-10 text-center">
                <Search size={25} className="text-[#818181]" />
                <h3 className="mt-3 text-sm font-bold">
                  Nenhuma instituição encontrada
                </h3>
                <p className="mt-1 text-xs text-[#818181]">
                  Tente outro nome ou conecte uma nova instituição.
                </p>
                <button
                  onClick={() => setSearch("")}
                  className="mt-3 text-xs font-bold text-[#c60835]"
                >
                  Limpar pesquisa
                </button>
              </div>
            )}
          </div>
        </section>

        <div className="mt-5 flex items-start gap-2 text-[11px] leading-5 text-[#858585]">
          <ShieldCheck size={17} className="shrink-0 text-[#607b6a]" />
          <p>
            Seus dados bancários são privados. Você mantém o controle
            sobre as instituições conectadas.
          </p>
        </div>
      </div>

      {/* Modal para adicionar instituição */}
      {modalOpen && (
        <div
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) setModalOpen(false);
          }}
          className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/50 p-4 backdrop-blur-[3px]"
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="bank-modal-title"
            className="my-auto w-full max-w-[450px] rounded-2xl border border-[#e7e7e7] bg-white p-6 shadow-2xl"
          >
            <div className="flex items-center justify-between">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-[#fcecf0] text-[#c60835]">
                <Landmark size={23} />
              </div>
              <button
                onClick={() => setModalOpen(false)}
                aria-label="Fechar modal"
                className="flex h-10 w-10 items-center justify-center rounded-xl border border-[#dedede] hover:bg-[#f6f6f6]"
              >
                <X size={19} />
              </button>
            </div>

            <h2
              id="bank-modal-title"
              className="mt-5 text-[22px] font-bold tracking-tight"
            >
              Conectar instituição
            </h2>
            <p className="mb-5 mt-2 text-[13px] leading-6 text-[#777]">
              Cadastre uma instituição para exibi-la na sua lista de contas.
            </p>

            <form onSubmit={connectInstitution} className="flex flex-col gap-3">
              <label htmlFor="institution-name" className="text-xs font-bold">
                Nome da instituição
              </label>
              <input
                id="institution-name"
                value={name}
                onChange={(event) => {
                  setName(event.target.value);
                  setMessage("");
                }}
                placeholder="Ex.: Banco Horizonte"
                required
                maxLength={80}
                className="h-11 rounded-lg border border-[#dedede] px-3 text-sm outline-none focus:border-[#c60835]"
              />

              <label htmlFor="account-type" className="mt-1 text-xs font-bold">
                Tipo de conta
              </label>
              <select
                id="account-type"
                value={accountType}
                onChange={(event) => setAccountType(event.target.value)}
                className="h-11 rounded-lg border border-[#dedede] bg-white px-3 text-sm outline-none focus:border-[#c60835]"
              >
                <option>Conta corrente</option>
                <option>Conta poupança</option>
                <option>Investimentos</option>
                <option>Conta digital</option>
              </select>

              {message && (
                <p role="alert" className="text-xs text-[#b80c35]">
                  {message}
                </p>
              )}

              <div className="mt-2 flex items-start gap-2 rounded-lg bg-[#f4f7f9] p-3 text-[11px] leading-5 text-[#596b76]">
                <ShieldCheck size={17} className="mt-0.5 shrink-0" />
              </div>

              <div className="mt-3 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="min-h-[42px] rounded-lg border border-[#dedede] px-4 text-sm font-semibold hover:bg-[#f7f7f7]"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="inline-flex min-h-[42px] items-center gap-2 rounded-lg bg-[#c60835] px-4 text-sm font-semibold text-white hover:bg-[#a9082d]"
                >
                  <Plus size={17} />
                  Adicionar
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </div>
  );
}