package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.Consultorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA de `Consultorio`.
 */
public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {

    /** Comprueba si ya existe un consultorio con ese número (alta). */
    @Query("SELECT COUNT(c) > 0 FROM Consultorio c WHERE c.numero = :numero")
    boolean existsConsultorioByNumero(@Param("numero") int numero);

    /** Comprueba si otro consultorio distinto del indicado tiene ese número (edición). */
    @Query("SELECT COUNT(c) > 0 FROM Consultorio c WHERE c.numero = :numero AND c.id != :id")
    boolean existsConsultorioByNumeroAndNotId(@Param("numero") int numero, @Param("id") Long id);
}
