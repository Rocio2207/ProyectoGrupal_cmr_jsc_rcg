package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * La clase `Veterinario` representa un veterinario de la clínica.
 * Es el lado propietario de la relación uno a uno con `Consultorio` (clave foránea `consultorio_id`, única)
 * y el lado inverso de la relación uno a muchos con `Consulta`.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "veterinario") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    // Identificador único del veterinario. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del veterinario (obligatorio y único).
    @NotBlank(message = "{msg.vet.nombre.notBlank}")
    @Size(max = 100, message = "{msg.vet.nombre.size}")
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    // Años de experiencia (mayor o igual que 0).
    @Min(value = 0, message = "{msg.vet.aniosExperiencia.min}")
    @Column(name = "aniosExperiencia", nullable = false)
    private int aniosExperiencia;

    // Fecha de alta en la clínica (obligatoria).
    @NotNull(message = "{msg.vet.fechaAlta.notNull}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechaAlta", nullable = false)
    private LocalDate fechaAlta;

    // Consultorio asignado (obligatorio y único). Se elige en el formulario con un desplegable.
    @NotNull(message = "{msg.vet.consultorio.notNull}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultorio_id", nullable = false, unique = true)
    private Consultorio consultorio;

    // Relación uno a muchos con `Consulta`: un veterinario puede atender muchas consultas.
    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Consulta> consultas;
}
