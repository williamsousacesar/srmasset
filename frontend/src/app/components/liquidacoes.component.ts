import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro } from '../services/api.service';
import { Liquidacao, Recebivel, MOEDAS, Moeda } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-liquidacoes',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Liquidar (Antecipar) Recebível</h2>
      <form class="grade" (ngSubmit)="liquidar()">
        <label>Recebível (em aberto)
          <select [(ngModel)]="recebivelId" name="recebivelId" required>
            <option [ngValue]="null" disabled>Selecione...</option>
            <option *ngFor="let r of recebiveisAbertos" [ngValue]="r.id">
              #{{ r.id }} — {{ r.nomeCedente }} — {{ r.valorFace | number:'1.2-2' }} {{ r.moedaTitulo }}
            </option>
          </select>
        </label>
        <label>Moeda de Pagamento
          <select [(ngModel)]="moedaPagamento" name="moedaPagamento" required>
            <option *ngFor="let m of moedas" [ngValue]="m">{{ m }}</option>
          </select>
        </label>
        <button class="acao" type="submit" [disabled]="recebivelId == null || carregando">Liquidar</button>
      </form>
      <div class="mensagem erro" *ngIf="erro">{{ erro }}</div>

      <div *ngIf="resultado" class="mensagem sucesso">
        Liquidação #{{ resultado.id }} concluída em {{ resultado.dataLiquidacao | dataBr }}.
      </div>
      <table *ngIf="resultado">
        <tbody>
          <tr><th>Valor Presente (moeda do título)</th><td>{{ resultado.valorPresenteMoedaTitulo | number:'1.2-2' }}</td></tr>
          <tr><th>Moeda de Pagamento</th><td>{{ resultado.moedaPagamento }}</td></tr>
          <tr><th>Valor Presente (moeda de pagamento)</th><td>{{ resultado.valorPresenteMoedaPagamento | number:'1.2-2' }}</td></tr>
          <tr><th>Taxa de Câmbio Utilizada</th><td>{{ resultado.taxaCambioUtilizada | number:'1.4-6' }}</td></tr>
          <tr><th>Spread Aplicado</th><td>{{ resultado.spreadAplicado | percent:'1.2-2' }}</td></tr>
          <tr><th>Taxa Base Aplicada</th><td>{{ resultado.taxaBaseAplicada | percent:'1.2-2' }}</td></tr>
        </tbody>
      </table>
    </div>

    <div class="cartao">
      <h2>Consultar Liquidação por Recebível</h2>
      <form class="grade" (ngSubmit)="consultar()">
        <label>ID do Recebível
          <input type="number" min="1" [(ngModel)]="consultaId" name="consultaId" required>
        </label>
        <button class="acao" type="submit" [disabled]="consultaId == null">Consultar</button>
      </form>
      <div class="mensagem erro" *ngIf="erroConsulta">{{ erroConsulta }}</div>
      <table *ngIf="consulta">
        <tbody>
          <tr><th>Liquidação</th><td>#{{ consulta.id }}</td></tr>
          <tr><th>Recebível</th><td>#{{ consulta.recebivelId }}</td></tr>
          <tr><th>Valor Presente (título)</th><td>{{ consulta.valorPresenteMoedaTitulo | number:'1.2-2' }}</td></tr>
          <tr><th>Valor Presente (pagamento)</th><td>{{ consulta.valorPresenteMoedaPagamento | number:'1.2-2' }} {{ consulta.moedaPagamento }}</td></tr>
          <tr><th>Data</th><td>{{ consulta.dataLiquidacao | dataBr }}</td></tr>
        </tbody>
      </table>
    </div>
  `
})
export class LiquidacoesComponent implements OnInit {
  recebiveisAbertos: Recebivel[] = [];
  moedas = MOEDAS;
  recebivelId: number | null = null;
  moedaPagamento: Moeda = 'BRL';
  resultado: Liquidacao | null = null;
  erro = '';
  carregando = false;

  consultaId: number | null = null;
  consulta: Liquidacao | null = null;
  erroConsulta = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.carregarAbertos(); }

  carregarAbertos(): void {
    this.api.listarRecebiveis().subscribe({
      next: dados => this.recebiveisAbertos = dados.filter(r => r.status !== 'LIQUIDADO'),
      error: err => this.erro = extrairErro(err)
    });
  }

  liquidar(): void {
    this.erro = '';
    this.resultado = null;
    this.carregando = true;
    this.api.liquidar({ recebivelId: this.recebivelId!, moedaPagamento: this.moedaPagamento }).subscribe({
      next: liq => {
        this.resultado = liq;
        this.recebivelId = null;
        this.carregando = false;
        this.carregarAbertos();
      },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }

  consultar(): void {
    this.erroConsulta = '';
    this.consulta = null;
    this.api.buscarLiquidacaoPorRecebivel(this.consultaId!).subscribe({
      next: liq => this.consulta = liq,
      error: err => this.erroConsulta = extrairErro(err)
    });
  }
}
