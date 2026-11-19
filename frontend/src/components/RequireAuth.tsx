import { Navigate, Outlet } from "react-router-dom";
import { getUsuario } from "@/lib/session";

export function RequireAuth() {
  return getUsuario() ? <Outlet /> : <Navigate to="/" replace />;
}