package br.com.srm.srmcreditengine.persistence.repository;

import br.com.srm.srmcreditengine.persistence.entities.Liquidacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiquidacaoRepository extends JpaRepository<Liquidacao, Long> {

    Optional<Liquidacao> findByRecebivelId(Long recebivelId);
}
