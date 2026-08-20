package br.com.srm.srmcreditengine.persistence.repository;

import br.com.srm.srmcreditengine.persistence.entities.TaxaCambio;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxaCambioRepository extends JpaRepository<TaxaCambio, Long> {

    Optional<TaxaCambio> findByMoedaOrigemAndMoedaDestino(Moeda moedaOrigem, Moeda moedaDestino);
}
