package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA de `Veterinario`.
 */
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    /** Comprueba si ya existe un veterinario con ese nombre (alta). */
    @Query("SELECT COUNT(v) > 0 FROM Veterinario v WHERE v.nombre = :nombre")
    boolean existsVeterinarioByNombre(@Param("nombre") String nombre);

    /** Comprueba si otro veterinario distinto del indicado tiene ese nombre (edición). */
    @Query("SELECT COUNT(v) > 0 FROM Veterinario v WHERE v.nombre = :nombre AND v.id != :id")
    boolean existsVeterinarioByNombreAndNotId(@Param("nombre") String nombre, @Param("id") Long id);

    /** Comprueba si ese consultorio ya está asignado a algún veterinario (alta). */
    @Query("SELECT COUNT(v) > 0 FROM Veterinario v WHERE v.consultorio.id = :consultorioId")
    boolean existsVeterinarioByConsultorioId(@Param("consultorioId") Long consultorioId);

    /** Comprueba si ese consultorio está asignado a otro veterinario distinto del indicado (edición). */
    @Query("SELECT COUNT(v) > 0 FROM Veterinario v WHERE v.consultorio.id = :consultorioId AND v.id != :id")
    boolean existsVeterinarioByConsultorioIdAndNotId(@Param("consultorioId") Long consultorioId, @Param("id") Long id);
}
