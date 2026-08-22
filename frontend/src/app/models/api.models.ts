export type Moeda = 'BRL' | 'USD' | 'EUR';
export type TipoRecebivel = 'DUPLICATA_MERCANTIL' | 'CHEQUE_PRE_DATADO';

export const MOEDAS: Moeda[] = ['BRL', 'USD', 'EUR'];
export const TIPOS_RECEBIVEL: TipoRecebivel[] = ['DUPLICATA_MERCANTIL', 'CHEQUE_PRE_DATADO'];

export interface Cedente {
  id: number;
  nome: string;
  documento: string;
  dataInclusao: string;
}

export interface CedenteRequisicao {
  nome: string;
  documento: string;
}

export interface Recebivel {
  id: number;
  cedenteId: number;
  nomeCedente: string;
  tipo: TipoRecebivel;
  valorFace: number;
  moedaTitulo: Moeda;
  prazoMeses: number;
  dataEmissao: string;
  dataVencimento: string;
  status: string;
  dataInclusao: string;
  dataUltimaAlteracao: string;
}

export interface RecebivelRequisicao {
  cedenteId: number;
  tipo: TipoRecebivel;
  valorFace: number;
  moedaTitulo: Moeda;
  prazoMeses: number;
  dataEmissao: string;
  dataVencimento: string;
}

export interface TaxaCambio {
  id: number;
  moedaOrigem: Moeda;
  moedaDestino: Moeda;
  valor: number;
  dataAtualizacao: string;
}

export interface TaxaCambioRequisicao {
  moedaOrigem: Moeda;
  moedaDestino: Moeda;
  valor: number;
}

export interface Liquidacao {
  id: number;
  recebivelId: number;
  valorPresenteMoedaTitulo: number;
  moedaPagamento: Moeda;
  valorPresenteMoedaPagamento: number;
  taxaCambioUtilizada: number;
  spreadAplicado: number;
  taxaBaseAplicada: number;
  dataLiquidacao: string;
}

export interface LiquidacaoRequisicao {
  recebivelId: number;
  moedaPagamento: Moeda;
}

export interface ItemExtrato {
  liquidacaoId: number;
  recebivelId: number;
  cedenteId: number;
  nomeCedente: string;
  tipoRecebivel: string;
  moedaTitulo: string;
  moedaPagamento: string;
  valorPresenteMoedaTitulo: number;
  valorPresenteMoedaPagamento: number;
  taxaCambioUtilizada: number;
  spreadAplicado: number;
  taxaBaseAplicada: number;
  dataLiquidacao: string;
}

export interface ExtratoResposta {
  conteudo: ItemExtrato[];
  pagina: number;
  tamanho: number;
  totalDeElementos: number;
  totalDePaginas: number;
}

export interface ErroResposta {
  mensagem?: string;
  detalhes?: string[];
}
