package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.repository.TaxaCambioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaxaCambioService {

    private final TaxaCambioRepository taxaCambioRepository;

    public TaxaCambioService(TaxaCambioRepository taxaCambioRepository) {
        this.taxaCambioRepository = taxaCambioRepository;
    }

    @Transactional(readOnly = true)
    public List<TaxaCambio> listarTaxas() {

        return taxaCambioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TaxaCambio obterTaxa(Moeda moedaOrigem, Moeda moedaDestino) {
        if (moedaOrigem == moedaDestino) {
            return TaxaCambio.builder()
                    .moedaOrigem(moedaOrigem)
                    .moedaDestino(moedaDestino)
                    .valor(BigDecimal.ONE)
                    .dataAtualizacao(LocalDateTime.now())
                    .build();
        }
        return taxaCambioRepository.findByMoedaOrigemAndMoedaDestino(moedaOrigem, moedaDestino)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Taxa de cambio nao cadastrada para o par %s/%s".formatted(moedaOrigem, moedaDestino)));
    }

    @Transactional
    public TaxaCambio atualizarTaxaManualmente(Moeda moedaOrigem, Moeda moedaDestino, BigDecimal novoValor) {
        TaxaCambio taxaCambio = taxaCambioRepository.findByMoedaOrigemAndMoedaDestino(moedaOrigem, moedaDestino)
                .orElseGet(() -> TaxaCambio.builder()
                        .moedaOrigem(moedaOrigem)
                        .moedaDestino(moedaDestino)
                        .build());
        taxaCambio.setValor(novoValor);
        taxaCambio.setDataAtualizacao(LocalDateTime.now());
        return taxaCambioRepository.save(taxaCambio);
    }

}
