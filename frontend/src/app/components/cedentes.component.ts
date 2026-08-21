import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, extrairErro } from '../services/api.service';
import { Cedente } from '../models/api.models';
import { DataBrPipe } from '../pipes/data-br.pipe';

@Component({
  selector: 'app-cedentes',
  standalone: true,
  imports: [CommonModule, FormsModule, DataBrPipe],
  template: `
    <div class="cartao">
      <h2>Cadastrar Cedente</h2>
      <form class="grade" (ngSubmit)="cadastrar()">
        <label>Nome
          <input [(ngModel)]="nome" name="nome" required placeholder="Empresa XYZ Ltda">
        </label>
        <label>Documento (CNPJ/CPF)
          <input [(ngModel)]="documento" name="documento" required placeholder="12.345.678/0001-90">
        </label>
        <button class="acao" type="submit" [disabled]="!nome || !documento || carregando">Cadastrar</button>
      </form>
      <div class="mensagem sucesso" *ngIf="sucesso">{{ sucesso }}</div>
      <div class="mensagem erro" *ngIf="erro">{{ erro }}</div>
    </div>

    <div class="cartao">
      <h2>Consultar Cedente por ID</h2>
      <form class="grade" (ngSubmit)="consultar()">
        <label>ID do Cedente
          <input type="number" min="1" [(ngModel)]="idConsulta" name="idConsulta" required placeholder="1">
        </label>
        <button class="acao" type="submit" [disabled]="idConsulta == null || consultando">
          {{ consultando ? 'Consultando...' : 'Consultar' }}
        </button>
      </form>
      <div class="mensagem erro" *ngIf="erroConsulta">{{ erroConsulta }}</div>
      <table *ngIf="cedente">
        <thead>
          <tr><th>ID</th><th>Nome</th><th>Documento</th><th>Data de Inclusão</th></tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ cedente.id }}</td>
            <td>{{ cedente.nome }}</td>
            <td>{{ cedente.documento }}</td>
            <td>{{ cedente.dataInclusao | dataBr }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class CedentesComponent {
  nome = '';
  documento = '';
  sucesso = '';
  erro = '';
  carregando = false;

  idConsulta: number | null = null;
  cedente: Cedente | null = null;
  erroConsulta = '';
  consultando = false;

  constructor(private api: ApiService) {}

  consultar(): void {
    if (this.idConsulta == null) { return; }
    this.erroConsulta = '';
    this.cedente = null;
    this.consultando = true;
    this.api.buscarCedentePorId(this.idConsulta).subscribe({
      next: c => { this.cedente = c; this.consultando = false; },
      error: err => { this.erroConsulta = extrairErro(err); this.consultando = false; }
    });
  }

  cadastrar(): void {
    this.sucesso = this.erro = '';
    this.carregando = true;
    this.api.cadastrarCedente({ nome: this.nome, documento: this.documento }).subscribe({
      next: c => {
        this.sucesso = `Cedente "${c.nome}" cadastrado com id ${c.id}.`;
        this.nome = this.documento = '';
        this.carregando = false;
      },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }
}
