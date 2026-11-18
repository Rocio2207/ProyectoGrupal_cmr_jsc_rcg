-- Consultorios (ids 1 a 3)
INSERT INTO consultorio (numero, tieneQuirofano) VALUES (101, TRUE);
INSERT INTO consultorio (numero, tieneQuirofano) VALUES (102, FALSE);
INSERT INTO consultorio (numero, tieneQuirofano) VALUES (103, TRUE);

-- Veterinarios
INSERT INTO veterinario (nombre, aniosExperiencia, fechaAlta, consultorio_id)
VALUES ('Laura Gómez', 12, '2014-03-10', 1);
INSERT INTO veterinario (nombre, aniosExperiencia, fechaAlta, consultorio_id)
VALUES ('Miguel Torres', 5, '2021-09-01', 2);
INSERT INTO veterinario (nombre, aniosExperiencia, fechaAlta, consultorio_id)
VALUES ('Ana Ruiz', 0, '2026-09-15', 3);

-- Mascotas
INSERT INTO mascota (nombre, foto, fechaNacimiento) VALUES ('Max',   NULL, '2019-05-20');
INSERT INTO mascota (nombre, foto, fechaNacimiento) VALUES ('Luna',  NULL, '2021-11-02');
INSERT INTO mascota (nombre, foto, fechaNacimiento) VALUES ('Toby',  NULL, '2018-01-15');
INSERT INTO mascota (nombre, foto, fechaNacimiento) VALUES ('Nala',  NULL, '2023-07-30');
INSERT INTO mascota (nombre, foto, fechaNacimiento) VALUES ('Rocky', NULL, '2020-03-08');

-- Fichas clínicas
INSERT INTO fichaClinica (vacunada, fechaUltimaRevision, mascota_id) VALUES (TRUE,  '2026-10-05', 1);
INSERT INTO fichaClinica (vacunada, fechaUltimaRevision, mascota_id) VALUES (TRUE,  '2026-09-18', 2);
INSERT INTO fichaClinica (vacunada, fechaUltimaRevision, mascota_id) VALUES (FALSE, '2026-06-12', 3);
INSERT INTO fichaClinica (vacunada, fechaUltimaRevision, mascota_id) VALUES (TRUE,  '2026-11-03', 4);
INSERT INTO fichaClinica (vacunada, fechaUltimaRevision, mascota_id) VALUES (FALSE, '2026-04-27', 5);

-- Tratamientos
INSERT INTO tratamiento (nombre, precioEuros, requiereReceta) VALUES ('Vacuna antirrábica', 25, FALSE);
INSERT INTO tratamiento (nombre, precioEuros, requiereReceta) VALUES ('Desparasitación',    15, FALSE);
INSERT INTO tratamiento (nombre, precioEuros, requiereReceta) VALUES ('Antibiótico',        30, TRUE);
INSERT INTO tratamiento (nombre, precioEuros, requiereReceta) VALUES ('Limpieza dental',    60, FALSE);
INSERT INTO tratamiento (nombre, precioEuros, requiereReceta) VALUES ('Antiinflamatorio',   20, TRUE);

-- Consultas
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-10-05 10:00:00', 30, FALSE, 1, 1);
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-09-18 12:30:00', 20, FALSE, 2, 2);
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-10-21 18:15:00', 45, TRUE,  3, 1);
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-11-03 09:00:00', 25, FALSE, 4, 3);
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-11-10 17:00:00', 60, FALSE, 1, 2);
INSERT INTO consulta (fechaHora, duracionMinutos, urgente, mascota_id, veterinario_id)
VALUES ('2026-11-12 11:45:00', 40, TRUE,  5, 3);

-- Tratamientos aplicados en cada consulta
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (1, 1);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (1, 2);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (2, 1);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (3, 3);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (3, 5);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (4, 2);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (5, 4);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (6, 3);
INSERT INTO consulta_tratamiento (consulta_id, tratamiento_id) VALUES (6, 5);