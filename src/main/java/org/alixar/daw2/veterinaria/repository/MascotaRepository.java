package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de `Mascota`. Los métodos CRUD (`findAll`, `findById`, `save`, `deleteById`...)
 * los aporta `JpaRepository`; `Mascota` no tiene campos únicos, así que no necesita consultas adicionales.
 */
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}

