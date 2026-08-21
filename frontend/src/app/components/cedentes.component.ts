import { Component, OnInit } from '@angular/core';
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
      <h2>Cedentes Cadastrados</h2>
      <table *ngIf="cedentes.length; else vazio">
        <thead>
          <tr><th>ID</th><th>Nome</th><th>Documento</th><th>Data de Inclusão</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let c of cedentes">
            <td>{{ c.id }}</td>
            <td>{{ c.nome }}</td>
            <td>{{ c.documento }}</td>
            <td>{{ c.dataInclusao | dataBr }}</td>
          </tr>
        </tbody>
      </table>
      <ng-template #vazio><p>Nenhum cedente cadastrado.</p></ng-template>
    </div>
  `
})
export class CedentesComponent implements OnInit {
  cedentes: Cedente[] = [];
  nome = '';
  documento = '';
  sucesso = '';
  erro = '';
  carregando = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void { this.listar(); }

  listar(): void {
    this.api.listarCedentes().subscribe({
      next: dados => this.cedentes = dados,
      error: err => this.erro = extrairErro(err)
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
        this.listar();
      },
      error: err => { this.erro = extrairErro(err); this.carregando = false; }
    });
  }
}
