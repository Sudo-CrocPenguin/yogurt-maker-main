<div align="center">

# Yogurt Maker

**Aplicación full stack para gestionar recetas de yogurt, lotes de producción y monitoreo de temperaturas durante el proceso.**  
Incluye API REST en Spring Boot, panel frontend en React y documentación interactiva con Swagger UI.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-7-646CFF?style=flat-square&logo=vite&logoColor=white)](https://vite.dev/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/tools/swagger-ui/)
[![H2](https://img.shields.io/badge/H2-in--memory-004990?style=flat-square)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

</div>

---

## ¿Qué hace esta aplicación?

Yogurt Maker modela el proceso real de producción artesanal de yogurt. Permite crear recetas con sus ingredientes, iniciar lotes de producción, avanzar etapas, registrar temperaturas manualmente y consultar un dashboard de monitoreo.

El backend usa arquitectura por capas, DTOs de entrada, manejo global de errores, validación con Jakarta Bean Validation y documentación automática con SpringDoc OpenAPI. El frontend ofrece un panel operativo para consumir esos endpoints desde el navegador.

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
| React 19 + TypeScript | Panel web de operación |
| Vite 7 | Desarrollo y build del frontend |

---

## Requisitos

- Java 21 o superior
- Node.js 20 o superior para el frontend

```bash
java --version
node --version
```

---

## Instalación y ejecución

```bash
git clone <url-del-repo>
cd yogurt-maker
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
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
El perfil `dev` carga recetas iniciales desde `data.sql` y habilita CORS para el frontend en `http://localhost:5173`.

### Frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El panel queda disponible en:

```text
http://localhost:5173
```

El frontend consume por defecto `http://localhost:8080/api`. Si necesitás cambiar esa URL, crea `frontend/.env.local` usando `frontend/.env.example` como referencia.

---

## Tests

```bash
./mvnw test
cd frontend
npm run build
```

La suite backend incluye una prueba de carga de contexto Spring y pruebas unitarias para reglas de negocio de recetas, lotes y monitoreo. El build del frontend ejecuta TypeScript y genera el bundle de producción.

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
- **`frontend/src/pages/DashboardPage.tsx`**: métricas generales y lotes activos.
- **`frontend/src/pages/RecipesPage.tsx`**: búsqueda, creación y edición de recetas.
- **`frontend/src/pages/BatchesPage.tsx`**: creación de lotes, detalle y control de estados.
- **`frontend/src/pages/MonitoringPage.tsx`**: resumen térmico, gráfica e historial de temperaturas.

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
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── pages/
│   │   └── utils/
├── pom.xml
├── mvnw / mvnw.cmd
├── docs/
│   ├── FRONTEND.md
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
- El frontend usa `fetch` tipado en `frontend/src/api/client.ts` y componentes propios para mantener el panel ligero.
- No se versionan capturas, videos ni exports generados de OpenAPI; la documentación actual se obtiene desde Swagger/OpenAPI en runtime.

Guía del frontend en [docs/FRONTEND.md](docs/FRONTEND.md).
Más detalles de mantenimiento y deuda técnica en [docs/MANTENIMIENTO.md](docs/MANTENIMIENTO.md).

---

## Licencia

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
