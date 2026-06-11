# Mantenimiento y Deuda Técnica

Fecha de revisión: 2026-06-11

## Cambios Realizados

- Se agregó `spring-boot-starter-validation` para que `@Valid` y las restricciones de Jakarta Validation funcionen realmente.
- Se añadieron restricciones a DTOs de recetas, ingredientes, lotes y temperaturas.
- Se separaron errores de negocio (`400`/`409`), recursos no encontrados (`404`), cuerpos inválidos y errores de validación.
- Se corrigió la documentación de estados de lote: `REFRIGERATING` estaba mal escrito en el schema.
- Se evitó recursión JSON en relaciones bidireccionales (`Recipe`/`Ingredient`, `YogurtBatch`/`TemperatureLog`).
- Se reemplazó el uso directo de `new Thread(...)` por un executor gestionado por Spring.
- Se corrigió la transición automática a `COOLING` para que se persista en base de datos.
- Se movió la lógica de monitoreo desde el controller a `MonitoringService`.
- Se limpiaron queries de repositorio que no estaban siendo usadas.
- Se endureció `application.properties`: sin SQL verbose por defecto, `open-in-view=false` y H2 Console sin acceso remoto.
- Se agregaron pruebas unitarias para reglas críticas de recetas, lotes y monitoreo.
- Se creó este README porque el repo no tenía uno versionado al momento de la revisión.

## Limpieza De Artefactos

Se removieron archivos generados o de evidencia manual que no forman parte del runtime:

- `code-json/api-docs.json`: export estático de OpenAPI, reemplazable por `/v3/api-docs`.
- `code-json/contexto-tarea.txt`: nota manual antigua, reemplazada por `README.md` y este documento.
- `code-json/pruebas/*`: capturas y video de pruebas manuales, pesados y no requeridos por la app.

La carpeta `code-json/` quedó ignorada para evitar volver a versionar artefactos generados.

## Deuda Técnica Pendiente

- Separar entidades JPA de respuestas públicas usando DTOs de salida. Actualmente los controllers devuelven entidades.
- Persistir y reanudar procesos largos de producción con un scheduler o máquina de estados; hoy son simulaciones en memoria.
- Crear perfiles `dev`, `test` y `prod` para separar H2, logging, consola H2 y políticas de inicialización.
- Agregar pruebas web con MockMvc/WebTestClient para validar contratos HTTP, serialización y errores de validación.
- Introducir migraciones con Flyway o Liquibase si la app deja de usar `ddl-auto=create-drop`.
- Parametrizar intervalos de simulación de temperatura para pruebas e instalaciones reales.
- Revisar compatibilidad futura de Mockito con Java 21+ y agente dinámico; por ahora se fijó `mock-maker-subclass` para pruebas locales.
