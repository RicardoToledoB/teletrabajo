# Corrección de persistencia F01 (H2)

El ensayo `AcrF01PersistenceTest.roundTripF01` informó que `acr_processes` no existía en H2 (42102). MySQL de producción sí contiene esa tabla. En H2, YEAR es una palabra reservada: las entidades AcrProcess y QualCycle declaraban `year` sin delimitar. Se utiliza el mecanismo de comillas de Hibernate (`@Column(name="`year`")`) para conservar el nombre físico y el JSON `year`, con las comillas adecuadas al dialecto. El ensayo utiliza una base H2 aislada y falla inmediatamente si no puede crear el esquema. No se cambian configuraciones productivas, migraciones SQL ni controles de seguridad.

Validación en servidor (antes de migrar MySQL):

```sh
./mvnw -Dtest=ModuleAuthorizationTest,CapacitacionEmissionTest,AcrF01PersistenceTest test
./mvnw clean package
```

Si falla, revisar el primer error DDL, no solo el resumen. No omitir pruebas. La corrección se verificó estáticamente; pendiente ejecutar Maven en el servidor.
