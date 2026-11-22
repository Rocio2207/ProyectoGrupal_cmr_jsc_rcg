package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA de `Tratamiento`.
 */
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    /** Comprueba si ya existe un tratamiento con ese nombre (alta). */
    @Query("SELECT COUNT(t) > 0 FROM Tratamiento t WHERE t.nombre = :nombre")
    boolean existsTratamientoByNombre(@Param("nombre") String nombre);

    /** Comprueba si otro tratamiento distinto del indicado tiene ese nombre (edición). */
    @Query("SELECT COUNT(t) > 0 FROM Tratamiento t WHERE t.nombre = :nombre AND t.id != :id")
    boolean existsTratamientoByNombreAndNotId(@Param("nombre") String nombre, @Param("id") Long id);
}
