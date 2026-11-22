package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.FichaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio Spring Data JPA de `FichaClinica`.
 */
public interface FichaClinicaRepository extends JpaRepository<FichaClinica, Long> {

    /** Comprueba si esa mascota ya tiene una ficha clínica (alta). */
    @Query("SELECT COUNT(f) > 0 FROM FichaClinica f WHERE f.mascota.id = :mascotaId")
    boolean existsFichaClinicaByMascotaId(@Param("mascotaId") Long mascotaId);

    /** Comprueba si esa mascota tiene una ficha clínica distinta de la indicada (edición). */
    @Query("SELECT COUNT(f) > 0 FROM FichaClinica f WHERE f.mascota.id = :mascotaId AND f.id != :id")
    boolean existsFichaClinicaByMascotaIdAndNotId(@Param("mascotaId") Long mascotaId, @Param("id") Long id);
}
