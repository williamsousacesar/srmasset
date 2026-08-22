package br.com.srm.srmcreditengine.business.strategy;

import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EstrategiaChequePreDatado implements EstrategiaDePrecificacao {

    private static final BigDecimal SPREAD_MENSAL = new BigDecimal("0.015");

    @Override
    public TipoRecebivel obterTipoSuportado() {

        return TipoRecebivel.CHEQUE_PRE_DATADO;
    }

    @Override
    public BigDecimal obterSpreadMensal() {

        return SPREAD_MENSAL;
    }
}
