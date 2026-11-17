
/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string;
  readonly VITE_USUARIO_ID_TESTE?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
