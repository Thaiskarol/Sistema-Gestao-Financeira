import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "./layouts/AppLayout";

import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import Dashboard from "./pages/Dashboard";
import Transactions from "./pages/Transactions";
import Categories from "./pages/Categories";
import Budget from "./pages/Budget";
import Reports from "./pages/Reports";
import Insights from "./pages/Insights";
import Income from "./pages/Income";
import Investments from "./pages/Investments";
import Goals from "./pages/Goals";
import FinancialHealth from "./pages/FinancialHealth";
import BankingIntegration from "./pages/BankingIntegration";
import NewTransaction from "./pages/NewTransaction";


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Register />} />
        <Route path="/esqueci-senha" element={<ForgotPassword />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/transacoes" element={<Transactions />} />
        <Route path="/categorias" element={<Categories />} />
        <Route path="/orcamento" element={<Budget />} />
        <Route path="/relatorios" element={<Reports/>} />
        <Route path="/insights" element={<Insights />} />
        <Route path="/renda" element={<Income />} />
        <Route path="/investimentos" element={<Investments />} />
        <Route path="/metas" element={<Goals />} />
        <Route path="/saude-financeira" element={<FinancialHealth />} />
        <Route path="/nova-transacao" element={<NewTransaction />} />
        <Route path="/integracao-bancaria" element={<AppLayout subtitle="Visão financeira pessoal"> <BankingIntegration />
        </AppLayout> } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
      
  );
}

export default App;