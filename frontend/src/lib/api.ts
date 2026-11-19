import axios from "axios";

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

export type ApiFieldError = {
  field: string;
  message: string;
};

export class ApiError extends Error {
  status?: number;
  fieldErrors: ApiFieldError[];

  constructor(
    message: string,
    status?: number,
    fieldErrors: ApiFieldError[] = [],
  ) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
  timeout: 15000,
});

function toApiError(error: unknown): ApiError {
  if (axios.isAxiosError(error)) {
    const body = error.response?.data as
      | { message?: string; errors?: ApiFieldError[] }
      | undefined;

    const fieldErrors = body?.errors ?? [];
    const base = body?.message ?? "Não foi possível conectar ao servidor.";

    const mensagem = fieldErrors.length
      ? `${base} ${fieldErrors.map((f) => f.message).join(" ")}`
      : base;

    return new ApiError(mensagem, error.response?.status, fieldErrors);
  }

  return new ApiError("Erro inesperado.");
}

type Params = Record<string, string | number | boolean | undefined>;

export async function apiGet<T>(url: string, params?: Params): Promise<T> {
  try {
    const { data } = await http.get<ApiResponse<T>>(url, { params });
    return data.data;
  } catch (e) {
    throw toApiError(e);
  }
}

export async function apiPost<T>(
  url: string,
  body?: unknown,
  params?: Params,
): Promise<T> {
  try {
    const { data } = await http.post<ApiResponse<T>>(url, body, { params });
    return data.data;
  } catch (e) {
    throw toApiError(e);
  }
}

export async function apiPut<T>(
  url: string,
  body?: unknown,
  params?: Params,
): Promise<T> {
  try {
    const { data } = await http.put<ApiResponse<T>>(url, body, { params });
    return data.data;
  } catch (e) {
    throw toApiError(e);
  }
}

export async function apiPatch<T>(
  url: string,
  body?: unknown,
  params?: Params,
): Promise<T> {
  try {
    const { data } = await http.patch<ApiResponse<T>>(url, body, { params });
    return data.data;
  } catch (e) {
    throw toApiError(e);
  }
}

export async function apiDelete<T = null>(
  url: string,
  params?: Params,
): Promise<T> {
  try {
    const { data } = await http.delete<ApiResponse<T>>(url, { params });
    return data.data;
  } catch (e) {
    throw toApiError(e);
  }
}
