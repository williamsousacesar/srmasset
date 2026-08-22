package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecebivelJaLiquidadoException;
import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import br.com.srm.srmcreditengine.persistence.entities.Liquidacao;
import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import br.com.srm.srmcreditengine.persistence.repository.LiquidacaoRepository;
import br.com.srm.srmcreditengine.persistence.repository.RecebivelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiquidacaoServiceTest {

    @Mock
    private RecebivelRepository recebivelRepository;

    @Mock
    private LiquidacaoRepository liquidacaoRepository;

    @Mock
    private CalculoValorPresenteService calculoValorPresenteService;

    @Mock
    private TaxaCambioService taxaCambioService;

    @Mock
    private ProvedorDeTaxaBase provedorDeTaxaBase;

    private LiquidacaoService servico;

    @BeforeEach
    void configurar() {
        servico = new LiquidacaoService(recebivelRepository, liquidacaoRepository,
                calculoValorPresenteService, taxaCambioService, provedorDeTaxaBase);
    }

    private Recebivel novoRecebivelPendente() {
        return Recebivel.builder()
                .id(1L)
                .cedente(Cedente.builder().id(10L).nome("Cedente Teste").build())
                .tipo(TipoRecebivel.DUPLICATA_MERCANTIL)
                .valorFace(new BigDecimal("10000.00"))
                .moedaTitulo(Moeda.BRL)
                .prazoMeses(3)
                .dataEmissao(LocalDate.now().minusDays(10))
                .dataVencimento(LocalDate.now().plusMonths(3))
                .status(StatusRecebivel.PENDENTE)
                .build();
    }

    @Test
    @DisplayName("Liquidacao na mesma moeda: sem conversao cambial (taxa = 1)")
    void deveLiquidarNaMesmaMoedaSemConversao() {
        Recebivel recebivel = novoRecebivelPendente();
        when(recebivelRepository.buscarComBloqueioPorId(1L)).thenReturn(Optional.of(recebivel));
        when(provedorDeTaxaBase.obterTaxaBaseMensal()).thenReturn(new BigDecimal("0.01"));
        when(calculoValorPresenteService.calcularValorPresente(any(), any(), any(), anyInt()))
                .thenReturn(new BigDecimal("9285.55"));
        when(calculoValorPresenteService.obterSpreadDoTipo(TipoRecebivel.DUPLICATA_MERCANTIL))
                .thenReturn(new BigDecimal("0.015"));
        when(liquidacaoRepository.save(any(Liquidacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Liquidacao liquidacao = servico.liquidar(1L, Moeda.BRL);

        assertThat(liquidacao.getValorPresenteMoedaTitulo()).isEqualByComparingTo("9285.55");
        assertThat(liquidacao.getValorPresenteMoedaPagamento()).isEqualByComparingTo("9285.55");
        assertThat(liquidacao.getTaxaCambioUtilizada()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(recebivel.getStatus()).isEqualTo(StatusRecebivel.LIQUIDADO);
        verify(taxaCambioService, never()).obterTaxa(any(), any());
    }

    @Test
    @DisplayName("Liquidacao cross-currency: aplica taxa de cambio no valor final")
    void deveAplicarConversaoCambialQuandoCrossCurrency() {
        Recebivel recebivel = novoRecebivelPendente();
        when(recebivelRepository.buscarComBloqueioPorId(1L)).thenReturn(Optional.of(recebivel));
        when(provedorDeTaxaBase.obterTaxaBaseMensal()).thenReturn(new BigDecimal("0.01"));
        when(calculoValorPresenteService.calcularValorPresente(any(), any(), any(), anyInt()))
                .thenReturn(new BigDecimal("10000.00"));
        when(calculoValorPresenteService.obterSpreadDoTipo(any())).thenReturn(new BigDecimal("0.015"));
        when(taxaCambioService.obterTaxa(Moeda.BRL, Moeda.USD)).thenReturn(TaxaCambio.builder()
                .moedaOrigem(Moeda.BRL).moedaDestino(Moeda.USD).valor(new BigDecimal("0.20")).build());
        when(liquidacaoRepository.save(any(Liquidacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Liquidacao liquidacao = servico.liquidar(1L, Moeda.USD);

        assertThat(liquidacao.getValorPresenteMoedaPagamento()).isEqualByComparingTo("2000.00");
        assertThat(liquidacao.getTaxaCambioUtilizada()).isEqualByComparingTo("0.20");
        assertThat(liquidacao.getMoedaPagamento()).isEqualTo(Moeda.USD);
    }

    @Test
    @DisplayName("Recebivel ja liquidado: deve lancar excecao e nao gravar nada")
    void deveFalharQuandoRecebivelJaLiquidado() {
        Recebivel recebivel = novoRecebivelPendente();
        recebivel.setStatus(StatusRecebivel.LIQUIDADO);
        when(recebivelRepository.buscarComBloqueioPorId(1L)).thenReturn(Optional.of(recebivel));

        assertThatThrownBy(() -> servico.liquidar(1L, Moeda.BRL))
                .isInstanceOf(RecebivelJaLiquidadoException.class);

        verify(liquidacaoRepository, never()).save(any());
        verify(recebivelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recebivel inexistente: deve lancar RecursoNaoEncontradoException")
    void deveFalharQuandoRecebivelNaoExiste() {
        when(recebivelRepository.buscarComBloqueioPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servico.liquidar(99L, Moeda.BRL))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }
}
