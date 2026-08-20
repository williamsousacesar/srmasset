package br.com.srm.srmcreditengine.persistence.repository;

import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecebivelRepository extends JpaRepository<Recebivel, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Recebivel r where r.id = :id")
    Optional<Recebivel> buscarComBloqueioPorId(@Param("id") Long id);

    @Query("select r from Recebivel r join fetch r.cedente where r.id = :id")
    Optional<Recebivel> buscarComCedentePorId(@Param("id") Long id);

    @Query("select r from Recebivel r join fetch r.cedente order by r.id")
    List<Recebivel> listarTodosComCedente();
}
