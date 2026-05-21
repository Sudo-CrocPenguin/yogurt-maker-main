<div align="center">

# Yogurt Maker API

**API REST para gestionar producción artesanal de yogurt.**  
Recetas, lotes de producción, control de temperatura y monitoreo — todo documentado con Swagger UI.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/tools/swagger-ui/)
[![H2](https://img.shields.io/badge/H2-in--memory-004990?style=flat-square)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

</div>

---

## ¿Qué hace esta API?

Yogurt Maker modela el proceso real de producción artesanal de yogurt como una API REST. Podés crear recetas con sus ingredientes, iniciar lotes de producción que avanzan paso a paso, registrar temperaturas manualmente y consultar un dashboard de monitoreo en tiempo real.

Está pensada como backend listo para consumir: arquitectura limpia en capas, DTOs para desacoplar la API de las entidades, manejo global de errores y documentación automática con Swagger.

---

## Stack

| Tecnología | Rol |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 4.0.3 | Framework backend |
| Spring Data JPA + Hibernate | ORM y acceso a datos |
| H2 Database | Base de datos en memoria (dev/test) |
| SpringDoc OpenAPI 2.8.0 | Swagger UI automático |
| Lombok | Elimina boilerplate |
| Maven Wrapper | Sin necesidad de Maven instalado |

---

## Instalación

El proyecto incluye Maven Wrapper — **no necesitás tener Maven instalado**.

**Requisito único:** Java 21 o superior.

```bash
java --version   # debe mostrar 21.x.x
```

### Clonar y ejecutar

```bash
git clone <url-del-repo>
cd yogurt-maker
```

**Linux / macOS**
```bash
chmod +x mvnw
./mvnw clean spring-boot:run
```

**Windows**
```bash
./mvnw clean spring-boot:run
```

Cuando veas `Started DemoApplication in X.XXX seconds`, la API está lista.

---

## Documentación interactiva

Una vez iniciado el servidor, abrí Swagger UI en el navegador:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí podés explorar y probar todos los endpoints sin necesidad de Postman ni ninguna herramienta adicional.

---

## Endpoints

### Recetas

```
GET    /api/recipes           → listar todas las recetas
GET    /api/recipes/{id}      → obtener una receta
POST   /api/recipes           → crear receta con ingredientes
PUT    /api/recipes/{id}      → actualizar receta
DELETE /api/recipes/{id}      → eliminar receta
```

### Lotes de producción

```
GET    /api/batches           → listar lotes
GET    /api/batches/{id}      → detalle de un lote
POST   /api/batches/start     → iniciar lote desde una receta
PUT    /api/batches/{id}/next → avanzar al siguiente paso
PUT    /api/batches/{id}/fail → marcar lote como fallido
```

### Monitoreo y temperatura

```
GET    /api/monitoring/dashboard       → resumen general de producción
POST   /api/monitoring/temperature     → registrar temperatura manualmente
GET    /api/monitoring/temperature/{batchId} → historial de temperaturas de un lote
```

---

## Arquitectura

Capas bien separadas siguiendo MVC clásico:

```
Controller → Service → Repository → DB
```

- **Controller** — recibe peticiones HTTP, delega en servicios, no tiene lógica de negocio
- **Service** — lógica de negocio y validaciones
- **Repository** — interfaces JPA, consultas a la BD
- **Model / Entity** — clases que mapean a tablas (`Recipe`, `YogurtBatch`, `Ingredient`, `TemperatureLog`)
- **DTO** — objetos de transferencia que desacoplan la API de las entidades internas
- **Exception** — `GlobalExceptionHandler` centraliza todos los errores y devuelve JSON limpio

---

## Estructura del proyecto

```
yogurt-maker/
├── pom.xml
├── mvnw / mvnw.cmd
└── src/main/
    ├── java/com/danieldev87/demo/
    │   ├── DemoApplication.java
    │   ├── config/
    │   │   └── SpringDocConfig.java        # Configuración Swagger
    │   ├── domain/
    │   │   ├── controller/
    │   │   │   ├── RecipeController.java
    │   │   │   ├── YogurtBatchController.java
    │   │   │   └── MonitoringController.java
    │   │   ├── model/
    │   │   │   ├── Recipe.java
    │   │   │   ├── Ingredient.java
    │   │   │   ├── YogurtBatch.java
    │   │   │   └── TemperatureLog.java
    │   │   ├── repository/
    │   │   │   ├── RecipeRepository.java
    │   │   │   ├── YogurtBatchRepository.java
    │   │   │   └── TemperatureLogRepository.java
    │   │   └── service/
    │   │       ├── RecipeService.java
    │   │       ├── YogurtMakingService.java
    │   │       └── TemperatureControlService.java
    │   ├── dto/
    │   │   ├── RecipeDTO.java
    │   │   ├── IngredientDTO.java
    │   │   ├── BatchDTO.java
    │   │   ├── MonitoringDTO.java
    │   │   └── TemperatureRecordDTO.java
    │   └── exception/
    │       ├── BusinessException.java
    │       └── GlobalExceptionHandler.java
    └── resources/
        └── application.properties
```

---

## Flujo de producción

Un lote sigue estos estados en orden:

```
INICIADO → PASTEURIZANDO → ENFRIANDO → INOCULANDO → FERMENTANDO → COMPLETADO
                                                                 ↘ FALLIDO
```

Cada llamada a `PUT /api/batches/{id}/next` avanza al siguiente estado. En cualquier punto podés registrar temperaturas o marcar el lote como fallido.

---

## Licencia

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) — libre para usar, modificar y distribuir.

---

<div align="center">

_Hecho con Java, Spring y demasiado yogurt de prueba._

</div>
