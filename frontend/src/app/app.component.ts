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
      <h1>SRM Credit Engine</h1>
      <p>Antecipação de recebíveis — gestão de cedentes, câmbio e liquidações</p>
    </header>
    <nav class="tabs">
      <button [class.ativa]="aba === 'cedentes'" (click)="aba = 'cedentes'">Cedentes</button>
      <button [class.ativa]="aba === 'recebiveis'" (click)="aba = 'recebiveis'">Recebíveis</button>
      <button [class.ativa]="aba === 'cambio'" (click)="aba = 'cambio'">Câmbio</button>
      <button [class.ativa]="aba === 'liquidacoes'" (click)="aba = 'liquidacoes'">Liquidações</button>
      <button [class.ativa]="aba === 'extrato'" (click)="aba = 'extrato'">Extrato</button>
    </nav>
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
