package br.com.srm.srmcreditengine.business.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProvedorDeTaxaBase {

    private final BigDecimal taxaBaseMensal;

    public ProvedorDeTaxaBase(@Value("${srm.precificacao.taxa-base-mensal:0.01}") BigDecimal taxaBaseMensal) {
        this.taxaBaseMensal = taxaBaseMensal;
    }

    public BigDecimal obterTaxaBaseMensal() {

        return taxaBaseMensal;
    }
}
