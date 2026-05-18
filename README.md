# 🍨 Yogurt Maker API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9.12-blue?logo=apachemaven)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-85EA2D?logo=swagger)](https://swagger.io/tools/swagger-ui/)
[![H2 Database](https://img.shields.io/badge/H2-in--memory-blue?logo=h2)](https://www.h2database.com/)
[![Licencia](https://img.shields.io/badge/Licencia-Apache%202.0-yellow)](https://www.apache.org/licenses/LICENSE-2.0)

**API REST profesional para gestionar la producción artesanal de yogurt.**  
Crea recetas, controla lotes de producción paso a paso y monitorea temperaturas en tiempo real. Todo documentado automáticamente con **Swagger UI** y listo para ser consumido por cualquier equipo frontend.

---

## 📖 Tabla de Contenidos

- [🧩 Tecnologías](#-tecnologías)
- [🏗️ Arquitectura](#️-arquitectura)
- [📁 Estructura del Proyecto](#-estructura-del-proyecto)
- [⚙️ Requisitos Previos](#️-requisitos-previos)
- [🚀 Instalación y Ejecución](#-instalación-y-ejecución)
  - [Windows](#windows)
  - [Linux / Ubuntu](#linux--ubuntu)
  - [macOS](#macos)
- [📚 Documentación de la API (Swagger)](#-documentación-de-la-api-swagger)
- [🔌 Endpoints Principales](#-endpoints-principales)
  - [Gestión de Recetas](#1-gestión-de-recetas)
  - [Gestión de Lotes de Producción](#2-gestión-de-lotes-de-producción)
  - [Monitoreo de Producción](#3-monitoreo-de-producción)
- [🗄️ Modelo de Datos](#️-modelo-de-datos)
- [🔄 Flujo del Proceso de Yogurt](#-flujo-del-proceso-de-yogurt)
- [🧪 Pruebas](#-pruebas)
- [🤝 Contribución](#-contribución)
- [📄 Licencia](#-licencia)
- [👤 Autor](#-autor)

---

## 🧩 Tecnologías

| Tecnología | Uso en el proyecto |
|------------|-------------------|
| **Java 21** | Lenguaje principal |
| **Spring Boot 4.0.3** | Framework backend |
| **Spring Data JPA / Hibernate** | ORM para mapear objetos a tablas SQL |
| **H2 Database** | Base de datos en memoria (desarrollo / pruebas) |
| **Spring Web MVC** | Creación de API REST |
| **SpringDoc OpenAPI 2.8.0** | Documentación automática con Swagger UI |
| **Lombok** | Elimina boilerplate (getters, setters, constructores) |
| **Maven** | Gestión de dependencias y construcción |

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura **MVC + Capas** bien separadas:

Cliente] ←→ [Controller] ←→ [Service] ←→ [Repository] ←→ [DB]
↑ ↑ ↑ ↑
JSON @RestController Lógica JPA/Hibernate
(Swagger) @Tag, @Operation negocio consultas SQL



- **Controller**: Recibe peticiones HTTP y delega en los servicios.
- **Service**: Contiene la lógica de negocio y validaciones.
- **Repository**: Interfaces JPA que acceden a la base de datos.
- **Model / Entity**: Clases Java que representan las tablas de la BD.
- **DTO**: Objetos de transferencia de datos para aislar la API de las entidades.
- **Exception**: Manejo global de errores con respuestas JSON limpias.

---

## 📁 Estructura del Proyecto


yogurt-maker/
├── pom.xml # Configuración de Maven
├── mvnw / mvnw.cmd # Maven Wrapper (no requiere Maven instalado)
├── .mvn/wrapper/maven-wrapper.properties # Versión de Maven descargable
├── src/main/java/com/danieldev87/demo/
│ ├── DemoApplication.java # Clase principal
│ ├── config/
│ │ └── SpringDocConfig.java # Configuración de Swagger/OpenAPI
│ ├── domain/
│ │ ├── controller/
│ │ │ ├── MonitoringController.java # Endpoints de monitoreo
│ │ │ ├── RecipeController.java # Endpoints de recetas
│ │ │ └── YogurtBatchController.java # Endpoints de lotes
│ │ ├── model/
│ │ │ ├── Ingredient.java # Entidad Ingrediente
│ │ │ ├── Recipe.java # Entidad Receta
│ │ │ ├── TemperatureLog.java # Entidad Registro de Temperatura
│ │ │ └── YogurtBatch.java # Entidad Lote de Producción
│ │ ├── repository/
│ │ │ ├── RecipeRepository.java # Repositorio JPA de Recetas
│ │ │ ├── TemperatureLogRepository.java
│ │ │ └── YogurtBatchRepository.java
│ │ └── service/
│ │ ├── RecipeService.java # Lógica de negocio de recetas
│ │ ├── TemperatureControlService.java # Simulación de control de temperatura
│ │ └── YogurtMakingService.java # Lógica de producción de lotes
│ ├── dto/
│ │ ├── BatchDTO.java # DTOs para iniciar lote / fallo
│ │ ├── IngredientDTO.java # DTO de ingrediente
│ │ ├── MonitoringDTO.java # DTOs de dashboard y resumen de temp.
│ │ ├── RecipeDTO.java # DTO de creación/actualización de receta
│ │ └── TemperatureRecordDTO.java # DTO de registro manual de temperatura
│ └── exception/
│ ├── BusinessException.java # Excepción personalizada
│ └── GlobalExceptionHandler.java # Manejador global de excepciones
└── src/main/resources/
└── application.properties # Configuración de BD, JPA, Swagger, etc.


---

## ⚙️ Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+** (opcional; el proyecto incluye Maven Wrapper)
- **Navegador web** (Chrome, Firefox, Edge)

Para verificar tus versiones:

java --version   # Debe mostrar 21.x.x
mvn --version    # (Opcional) Debe mostrar 3.9 o superior


## 🚀 Instalación y Ejecución


Windows

# 1. Clona o descarga el proyecto
cd C:\Users\TuUsuario\Desktop\yogurt-maker-main

# 2. Ejecuta con Maven Wrapper
./mvnw clean spring-boot:run

Linux / Ubuntu

# 1. Entra a la carpeta del proyecto
cd ~/Escritorio/yogurt-maker-main

# 2. Da permisos de ejecución al wrapper
chmod +x mvnw

# 3. Compila y ejecuta
./mvnw clean spring-boot:run

macOS

# 1. Entra a la carpeta del proyecto
cd ~/Desktop/yogurt-maker-main

# 2. Da permisos al wrapper
chmod +x mvnw

# 3. Ejecuta
./mvnw clean spring-boot:run


Una vez iniciado, verás en la terminal:
Started DemoApplication in X.XXX seconds








