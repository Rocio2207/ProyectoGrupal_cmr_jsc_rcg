-- ------------------------------------------------------------
-- Consultorio
-- ------------------------------------------------------------
CREATE TABLE consultorio (
                             id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                             numero          INT     NOT NULL,
                             tieneQuirofano  BOOLEAN NOT NULL DEFAULT FALSE,
                             CONSTRAINT uk_consultorio_numero UNIQUE (numero)
);

-- ------------------------------------------------------------
-- Veterinario
-- ------------------------------------------------------------
CREATE TABLE veterinario (
                             id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nombre            VARCHAR(100) NOT NULL,
                             aniosExperiencia  INT          NOT NULL,
                             fechaAlta         DATE         NOT NULL,
                             consultorio_id    BIGINT       NOT NULL,
                             CONSTRAINT uk_veterinario_nombre      UNIQUE (nombre),
                             CONSTRAINT uk_veterinario_consultorio UNIQUE (consultorio_id),
                             CONSTRAINT ck_veterinario_anios       CHECK (aniosExperiencia >= 0),
                             CONSTRAINT fk_veterinario_consultorio FOREIGN KEY (consultorio_id)
                                 REFERENCES consultorio (id)
);

-- ------------------------------------------------------------
-- Mascota
-- ------------------------------------------------------------
CREATE TABLE mascota (
                         id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre           VARCHAR(100) NOT NULL,
                         foto             VARCHAR(255),
                         fechaNacimiento  DATE         NOT NULL
);

-- ------------------------------------------------------------
-- FichaClinica
-- ------------------------------------------------------------
CREATE TABLE fichaClinica (
                              id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
                              vacunada             BOOLEAN NOT NULL DEFAULT FALSE,
                              fechaUltimaRevision  DATE    NOT NULL,
                              mascota_id           BIGINT  NOT NULL,
                              CONSTRAINT uk_ficha_mascota UNIQUE (mascota_id),
                              CONSTRAINT fk_ficha_mascota FOREIGN KEY (mascota_id)
                                  REFERENCES mascota (id)
);

-- ------------------------------------------------------------
-- Tratamiento
-- ------------------------------------------------------------
CREATE TABLE tratamiento (
                             id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nombre           VARCHAR(100) NOT NULL,
                             precioEuros      INT          NOT NULL,
                             requiereReceta   BOOLEAN      NOT NULL DEFAULT FALSE,
                             CONSTRAINT uk_tratamiento_nombre UNIQUE (nombre),
                             CONSTRAINT ck_tratamiento_precio CHECK (precioEuros > 0)
);

-- ------------------------------------------------------------
-- Consulta
-- ------------------------------------------------------------
CREATE TABLE consulta (
                          id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                          fechaHora        DATETIME NOT NULL,
                          duracionMinutos  INT      NOT NULL,
                          urgente          BOOLEAN  NOT NULL DEFAULT FALSE,
                          mascota_id       BIGINT   NOT NULL,
                          veterinario_id   BIGINT   NOT NULL,
                          CONSTRAINT ck_consulta_duracion     CHECK (duracionMinutos > 0),
                          CONSTRAINT fk_consulta_mascota      FOREIGN KEY (mascota_id)
                              REFERENCES mascota (id),
                          CONSTRAINT fk_consulta_veterinario  FOREIGN KEY (veterinario_id)
                              REFERENCES veterinario (id)
);

-- ------------------------------------------------------------
-- Tabla intermedia de Consulta <-> Tratamiento
-- ------------------------------------------------------------
CREATE TABLE consulta_tratamiento (
                                      consulta_id     BIGINT NOT NULL,
                                      tratamiento_id  BIGINT NOT NULL,
                                      PRIMARY KEY (consulta_id, tratamiento_id),
                                      CONSTRAINT fk_ct_consulta    FOREIGN KEY (consulta_id)
                                          REFERENCES consulta (id),
                                      CONSTRAINT fk_ct_tratamiento FOREIGN KEY (tratamiento_id)
                                          REFERENCES tratamiento (id)
);