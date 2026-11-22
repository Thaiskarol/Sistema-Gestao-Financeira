
export type TipoTransacao = "RECEITA" | "DESPESA";

export type Categoria = {
  id: number;
  nome: string;
  padrao: boolean;
};

export type Transacao = {
  id: number;
  descricao: string;
  valor: number;
  dataTransacao: string;
  recorrencia: string | null;
  tipo: TipoTransacao;
  categoriaId: number;
  usuarioId: number;
  estabelecimento: string | null;
  categoriaNome: string;
};

export type ResumoFinanceiro = {
  totalReceitas: number;
  totalDespesas: number;
  saldo: number;
  economiaAcumulada: number;
  percentualComprometimentoRenda: number;
};

export type ValorMensal = {
  mes: string;
  valor: number;
};

export type GrupoValor = {
  rotulo: string;
  valor: number;
};

export type EvolucaoPatrimonial = {
  mes: string;
  patrimonioAcumulado: number;
};

export type RelatorioMensal = {
  mesReferencia: string;
  resumo: ResumoFinanceiro;
  gastosPorCategoria: GrupoValor[];
  evolucao: {
    totalMesAtual: number;
    totalMesAnterior: number;
    variacaoPercentual: number;
    descricao: string;
  };
};

export type Meta = {
  id: number;
  nome: string;
  valorObjetivo: number;
  valorAcumulado: number;
  dataDesejada: string;
  dataCriacao: string;
  usuarioId: number;
};

export type CriterioScore = {
  nome: string;
  pontos: number;
  dadosSuficientes: boolean;
};

export type Score = {
  score: number;
  dadosSuficientes: boolean;
  mensagem: string;
  criterios: CriterioScore[];
};

export type ClassificacaoScore = {
  usuarioId: number;
  score: number | null;
  nivel: "CRITICO" | "ATENCAO" | "BOM" | "EXCELENTE" | null;
  descricao: string;
};

export type RecomendacaoGeral = {
  rendaIdentificada: number;
  investimento: {
    rendaIdentificada: number;
    percentualRecomendado: number;
    valorRecomendado: number;
  };
  reserva: {
    despesaMediaMensal: number;
    mesesCobertura: number;
    valorIdealReserva: number;
  };
  metasSugeridas: {
    descricao: string;
    valorObjetivo: number;
  }[];
  insight: string;
};

export type DistribuicaoFinanceira = {
  rendaTotal: number;
  necessidades: number;
  desejos: number;
  investimentos: number;
};

export type ReservaEmergencia = {
  despesaMediaMensal: number;
  mesesCobertura: number;
  valorIdealReserva: number;
};

export type RendaItem = {
  id: number;
  descricao: string;
  valor: number;
  tipo: "SALARIO_LIQUIDO" | "RECORRENTE" | "VARIAVEL";
  usuarioId: number;
};