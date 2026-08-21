import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro } from '../services/api.service';
import { Recebivel, MOEDAS, TIPOS_RECEBIVEL, Moeda, TipoRecebivel } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-recebiveis',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Cadastrar Recebível</h2>
      <form class="grade" (ngSubmit)="cadastrar()">
        <label>ID do Cedente
          <input type="number" min="1" [(ngModel)]="cedenteId" name="cedenteId" required placeholder="1">
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
      <h2>Consultar Recebível por ID</h2>
      <form class="grade" (ngSubmit)="consultar()">
        <label>ID do Recebível
          <input type="number" min="1" [(ngModel)]="idConsulta" name="idConsulta" required placeholder="1">
        </label>
        <button class="acao" type="submit" [disabled]="idConsulta == null || consultando">
          {{ consultando ? 'Consultando...' : 'Consultar' }}
        </button>
      </form>
      <div class="mensagem erro" *ngIf="erroConsulta">{{ erroConsulta }}</div>
      <table *ngIf="recebivel">
        <thead>
          <tr>
            <th>ID</th><th>Cedente</th><th>Tipo</th><th>Valor Face</th><th>Moeda</th>
            <th>Prazo</th><th>Vencimento</th><th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ recebivel.id }}</td>
            <td>{{ recebivel.nomeCedente }}</td>
            <td>{{ recebivel.tipo }}</td>
            <td>{{ recebivel.valorFace | number:'1.2-2' }}</td>
            <td>{{ recebivel.moedaTitulo }}</td>
            <td>{{ recebivel.prazoMeses }}m</td>
            <td>{{ recebivel.dataVencimento | dataBr }}</td>
            <td><span class="badge" [class.aberto]="recebivel.status !== 'LIQUIDADO'"
                  [class.liquidado]="recebivel.status === 'LIQUIDADO'">{{ recebivel.status }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class RecebiveisComponent {
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

  idConsulta: number | null = null;
  recebivel: Recebivel | null = null;
  erroConsulta = '';
  consultando = false;

  constructor(private api: ApiService) {}

  consultar(): void {
    if (this.idConsulta == null) { return; }
    this.erroConsulta = '';
    this.recebivel = null;
    this.consultando = true;
    this.api.buscarRecebivelPorId(this.idConsulta).subscribe({
      next: r => { this.recebivel = r; this.consultando = false; },
      error: err => { this.erroConsulta = extrairErro(err); this.consultando = false; }
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
      },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }
}
