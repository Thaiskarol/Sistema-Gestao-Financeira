
import { lazy, Suspense, useEffect } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "./layouts/AppLayout";

// Carregamento sob demanda das páginas
const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const ForgotPassword = lazy(() => import("./pages/ForgotPassword"));
const Dashboard = lazy(() => import("./pages/Dashboard"));
const Transactions = lazy(() => import("./pages/Transactions"));
const Categories = lazy(() => import("./pages/Categories"));
const Budget = lazy(() => import("./pages/Budget"));
const Reports = lazy(() => import("./pages/Reports"));
const Insights = lazy(() => import("./pages/Insights"));
const Income = lazy(() => import("./pages/Income"));
const Investments = lazy(() => import("./pages/Investments"));
const Goals = lazy(() => import("./pages/Goals"));
const FinancialHealth = lazy(() => import("./pages/FinancialHealth"));
const BankingIntegration = lazy(() => import("./pages/BankingIntegration"));
const NewTransaction = lazy(() => import("./pages/NewTransaction"));
const Profile = lazy(() => import("./pages/Profile"));

function App() {
  useEffect(() => {
    const savedTheme = localStorage.getItem("financialTheme");

    document.documentElement.classList.toggle(
      "dark",
      savedTheme === "dark",
    );
  }, []);

  return (
    <BrowserRouter>
      <Suspense
        fallback={
          <div className="flex min-h-screen items-center justify-center bg-[#f7f7f7] text-[#193f59]">
            Carregando...
          </div>
        }
      >
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/cadastro" element={<Register />} />
          <Route path="/esqueci-senha" element={<ForgotPassword />} />

          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/transacoes" element={<Transactions />} />
          <Route path="/categorias" element={<Categories />} />
          <Route path="/orcamento" element={<Budget />} />
          <Route path="/relatorios" element={<Reports />} />
          <Route path="/insights" element={<Insights />} />
          <Route path="/renda" element={<Income />} />
          <Route path="/investimentos" element={<Investments />} />
          <Route path="/metas" element={<Goals />} />
          <Route path="/saude-financeira" element={<FinancialHealth />} />
          <Route path="/nova-transacao" element={<NewTransaction />} />
          <Route path="/perfil" element={<Profile />} />

          <Route
            path="/integracao-bancaria"
            element={
              <AppLayout subtitle="Visão financeira pessoal">
                <BankingIntegration />
              </AppLayout>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}

export default App;