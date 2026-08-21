package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RegraDeNegocioException;
import br.com.srm.srmcreditengine.business.strategy.EstrategiaChequePreDatado;
import br.com.srm.srmcreditengine.business.strategy.EstrategiaDuplicataMercantil;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculoValorPresenteServiceTest {

    private static final BigDecimal TAXA_BASE_MENSAL = new BigDecimal("0.01");

    private CalculoValorPresenteService servico;

    @BeforeEach
    void configurar() {
        servico = new CalculoValorPresenteService(
                List.of(new EstrategiaDuplicataMercantil(), new EstrategiaChequePreDatado()));
    }

    @Test
    @DisplayName("Duplicata mercantil: VP = 10000 / (1 + 0.01 + 0.015)^3 = 9285.99")
    void deveCalcularValorPresenteDeDuplicataMercantil() {
        BigDecimal valorPresente = servico.calcularValorPresente(
                new BigDecimal("10000.00"), TipoRecebivel.DUPLICATA_MERCANTIL, TAXA_BASE_MENSAL, 3);

        assertThat(valorPresente).isEqualByComparingTo("9285.99");
    }

    @Test
    @DisplayName("Cheque pre-datado: VP = 10000 / (1 + 0.01 + 0.025)^3 = 9019.43")
    void deveCalcularValorPresenteDeChequePreDatado() {
        BigDecimal valorPresente = servico.calcularValorPresente(
                new BigDecimal("10000.00"), TipoRecebivel.CHEQUE_PRE_DATADO, TAXA_BASE_MENSAL, 3);

        assertThat(valorPresente).isEqualByComparingTo("9019.43");
    }

    @Test
    @DisplayName("Cheque pre-datado deve ter valor presente menor que duplicata (spread maior)")
    void chequeDeveTerValorPresenteMenorQueDuplicata() {
        BigDecimal valorFace = new BigDecimal("5000.00");

        BigDecimal vpDuplicata = servico.calcularValorPresente(
                valorFace, TipoRecebivel.DUPLICATA_MERCANTIL, TAXA_BASE_MENSAL, 2);
        BigDecimal vpCheque = servico.calcularValorPresente(
                valorFace, TipoRecebivel.CHEQUE_PRE_DATADO, TAXA_BASE_MENSAL, 2);

        assertThat(vpCheque).isLessThan(vpDuplicata);
    }

    @Test
    @DisplayName("Prazo de 1 mes: desconto de apenas um periodo")
    void deveCalcularComPrazoDeUmMes() {
        BigDecimal valorPresente = servico.calcularValorPresente(
                new BigDecimal("1025.00"), TipoRecebivel.DUPLICATA_MERCANTIL, TAXA_BASE_MENSAL, 1);

        // 1025 / 1.025 = 1000.00
        assertThat(valorPresente).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Spread do tipo deve refletir a estrategia cadastrada")
    void deveObterSpreadDoTipo() {
        assertThat(servico.obterSpreadDoTipo(TipoRecebivel.DUPLICATA_MERCANTIL))
                .isEqualByComparingTo("0.015");
        assertThat(servico.obterSpreadDoTipo(TipoRecebivel.CHEQUE_PRE_DATADO))
                .isEqualByComparingTo("0.025");
    }

    @Test
    @DisplayName("Tipo sem estrategia cadastrada deve lancar RegraDeNegocioException")
    void deveFalharQuandoNaoHaEstrategiaParaOTipo() {
        CalculoValorPresenteService servicoSemEstrategias =
                new CalculoValorPresenteService(List.of(new EstrategiaDuplicataMercantil()));

        assertThatThrownBy(() -> servicoSemEstrategias.calcularValorPresente(
                BigDecimal.TEN, TipoRecebivel.CHEQUE_PRE_DATADO, TAXA_BASE_MENSAL, 1))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("CHEQUE_PRE_DATADO");
    }
}
