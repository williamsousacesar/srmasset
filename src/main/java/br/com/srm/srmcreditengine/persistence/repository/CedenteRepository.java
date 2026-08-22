package br.com.srm.srmcreditengine.persistence.repository;

import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CedenteRepository extends JpaRepository<Cedente, Long> {
}
