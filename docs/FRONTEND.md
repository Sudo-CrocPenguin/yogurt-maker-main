# Frontend de Yogurt Maker

Fecha de creación: 2026-06-15

## Qué es

El frontend es un panel operativo en React y TypeScript para usar la API de Yogurt Maker desde el navegador. No es una landing page: está diseñado como una herramienta de trabajo para consultar métricas, administrar recetas, controlar lotes y revisar temperaturas.

## Para qué sirve

Sirve para cubrir el flujo completo de producción sin usar Postman ni Swagger como interfaz principal:

- Ver un dashboard con lotes totales, activos, completados hoy y distribución por estado.
- Crear, buscar, editar y desactivar recetas.
- Crear lotes desde recetas activas.
- Avanzar el ciclo de vida de un lote: calentamiento, inoculación, incubación, refrigeración y completado.
- Marcar lotes como fallidos con motivo.
- Registrar temperaturas manuales.
- Consultar resumen térmico, gráfica e historial de temperaturas por lote.

## Cómo funciona

La app vive en `frontend/` y se ejecuta con Vite. El archivo `frontend/src/api/client.ts` centraliza las llamadas HTTP a la API. Las pantallas consumen ese cliente y comparten componentes visuales ubicados en `frontend/src/components/`.

Flujo general:

```text
Usuario -> Pantalla React -> apiClient -> Backend Spring Boot -> H2
```

El backend debe estar corriendo en `http://localhost:8080`. El frontend usa `VITE_API_BASE_URL` para saber a qué API llamar. Si no se define, toma `http://localhost:8080/api`.

## Ejecución local

Terminal 1, backend con datos de desarrollo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Terminal 2, frontend:

```bash
cd frontend
npm install
npm run dev
```

Abrir:

```text
http://localhost:5173
```

## Configuración

Crear `frontend/.env.local` solo si la API no corre en el puerto por defecto:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

El backend permite CORS para:

```text
http://localhost:5173
http://127.0.0.1:5173
```

## Estructura

```text
frontend/
├── src/
│   ├── api/
│   │   └── client.ts
│   ├── components/
│   │   ├── Button.tsx
│   │   ├── Field.tsx
│   │   ├── Notice.tsx
│   │   ├── Panel.tsx
│   │   ├── StatusBadge.tsx
│   │   └── TemperatureChart.tsx
│   ├── hooks/
│   │   └── useAsyncResource.ts
│   ├── pages/
│   │   ├── BatchesPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── MonitoringPage.tsx
│   │   └── RecipesPage.tsx
│   ├── utils/
│   │   └── format.ts
│   ├── App.tsx
│   ├── constants.ts
│   ├── main.tsx
│   └── types.ts
└── package.json
```

## Pantallas

### Dashboard

Muestra métricas de producción general. Consume:

```text
GET /api/monitoring/dashboard
GET /api/monitoring/batches/active
```

### Recetas

Permite administrar las recetas que después se usan para crear lotes. Consume:

```text
GET    /api/recipes
GET    /api/recipes/search
POST   /api/recipes
PUT    /api/recipes/{id}
PATCH  /api/recipes/{id}/deactivate
PATCH  /api/recipes/{id}/activate
```

### Lotes

Controla la producción. Permite crear lotes, avanzar estados, fallar lotes y registrar temperatura manual. Consume:

```text
GET  /api/batches
GET  /api/batches/{id}
POST /api/batches
POST /api/batches/{id}/heating
POST /api/batches/{id}/inoculating
POST /api/batches/{id}/incubation
POST /api/batches/{id}/refrigeration
POST /api/batches/{id}/complete
POST /api/batches/{id}/fail
POST /api/batches/{id}/temperature
```

### Temperaturas

Muestra resumen, gráfica e historial térmico. Consume:

```text
GET /api/batches
GET /api/monitoring/batches/{id}/temperature
GET /api/monitoring/batches/{id}/temperature-logs
```

## Validación

Para validar el frontend:

```bash
cd frontend
npm run build
```

Ese comando ejecuta TypeScript y genera `frontend/dist/`. La carpeta `dist/` no se versiona porque es un artefacto generado.

## Decisiones técnicas

- Se usa React sin librería de routing porque el panel actual tiene navegación interna simple.
- Se usa una gráfica SVG propia para evitar dependencias adicionales.
- Los tipos TypeScript reflejan las entidades que hoy devuelve el backend.
- Los errores de la API se muestran usando el formato uniforme de `GlobalExceptionHandler`.
- `package-lock.json` sí se versiona para que las instalaciones sean reproducibles.

## Limitaciones actuales

- El backend devuelve entidades JPA directamente; si se agregan DTOs de salida, `frontend/src/types.ts` debe actualizarse.
- El endpoint `GET /api/recipes` lista recetas activas; una receta desactivada puede desaparecer del listado principal.
- Los procesos automáticos son simulaciones en memoria; si el backend se reinicia, el proceso activo no se reanuda.
