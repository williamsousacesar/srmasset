import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro } from '../services/api.service';
import { TaxaCambio, MOEDAS, Moeda } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-cambio',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Atualizar Taxa de Câmbio</h2>
      <form class="grade" (ngSubmit)="atualizar()">
        <label>Moeda de Origem
          <select [(ngModel)]="moedaOrigem" name="moedaOrigem" required>
            <option *ngFor="let m of moedas" [ngValue]="m">{{ m }}</option>
          </select>
        </label>
        <label>Moeda de Destino
          <select [(ngModel)]="moedaDestino" name="moedaDestino" required>
            <option *ngFor="let m of moedas" [ngValue]="m">{{ m }}</option>
          </select>
        </label>
        <label>Valor da Taxa
          <input type="number" step="0.0001" min="0.0001" [(ngModel)]="valor" name="valor" required placeholder="5.4321">
        </label>
        <button class="acao" type="submit" [disabled]="valor == null || carregando">Atualizar</button>
      </form>
      <div class="mensagem sucesso" *ngIf="sucesso">{{ sucesso }}</div>
      <div class="mensagem erro" *ngIf="erro">{{ erro }}</div>
    </div>

    <div class="cartao">
      <h2>Taxas Cadastradas</h2>
      <table *ngIf="taxas.length; else vazio">
        <thead>
          <tr><th>ID</th><th>Origem</th><th>Destino</th><th>Valor</th><th>Última Atualização</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let t of taxas">
            <td>{{ t.id }}</td>
            <td>{{ t.moedaOrigem }}</td>
            <td>{{ t.moedaDestino }}</td>
            <td>{{ t.valor | number:'1.4-6' }}</td>
            <td>{{ t.dataAtualizacao | dataBr }}</td>
          </tr>
        </tbody>
      </table>
      <ng-template #vazio><p>Nenhuma taxa cadastrada.</p></ng-template>
    </div>
  `
})
export class CambioComponent implements OnInit {
  taxas: TaxaCambio[] = [];
  moedas = MOEDAS;
  moedaOrigem: Moeda = 'USD';
  moedaDestino: Moeda = 'BRL';
  valor: number | null = null;
  sucesso = '';
  erro = '';
  carregando = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.listar(); }

  listar(): void {
    this.api.listarTaxas().subscribe({
      next: dados => this.taxas = dados,
      error: err => this.erro = extrairErro(err)
    });
  }

  atualizar(): void {
    this.sucesso = this.erro = '';
    if (this.moedaOrigem === this.moedaDestino) {
      this.erro = 'Moedas de origem e destino devem ser diferentes.';
      return;
    }
    this.carregando = true;
    this.api.atualizarTaxa({ moedaOrigem: this.moedaOrigem, moedaDestino: this.moedaDestino, valor: this.valor! })
      .subscribe({
        next: t => {
          this.sucesso = `Taxa ${t.moedaOrigem}→${t.moedaDestino} atualizada para ${t.valor}.`;
          this.valor = null;
          this.carregando = false;
          this.listar();
        },
        error: err => { this.erro = extrairErro(err); this.carregando = false; }
      });
  }
}
