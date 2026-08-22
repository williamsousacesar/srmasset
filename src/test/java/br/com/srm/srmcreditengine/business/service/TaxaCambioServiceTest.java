package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.repository.TaxaCambioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxaCambioServiceTest {

    @Mock
    private TaxaCambioRepository taxaCambioRepository;

    private TaxaCambioService servico;

    @BeforeEach
    void configurar() {
        servico = new TaxaCambioService(taxaCambioRepository);
    }

    @Test
    @DisplayName("Mesma moeda: taxa 1 sem consultar o repositorio")
    void deveRetornarTaxaUmParaMesmaMoeda() {
        TaxaCambio taxa = servico.obterTaxa(Moeda.BRL, Moeda.BRL);

        assertThat(taxa.getValor()).isEqualByComparingTo(BigDecimal.ONE);
        verifyNoInteractions(taxaCambioRepository);
    }

    @Test
    @DisplayName("Par cadastrado: retorna a taxa persistida")
    void deveRetornarTaxaCadastrada() {
        TaxaCambio cadastrada = TaxaCambio.builder()
                .moedaOrigem(Moeda.USD).moedaDestino(Moeda.BRL).valor(new BigDecimal("5.40")).build();
        when(taxaCambioRepository.findByMoedaOrigemAndMoedaDestino(Moeda.USD, Moeda.BRL))
                .thenReturn(Optional.of(cadastrada));

        TaxaCambio taxa = servico.obterTaxa(Moeda.USD, Moeda.BRL);

        assertThat(taxa.getValor()).isEqualByComparingTo("5.40");
    }

    @Test
    @DisplayName("Par nao cadastrado: lanca RecursoNaoEncontradoException")
    void deveFalharQuandoParNaoCadastrado() {
        when(taxaCambioRepository.findByMoedaOrigemAndMoedaDestino(Moeda.EUR, Moeda.USD))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> servico.obterTaxa(Moeda.EUR, Moeda.USD))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("EUR")
                .hasMessageContaining("USD");
    }

    @Test
    @DisplayName("Atualizacao manual cria a taxa quando o par nao existe")
    void deveCriarTaxaQuandoParNaoExiste() {
        when(taxaCambioRepository.findByMoedaOrigemAndMoedaDestino(Moeda.USD, Moeda.BRL))
                .thenReturn(Optional.empty());
        when(taxaCambioRepository.save(any(TaxaCambio.class))).thenAnswer(inv -> inv.getArgument(0));

        TaxaCambio taxa = servico.atualizarTaxaManualmente(Moeda.USD, Moeda.BRL, new BigDecimal("5.55"));

        assertThat(taxa.getValor()).isEqualByComparingTo("5.55");
        assertThat(taxa.getDataAtualizacao()).isNotNull();
        verify(taxaCambioRepository).save(any(TaxaCambio.class));
    }
}
