import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro } from '../services/api.service';
import { Cedente, Recebivel, MOEDAS, TIPOS_RECEBIVEL, Moeda, TipoRecebivel } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-recebiveis',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Cadastrar Recebível</h2>
      <form class="grade" (ngSubmit)="cadastrar()">
        <label>Cedente
          <select [(ngModel)]="cedenteId" name="cedenteId" required>
            <option [ngValue]="null" disabled>Selecione...</option>
            <option *ngFor="let c of cedentes" [ngValue]="c.id">{{ c.id }} — {{ c.nome }}</option>
          </select>
        </label>
        <label>Tipo
          <select [(ngModel)]="tipo" name="tipo" required>
            <option *ngFor="let t of tipos" [ngValue]="t">{{ t }}</option>
          </select>
        </label>
        <label>Valor de Face
          <input type="number" step="0.01" min="0.01" [(ngModel)]="valorFace" name="valorFace" required>
        </label>
        <label>Moeda do Título
          <select [(ngModel)]="moedaTitulo" name="moedaTitulo" required>
            <option *ngFor="let m of moedas" [ngValue]="m">{{ m }}</option>
          </select>
        </label>
        <label>Prazo (meses)
          <input type="number" min="1" [(ngModel)]="prazoMeses" name="prazoMeses" required>
        </label>
        <label>Data de Emissão
          <input type="date" [(ngModel)]="dataEmissao" name="dataEmissao" required>
        </label>
        <label>Data de Vencimento
          <input type="date" [(ngModel)]="dataVencimento" name="dataVencimento" required>
        </label>
        <button class="acao" type="submit" [disabled]="carregando">Cadastrar</button>
      </form>
      <div class="mensagem sucesso" *ngIf="sucesso">{{ sucesso }}</div>
      <div class="mensagem erro" *ngIf="erro">{{ erro }}</div>
    </div>

    <div class="cartao">
      <h2>Recebíveis Cadastrados</h2>
      <table *ngIf="recebiveis.length; else vazio">
        <thead>
          <tr>
            <th>ID</th><th>Cedente</th><th>Tipo</th><th>Valor Face</th><th>Moeda</th>
            <th>Prazo</th><th>Vencimento</th><th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let r of recebiveis">
            <td>{{ r.id }}</td>
            <td>{{ r.nomeCedente }}</td>
            <td>{{ r.tipo }}</td>
            <td>{{ r.valorFace | number:'1.2-2' }}</td>
            <td>{{ r.moedaTitulo }}</td>
            <td>{{ r.prazoMeses }}m</td>
            <td>{{ r.dataVencimento | dataBr }}</td>
            <td><span class="badge" [class.aberto]="r.status !== 'LIQUIDADO'"
                  [class.liquidado]="r.status === 'LIQUIDADO'">{{ r.status }}</span></td>
          </tr>
        </tbody>
      </table>
      <ng-template #vazio><p>Nenhum recebível cadastrado.</p></ng-template>
    </div>
  `
})
export class RecebiveisComponent implements OnInit {
  recebiveis: Recebivel[] = [];
  cedentes: Cedente[] = [];
  moedas = MOEDAS;
  tipos = TIPOS_RECEBIVEL;

  cedenteId: number | null = null;
  tipo: TipoRecebivel = 'DUPLICATA_MERCANTIL';
  valorFace: number | null = null;
  moedaTitulo: Moeda = 'BRL';
  prazoMeses: number | null = null;
  dataEmissao = '';
  dataVencimento = '';

  sucesso = '';
  erro = '';
  carregando = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.listar();
    this.api.listarCedentes().subscribe({ next: c => this.cedentes = c });
  }

  listar(): void {
    this.api.listarRecebiveis().subscribe({
      next: dados => this.recebiveis = dados,
      error: err => this.erro = extrairErro(err)
    });
  }

  cadastrar(): void {
    this.sucesso = this.erro = '';
    if (this.cedenteId == null || this.valorFace == null || this.prazoMeses == null
        || !this.dataEmissao || !this.dataVencimento) {
      this.erro = 'Preencha todos os campos.';
      return;
    }
    this.carregando = true;
    this.api.cadastrarRecebivel({
      cedenteId: this.cedenteId,
      tipo: this.tipo,
      valorFace: this.valorFace,
      moedaTitulo: this.moedaTitulo,
      prazoMeses: this.prazoMeses,
      dataEmissao: this.dataEmissao,
      dataVencimento: this.dataVencimento
    }).subscribe({
      next: r => {
        this.sucesso = `Recebível cadastrado com id ${r.id}.`;
        this.carregando = false;
        this.listar();
      },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }
}
