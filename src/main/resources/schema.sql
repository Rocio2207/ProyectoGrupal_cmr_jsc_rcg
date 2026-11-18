-- Crear tabla para los consultorios
CREATE TABLE IF NOT EXISTS consultorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    tieneQuirofano BOOLEAN NOT NULL DEFAULT FALSE
);

-- Crear tabla para los veterinarios
CREATE TABLE IF NOT EXISTS veterinario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    aniosExperiencia INT NOT NULL CHECK (aniosExperiencia >= 0),
    fechaAlta DATE NOT NULL,
    consultorio_id INT NOT NULL UNIQUE,
    FOREIGN KEY (consultorio_id) REFERENCES consultorio(id)
);

-- Crear tabla para las mascotas
CREATE TABLE IF NOT EXISTS mascota (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    foto VARCHAR(255),
    fechaNacimiento DATE NOT NULL
);

-- Crear tabla para las fichas clínicas
CREATE TABLE IF NOT EXISTS fichaClinica (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vacunada BOOLEAN NOT NULL DEFAULT FALSE,
    fechaUltimaRevision DATE NOT NULL,
    mascota_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (mascota_id) REFERENCES mascota(id)
);

-- Crear tabla para los tratamientos
CREATE TABLE IF NOT EXISTS tratamiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    precioEuros INT NOT NULL CHECK (precioEuros > 0),
    requiereReceta BOOLEAN NOT NULL DEFAULT FALSE
);

-- Crear tabla para las consultas
CREATE TABLE IF NOT EXISTS consulta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora DATETIME NOT NULL,
    duracionMinutos INT NOT NULL CHECK (duracionMinutos > 0),
    urgente BOOLEAN NOT NULL DEFAULT FALSE,
    mascota_id BIGINT NOT NULL,
    veterinario_id INT NOT NULL,
    FOREIGN KEY (mascota_id) REFERENCES mascota(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

-- Tabla intermedia Consulta <-> Tratamiento
CREATE TABLE IF NOT EXISTS consulta_tratamiento (
    consulta_id BIGINT NOT NULL,
    tratamiento_id BIGINT NOT NULL,
    PRIMARY KEY (consulta_id, tratamiento_id),
    FOREIGN KEY (consulta_id) REFERENCES consulta(id),
    FOREIGN KEY (tratamiento_id) REFERENCES tratamiento(id)
);