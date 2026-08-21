import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro, isoParaBr } from '../services/api.service';
import { Cedente, ExtratoResposta, MOEDAS } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Extrato de Liquidação</h2>
      <form class="grade" (ngSubmit)="consultar(0)">
        <label>Data Início
          <input type="date" [(ngModel)]="dataInicio" name="dataInicio" required>
        </label>
        <label>Data Fim
          <input type="date" [(ngModel)]="dataFim" name="dataFim" required>
        </label>
        <label>Cedente (opcional)
          <select [(ngModel)]="cedenteId" name="cedenteId">
            <option [ngValue]="null">Todos</option>
            <option *ngFor="let c of cedentes" [ngValue]="c.id">{{ c.id }} — {{ c.nome }}</option>
          </select>
        </label>
        <label>Moeda de Pagamento (opcional)
          <select [(ngModel)]="moeda" name="moeda">
            <option [ngValue]="null">Todas</option>
            <option *ngFor="let m of moedas" [ngValue]="m">{{ m }}</option>
          </select>
        </label>
        <button class="acao" type="submit" [disabled]="!dataInicio || !dataFim || carregando">Consultar</button>
      </form>
      <div class="mensagem erro" *ngIf="erro">{{ erro }}</div>
    </div>

    <div class="cartao" *ngIf="resposta">
      <h2>Resultado ({{ resposta.totalDeElementos }} liquidações)</h2>
      <table *ngIf="resposta.conteudo.length; else vazio">
        <thead>
          <tr>
            <th>Liq.</th><th>Receb.</th><th>Cedente</th><th>Tipo</th>
            <th>VP Título</th><th>VP Pagamento</th><th>Câmbio</th><th>Data</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let item of resposta.conteudo">
            <td>#{{ item.liquidacaoId }}</td>
            <td>#{{ item.recebivelId }}</td>
            <td>{{ item.nomeCedente }}</td>
            <td>{{ item.tipoRecebivel }}</td>
            <td>{{ item.valorPresenteMoedaTitulo | number:'1.2-2' }} {{ item.moedaTitulo }}</td>
            <td>{{ item.valorPresenteMoedaPagamento | number:'1.2-2' }} {{ item.moedaPagamento }}</td>
            <td>{{ item.taxaCambioUtilizada | number:'1.4-6' }}</td>
            <td>{{ item.dataLiquidacao | dataBr }}</td>
          </tr>
        </tbody>
      </table>
      <ng-template #vazio><p>Nenhuma liquidação encontrada no período.</p></ng-template>

      <div class="paginacao" *ngIf="resposta.totalDePaginas > 1">
        <button class="secundaria" (click)="consultar(resposta.pagina - 1)"
                [disabled]="resposta.pagina === 0">&laquo; Anterior</button>
        <span>Página {{ resposta.pagina + 1 }} de {{ resposta.totalDePaginas }}</span>
        <button class="secundaria" (click)="consultar(resposta.pagina + 1)"
                [disabled]="resposta.pagina + 1 >= resposta.totalDePaginas">Próxima &raquo;</button>
      </div>
    </div>
  `
})
export class ExtratoComponent implements OnInit {
  cedentes: Cedente[] = [];
  moedas = MOEDAS;
  dataInicio = '';
  dataFim = '';
  cedenteId: number | null = null;
  moeda: string | null = null;
  resposta: ExtratoResposta | null = null;
  erro = '';
  carregando = false;
  readonly tamanho = 20;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.listarCedentes().subscribe({ next: c => this.cedentes = c });
    const hoje = new Date();
    const inicio = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    this.dataFim = hoje.toISOString().slice(0, 10);
    this.dataInicio = inicio.toISOString().slice(0, 10);
  }

  consultar(pagina: number): void {
    this.erro = '';
    this.carregando = true;
    this.api.consultarExtrato(
      isoParaBr(this.dataInicio), isoParaBr(this.dataFim),
      this.cedenteId, this.moeda, pagina, this.tamanho
    ).subscribe({
      next: resp => { this.resposta = resp; this.carregando = false; },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }
}
