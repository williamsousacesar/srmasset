package br.com.srm.srmcreditengine.business.strategy;

import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;

import java.math.BigDecimal;

public interface EstrategiaDePrecificacao {

    TipoRecebivel obterTipoSuportado();

    BigDecimal obterSpreadMensal();
}
