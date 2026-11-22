package org.alixar.daw2.veterinaria.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
 * La clase `Mascota` representa una entidad que modela una mascota de la clínica veterinaria.
 * Contiene el identificador, el nombre, el nombre del fichero de la foto guardada en el servidor,
 * la fecha de nacimiento y las relaciones con su ficha clínica (uno a uno) y sus consultas (uno a muchos).
 *
 * Las anotaciones de Lombok generan getters, setters, constructores y otros métodos estándar.
 * Las relaciones inversas se excluyen de `toString`, `equals` y `hashCode` para evitar recursiones infinitas.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "mascota") // Nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    // Identificador único de la mascota. Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la mascota (obligatorio).
    @NotBlank(message = "{msg.pet.nombre.notBlank}")
    @Size(max = 100, message = "{msg.pet.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Nombre del fichero de la foto guardado en el servidor (opcional).
    @Column(name = "foto")
    private String foto;

    // Fecha de nacimiento de la mascota (obligatoria y no futura).
    @NotNull(message = "{msg.pet.fechaNacimiento.notNull}")
    @PastOrPresent(message = "{msg.pet.fechaNacimiento.past}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechaNacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    // Relación uno a uno inversa con `FichaClinica`: la clave foránea está en la tabla fichaClinica.
    @OneToOne(mappedBy = "mascota", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FichaClinica fichaClinica;

    // Relación uno a muchos con `Consulta`: una mascota puede tener muchas consultas.
    @OneToMany(mappedBy = "mascota", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Consulta> consultas;
}

