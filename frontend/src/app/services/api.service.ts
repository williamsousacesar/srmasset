import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Cedente, CedenteRequisicao,
  Recebivel, RecebivelRequisicao,
  TaxaCambio, TaxaCambioRequisicao,
  Liquidacao, LiquidacaoRequisicao,
  ExtratoResposta
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {

  private readonly base = '/api';

  constructor(private http: HttpClient) {}

  // Cedentes
  listarCedentes(): Observable<Cedente[]> {
    return this.http.get<Cedente[]>(`${this.base}/cedentes/listar-tudo`);
  }

  cadastrarCedente(req: CedenteRequisicao): Observable<Cedente> {
    return this.http.post<Cedente>(`${this.base}/cedentes/cadastrar`, req);
  }

  buscarCedentePorId(id: number): Observable<Cedente> {
    return this.http.get<Cedente>(`${this.base}/cedentes/${id}`);
  }

  // Recebiveis
  listarRecebiveis(): Observable<Recebivel[]> {
    return this.http.get<Recebivel[]>(`${this.base}/recebiveis/listar-tudo`);
  }

  cadastrarRecebivel(req: RecebivelRequisicao): Observable<Recebivel> {
    return this.http.post<Recebivel>(`${this.base}/recebiveis/cadastrar`, req);
  }

  buscarRecebivelPorId(id: number): Observable<Recebivel> {
    return this.http.get<Recebivel>(`${this.base}/recebiveis/${id}`);
  }

  // Cambio
  listarTaxas(): Observable<TaxaCambio[]> {
    return this.http.get<TaxaCambio[]>(`${this.base}/cambio/taxas/listar-tudo`);
  }

  atualizarTaxa(req: TaxaCambioRequisicao): Observable<TaxaCambio> {
    return this.http.put<TaxaCambio>(`${this.base}/cambio/taxas`, req);
  }

  buscarTaxa(moedaOrigem: string, moedaDestino: string): Observable<TaxaCambio> {
    return this.http.get<TaxaCambio>(`${this.base}/cambio/taxas/${moedaOrigem}/${moedaDestino}`);
  }

  // Liquidacoes
  liquidar(req: LiquidacaoRequisicao): Observable<Liquidacao> {
    return this.http.post<Liquidacao>(`${this.base}/liquidacoes/liquidar`, req);
  }

  buscarLiquidacaoPorRecebivel(recebivelId: number): Observable<Liquidacao> {
    return this.http.get<Liquidacao>(`${this.base}/liquidacoes/recebivel/${recebivelId}`);
  }

  // Extrato (datas no formato dd/MM/yyyy)
  consultarExtrato(dataInicio: string, dataFim: string, cedenteId: number | null,
                   moeda: string | null, pagina: number, tamanho: number): Observable<ExtratoResposta> {
    let params = new HttpParams()
      .set('dataInicio', dataInicio)
      .set('dataFim', dataFim)
      .set('pagina', pagina)
      .set('tamanho', tamanho);
    if (cedenteId != null) params = params.set('cedenteId', cedenteId);
    if (moeda) params = params.set('moeda', moeda);
    return this.http.get<ExtratoResposta>(`${this.base}/relatorios/extrato-liquidacao`, { params });
  }
}

export function extrairErro(err: any): string {
  if (err?.error?.mensagem) {
    const detalhes = err.error.detalhes?.length ? ` (${err.error.detalhes.join('; ')})` : '';
    return err.error.mensagem + detalhes;
  }
  return 'Erro ao comunicar com o servidor.';
}

/** Converte data ISO (yyyy-MM-dd, de input type=date) para dd/MM/yyyy. */
export function isoParaBr(iso: string): string {
  const [a, m, d] = iso.split('-');
  return `${d}/${m}/${a}`;
}
