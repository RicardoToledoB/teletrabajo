INSERT INTO parents_types (name, created_at, updated_at, deleted_at) VALUES
                                                                    ('Padre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Madre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Hermano/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Abuelo/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Tio/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Primo/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Conyuge', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                    ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);


INSERT INTO previtions (name, created_at, updated_at, deleted_at) VALUES
                                                                         ('Fonasa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Isapre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Capredena', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO civil_states (name, created_at, updated_at, deleted_at) VALUES
                                                                      ('Soltero/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Casado/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Divorciado/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Viudo/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Separado/a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Union Civil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);


INSERT INTO activities (name, created_at, updated_at, deleted_at) VALUES
                                                                        ('Administrativo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Operativo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Tecnico', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Profesional', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Supervision', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Terreno', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Atencion de Publico', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Dueña de Casa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Jubilado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO works_places (name, created_at, updated_at, deleted_at) VALUES
                                                                      ('Estudiante', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Institucion Publica', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Empresa Privada', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Establecimiento Educacional', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('ONG / Fundacion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Independiente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Organismo Internacional', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                      ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO contracts_types (name, created_at, updated_at, deleted_at) VALUES
                                                                        ('Ingreso por Remuneracion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Pension de Alimentos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Pension (jubilacion / invalidez)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Subsidio Estatal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Ingreso Independiente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Arriendo(Ingreso por propiedades)', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Apoyo Familiar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                        ('Sin Ingresos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO stablishments (name, created_at, updated_at, deleted_at) VALUES
                                                                           ('DSSM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Hospital Clinico Magallanes', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                           ('Hospital Dr. Augusto Essmann Burgos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                           ('Hospital Comunitario Cristina Calderon', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                           ('Hospital Dr. Marco Chamorro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                           ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO studies (name, created_at, updated_at, deleted_at) VALUES
                                                                         ('Basica - En curso', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Media - En curso', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 1 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 2 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 3 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 4 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 5 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Tecnico Profesional - 6 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 1 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 2 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 3 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 4 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 5 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 6 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 7 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 8 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 9 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 10 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 11 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Universitario - 12 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Postgrado - 1 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Postgrado - 2 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Postgrado - 3 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Postgrado - 4 Semestre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO types_housings (name, created_at, updated_at, deleted_at) VALUES
                                                                         ('Casa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Departamento', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Pierza / Habitacion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Vivienda Social', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Parcela', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Mediagua', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Campamento', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Residencia familiar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Hogar de acogida', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO types_properties (name, created_at, updated_at, deleted_at) VALUES
                                                                          ('Propia', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('Arrendada', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('Cedida', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('Allegado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('Usufructo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('En proceso de pago', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                          ('Ocupacion irregular', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                         ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);


INSERT INTO bills_types (name, created_at, updated_at, deleted_at) VALUES
                                                                            ('Matricula', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Arancel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Mensualidad', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Materiales de estudio', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Libros', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Transporte', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Alimentacion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Alojamiento', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
                                                                            ('Otro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);
