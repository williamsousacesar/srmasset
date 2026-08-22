import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CedentesComponent } from './components/cedentes.component';
import { RecebiveisComponent } from './components/recebiveis.component';
import { CambioComponent } from './components/cambio.component';
import { LiquidacoesComponent } from './components/liquidacoes.component';
import { ExtratoComponent } from './components/extrato.component';

type Aba = 'cedentes' | 'recebiveis' | 'cambio' | 'liquidacoes' | 'extrato';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, CedentesComponent, RecebiveisComponent, CambioComponent,
    LiquidacoesComponent, ExtratoComponent],
  template: `
    <header class="topo">
      <div class="topo-conteudo">
        <a class="marca" href="/" title="SRM Asset">
          <svg class="marca-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 29.7031" fill="none" aria-hidden="true">
            <path d="M0.00489387 7.27925V21.8279L12.654 1.36185e-07L0.00489387 7.27925ZM0.350909 22.4287L12.9999 29.7031L25.6491 22.4287H0.350909ZM13.346 0.00493001L25.9951 21.8328V7.28418L13.346 0.00493001Z" fill="#28408D"/>
          </svg>
          <span class="marca-texto">
            <strong>SRM</strong><span class="marca-sub">asset</span>
          </span>
        </a>
        <div class="topo-titulo">
          <h1>Credit Engine</h1>
          <p>Antecipação de recebíveis — cedentes, câmbio e liquidações</p>
        </div>
      </div>
      <nav class="tabs">
        <button [class.ativa]="aba === 'cedentes'" (click)="aba = 'cedentes'">Cedentes</button>
        <button [class.ativa]="aba === 'recebiveis'" (click)="aba = 'recebiveis'">Recebíveis</button>
        <button [class.ativa]="aba === 'cambio'" (click)="aba = 'cambio'">Câmbio</button>
        <button [class.ativa]="aba === 'liquidacoes'" (click)="aba = 'liquidacoes'">Liquidações</button>
        <button [class.ativa]="aba === 'extrato'" (click)="aba = 'extrato'">Extrato</button>
      </nav>
    </header>
    <main>
      <app-cedentes *ngIf="aba === 'cedentes'"></app-cedentes>
      <app-recebiveis *ngIf="aba === 'recebiveis'"></app-recebiveis>
      <app-cambio *ngIf="aba === 'cambio'"></app-cambio>
      <app-liquidacoes *ngIf="aba === 'liquidacoes'"></app-liquidacoes>
      <app-extrato *ngIf="aba === 'extrato'"></app-extrato>
    </main>
  `
})
export class AppComponent {
  aba: Aba = 'cedentes';
}
