package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * La clase `Consulta` representa una consulta veterinaria.
 * Es el lado propietario de las relaciones con `Mascota` y `Veterinario` (muchos a uno)
 * y de la relación muchos a muchos con `Tratamiento` (tabla intermedia `consulta_tratamiento`).
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "consulta") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    // Identificador único de la consulta. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha y hora de la consulta (obligatoria).
    @NotNull(message = "{msg.consultation.fechaHora.notNull}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    // Duración en minutos (mayor que 0).
    @Min(value = 1, message = "{msg.consultation.duracionMinutos.min}")
    @Column(name = "duracionMinutos", nullable = false)
    private int duracionMinutos;

    // Indica si la consulta es urgente.
    @Column(name = "urgente", nullable = false)
    private boolean urgente;

    // Tratamientos aplicados en la consulta. Lado propietario de la tabla intermedia `consulta_tratamiento`.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "consulta_tratamiento",
            joinColumns = @JoinColumn(name = "consulta_id"),
            inverseJoinColumns = @JoinColumn(name = "tratamiento_id"))
    private List<Tratamiento> tratamientos = new ArrayList<>();

    // Mascota atendida (obligatoria). En el listado se muestra su nombre.
    @NotNull(message = "{msg.consultation.mascota.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    // Veterinario que atiende la consulta (obligatorio). En el listado se muestra su nombre.
    @NotNull(message = "{msg.consultation.veterinario.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;


}