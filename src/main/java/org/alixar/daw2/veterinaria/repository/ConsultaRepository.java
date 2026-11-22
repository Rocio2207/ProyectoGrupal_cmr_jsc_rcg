package org.alixar.daw2.veterinaria.repository;

import org.alixar.daw2.veterinaria.entities.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA de `Consulta`. Los métodos CRUD los aporta `JpaRepository`;
 * `Consulta` no tiene campos únicos, así que no necesita consultas adicionales.
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}
