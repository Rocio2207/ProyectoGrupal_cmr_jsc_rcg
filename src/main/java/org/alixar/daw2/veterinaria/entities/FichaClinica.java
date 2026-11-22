package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * La clase `FichaClinica` representa la ficha clínica de una mascota.
 * Es el lado propietario de la relación uno a uno con `Mascota`: la tabla fichaClinica
 * contiene la clave foránea `mascota_id`, que además es única.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "fichaClinica") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaClinica {

    // Identificador único de la ficha clínica. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Indica si la mascota está vacunada.
    @Column(name = "vacunada", nullable = false)
    private boolean vacunada;

    // Fecha de la última revisión (obligatoria).
    @NotNull(message = "{msg.clinical-record.fechaUltimaRevision.notNull}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechaUltimaRevision", nullable = false)
    private LocalDate fechaUltimaRevision;

    // Mascota a la que pertenece la ficha (obligatoria y única). Se elige en el formulario con un desplegable.
    @NotNull(message = "{msg.clinical-record.mascota.notNull}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false, unique = true)
    private Mascota mascota;
}

