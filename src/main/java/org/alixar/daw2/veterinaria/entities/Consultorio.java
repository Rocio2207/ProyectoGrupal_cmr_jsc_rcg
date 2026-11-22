package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * La clase `Consultorio` representa un consultorio de la clínica veterinaria.
 * Es el lado inverso de la relación uno a uno con `Veterinario`
 * (la clave foránea `consultorio_id` está en la tabla veterinario).
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "consultorio") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultorio {

    // Identificador único del consultorio. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número del consultorio (obligatorio y único). Si se deja vacío o no es un entero,
    // el formulario muestra el mensaje `typeMismatch.int`.
    @Column(name = "numero", nullable = false, unique = true)
    private int numero;

    // Indica si el consultorio dispone de quirófano.
    @Column(name = "tieneQuirofano", nullable = false)
    private boolean tieneQuirofano;

    // Relación uno a uno inversa con `Veterinario`.
    @OneToOne(mappedBy = "consultorio", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Veterinario veterinario;
}
