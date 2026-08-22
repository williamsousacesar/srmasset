package br.com.srm.srmcreditengine.presentation.dto;

import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta com os dados de uma taxa de cambio.
 */
public record TaxaCambioRespostaDTO(
        Long id,
        Moeda moedaOrigem,
        Moeda moedaDestino,
        BigDecimal valor,
        LocalDateTime dataAtualizacao) {//,
        //String origemAtualizacao) {

    public static TaxaCambioRespostaDTO apartirDe(TaxaCambio taxaCambio) {
        return new TaxaCambioRespostaDTO(
                taxaCambio.getId(),
                taxaCambio.getMoedaOrigem(),
                taxaCambio.getMoedaDestino(),
                taxaCambio.getValor(),
                taxaCambio.getDataAtualizacao());//,
                //taxaCambio.getOrigemAtualizacao());
    }
}
