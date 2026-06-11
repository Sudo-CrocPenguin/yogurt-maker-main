<div align="center">

# Yogurt Maker API

**API REST en Spring Boot para gestionar recetas de yogurt, lotes de producción y monitoreo de temperaturas durante el proceso.**  
Recetas, lotes de producción, control de temperatura y monitoreo, todo documentado con Swagger UI.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/tools/swagger-ui/)
[![H2](https://img.shields.io/badge/H2-in--memory-004990?style=flat-square)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

</div>

---

## ¿Qué hace esta API?

Yogurt Maker modela el proceso real de producción artesanal de yogurt como una API REST. Permite crear recetas con sus ingredientes, iniciar lotes de producción, registrar temperaturas manualmente y consultar un dashboard de monitoreo.

La aplicación usa arquitectura por capas, DTOs de entrada, manejo global de errores, validación con Jakarta Bean Validation y documentación automática con SpringDoc OpenAPI.

---

## Stack

| Tecnología | Rol |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 4.0.3 | Framework backend |
| Spring Data JPA + Hibernate | ORM y acceso a datos |
| H2 Database | Base de datos en memoria para desarrollo |
| SpringDoc OpenAPI 2.8.0 | Swagger UI automático |
| Lombok | Reduce boilerplate |
| Maven Wrapper | Ejecuta Maven sin instalación global |

---

## Requisitos

- Java 21 o superior

```bash
java --version
```

---

## Instalación y ejecución

```bash
git clone <url-del-repo>
cd yogurt-maker
./mvnw spring-boot:run
```

En Linux/macOS, si hace falta:

```bash
chmod +x mvnw
```

Cuando veas `Started DemoApplication in X.XXX seconds`, la API está disponible en:

| Recurso | URL |
|---|---|
| API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| H2 Console | `http://localhost:8080/h2-console` |

La base de datos es H2 en memoria (`jdbc:h2:mem:yogurtdb`) y se recrea al iniciar la aplicación.

---

## Tests

```bash
./mvnw test
```

La suite incluye una prueba de carga de contexto Spring y pruebas unitarias para reglas de negocio de recetas, lotes y monitoreo.

---

## Documentación interactiva

Desde Swagger UI podés explorar y probar los endpoints sin Postman ni herramientas adicionales:

```text
http://localhost:8080/swagger-ui.html
```

---

## Endpoints principales

### Recetas

```text
GET    /api/recipes                  listar recetas activas
GET    /api/recipes/{id}             obtener una receta
GET    /api/recipes/search           buscar recetas por keyword
POST   /api/recipes                  crear receta con ingredientes
PUT    /api/recipes/{id}             actualizar receta
PATCH  /api/recipes/{id}/activate    activar receta
PATCH  /api/recipes/{id}/deactivate  desactivar receta
```

### Lotes de producción

```text
GET    /api/batches                       listar lotes
GET    /api/batches/{id}                  detalle de un lote
POST   /api/batches                       iniciar lote desde una receta
POST   /api/batches/{id}/heating          iniciar calentamiento
POST   /api/batches/{id}/inoculating      iniciar inoculación
POST   /api/batches/{id}/incubation       iniciar incubación
POST   /api/batches/{id}/refrigeration    iniciar refrigeración
POST   /api/batches/{id}/complete         completar lote
POST   /api/batches/{id}/fail             marcar lote como fallido
POST   /api/batches/{id}/temperature      registrar temperatura manual
```

### Monitoreo

```text
GET    /api/monitoring/dashboard
GET    /api/monitoring/batches/active
GET    /api/monitoring/batches/{id}/temperature
GET    /api/monitoring/batches/{id}/temperature-logs
```

---

## Módulos principales

- **`RecipeController`**: creación, actualización, búsqueda y activación/desactivación de recetas.
- **`YogurtBatchController`**: ciclo de vida de lotes, transiciones de estado y registro manual de temperaturas.
- **`MonitoringController`**: lotes activos, historial de temperatura y dashboard de producción.

---

## Flujo de producción

Un lote sigue estos estados:

```text
PREPARING -> HEATING -> COOLING -> INOCULATING -> INCUBATING -> REFRIGERATING -> COMPLETED
                                                                                  -> FAILED
```

Los estados activos son todos los no terminales: desde `PREPARING` hasta `REFRIGERATING`.

---

## Arquitectura

```text
Controller -> Service -> Repository -> DB
```

- **Controller**: recibe peticiones HTTP y delega en servicios.
- **Service**: concentra lógica de negocio y validaciones.
- **Repository**: interfaces JPA y consultas a base de datos.
- **Model / Entity**: entidades persistidas (`Recipe`, `YogurtBatch`, `Ingredient`, `TemperatureLog`).
- **DTO**: objetos de transferencia para solicitudes REST.
- **Exception**: `GlobalExceptionHandler` centraliza respuestas de error.

---

## Estructura del proyecto

```text
yogurt-maker/
├── pom.xml
├── mvnw / mvnw.cmd
├── docs/
│   └── MANTENIMIENTO.md
└── src/
    ├── main/java/com/danieldev87/demo/
    │   ├── config/
    │   ├── domain/
    │   │   ├── controller/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   ├── dto/
    │   └── exception/
    └── test/
```

---

## Notas técnicas

- Las entradas REST usan Bean Validation real mediante `spring-boot-starter-validation`.
- Los errores de negocio, validación y recursos no encontrados devuelven respuestas JSON uniformes.
- Los procesos simulados de calentamiento/incubación corren en un `ThreadPoolTaskExecutor` gestionado por Spring.
- No se versionan capturas, videos ni exports generados de OpenAPI; la documentación actual se obtiene desde Swagger/OpenAPI en runtime.

Más detalles de mantenimiento y deuda técnica en [docs/MANTENIMIENTO.md](docs/MANTENIMIENTO.md).

---

## Licencia

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
