import { Pipe, PipeTransform } from '@angular/core';

/**
 * Exibe datas no padrão brasileiro:
 * - data:      dd/MM/yyyy
 * - data/hora: dd/MM/yyyy HH:mm:ss
 * Aceita valores ISO (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss) e valores
 * já formatados pelo backend (dd/MM/yyyy...), que passam intactos.
 */
@Pipe({ name: 'dataBr', standalone: true })
export class DataBrPipe implements PipeTransform {

  transform(valor: string | null | undefined): string {
    if (!valor) return '';
    // já veio formatado do backend (dd/MM/yyyy...)
    if (valor.includes('/')) return valor;

    const [data, hora] = valor.split('T');
    const partes = data.split('-');
    if (partes.length !== 3) return valor;

    const [ano, mes, dia] = partes;
    const dataBr = `${dia}/${mes}/${ano}`;
    if (!hora) return dataBr;

    return `${dataBr} ${hora.slice(0, 8)}`;
  }
}
