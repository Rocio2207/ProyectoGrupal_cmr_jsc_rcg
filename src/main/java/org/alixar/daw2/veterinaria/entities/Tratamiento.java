package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * La clase `Tratamiento` representa un tratamiento que se puede aplicar en una consulta.
 * Es el lado inverso de la relación muchos a muchos con `Consulta`
 * (la tabla intermedia `consulta_tratamiento` se define en `Consulta`).
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "tratamiento") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tratamiento {

    // Identificador único del tratamiento. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del tratamiento (obligatorio y único).
    @NotBlank(message = "{msg.treatment.nombre.notBlank}")
    @Size(max = 100, message = "{msg.treatment.nombre.size}")
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    // Precio en euros (mayor que 0).
    @Min(value = 1, message = "{msg.treatment.precioEuros.min}")
    @Column(name = "precioEuros", nullable = false)
    private int precioEuros;

    // Indica si el tratamiento requiere receta.
    @Column(name = "requiereReceta", nullable = false)
    private boolean requiereReceta;

    // Relación muchos a muchos inversa con `Consulta`.
    @ManyToMany(mappedBy = "tratamientos", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Consulta> consultas;
}
