# DevOps Fundamentals con GitLab CI/CD

Material de estudio sobre los fundamentos de DevOps y la construcción de pipelines de CI/CD con GitLab. Incluye una presentación introductoria, notas de referencia y dos proyectos de práctica con pipelines reales: un sitio estático y una API REST desplegada en AWS.

## Contenido

| Recurso | Descripción |
|---------|-------------|
| [`docs/slides/presentacion-devops.pptx`](docs/slides/presentacion-devops.pptx) | Presentación: qué es DevOps, ciclo de vida (plan, code, build, test, release, deploy, operate, monitor), CI/CD y GitLab CI |
| [`docs/gitlab-ci-course-notes.pdf`](docs/gitlab-ci-course-notes.pdf) | Notas de referencia del curso de GitLab CI |
| [`docs/diagrams/`](docs/diagrams) | Arquitectura de GitLab y flujo de CI/CD |
| [`docs/snippets/basic-pipeline.gitlab-ci.yml`](docs/snippets/basic-pipeline.gitlab-ci.yml) | Pipeline mínimo de dos etapas (build → test) con artifacts |
| [`projects/my-static-website/`](projects/my-static-website) | Sitio Gatsby con pipeline completo y 15 ejemplos progresivos |
| [`projects/cars-api/`](projects/cars-api) | API Spring Boot con pipeline de build, calidad, tests y despliegue en AWS |
| [`projects/postman/`](projects/postman) | Colección y entorno de Postman para probar `cars-api` |

## Estructura

```
.
├── docs/
│   ├── slides/              # Presentación
│   ├── diagrams/            # Diagramas de arquitectura y CI/CD
│   ├── images/              # Imágenes usadas en la presentación
│   ├── snippets/            # Pipelines mínimos de referencia
│   └── gitlab-ci-course-notes.pdf
└── projects/
    ├── my-static-website/
    │   ├── gitlab-ci-examples/   # Evolución del pipeline, del 01 al 15
    │   └── .gitlab-ci.yml        # Pipeline final
    ├── cars-api/
    │   └── .gitlab-ci.yml
    └── postman/
```

## Proyectos

### my-static-website

Sitio estático con [Gatsby](https://www.gatsbyjs.com) 5 y React 18, desplegado en [Surge](https://surge.sh).

**Ejecutar en local** (requiere Node.js):

```bash
cd projects/my-static-website
npm install
npm run develop   # http://localhost:8000
npm run build     # genera ./public
```

**Pipeline** (`.gitlab-ci.yml`):

```
build → test → deploy review → deploy staging → deploy production → production tests
```

- Las merge requests despliegan una *review app* en un subdominio propio, que se elimina con el job manual `stop review`.
- `main` despliega a staging y luego a producción, y verifica el sitio publicado.
- Usa cache de `node_modules` por rama.

**Variables de CI/CD:** `SURGE_LOGIN` y `SURGE_TOKEN` para autenticar el CLI de Surge.

#### Ejemplos progresivos

En [`gitlab-ci-examples/`](projects/my-static-website/gitlab-ci-examples) está la evolución del pipeline paso a paso. Cada archivo agrega un concepto sobre el anterior. Para probar uno, cópialo como `.gitlab-ci.yml` en la raíz del proyecto.

| # | Concepto |
|---|----------|
| 01 | Primer job: build del sitio |
| 02 | Etapas y artifacts: test sobre el build |
| 03 | Jobs de test en paralelo |
| 04 | Optimización de los tests |
| 05 | Primer despliegue (CD) a Surge |
| 06 | Variables predefinidas de GitLab y test del despliegue |
| 07 | Cache de dependencias |
| 08 | Entornos staging y producción |
| 09 | Variables personalizadas |
| 10 | Despliegue manual a producción |
| 11 | Reglas por rama |
| 12 | Pipelines para merge requests |
| 13 | Review apps |
| 14 | Detener review apps |
| 15 | Anchors de YAML para reutilizar configuración |

### cars-api

API REST de una flota de vehículos con Spring Boot 2.2, Java 8, Gradle y base de datos H2 en memoria.

**Ejecutar en local** (requiere JDK 8 o superior):

```bash
cd projects/cars-api
./gradlew bootRun   # http://localhost:5000
./gradlew test
```

**Endpoints:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/cars` | Lista todos los vehículos |
| POST | `/cars` | Crea un vehículo |
| GET | `/cars/{id}` | Obtiene un vehículo |
| DELETE | `/cars/{id}` | Elimina un vehículo |
| GET | `/statistics/{age}` | Edad promedio de la flota |
| GET | `/actuator/info` | Versión, commit y rama del build |

**Pipeline** (`.gitlab-ci.yml`):

```
build → test (smoke, code quality, unit tests) → deploy → post deploy → publishing
```

- **build:** compila el JAR e inyecta en `application.yml` el número de pipeline, el commit y la rama.
- **test:** smoke test, análisis estático con PMD y tests unitarios.
- **deploy:** sube el JAR a S3 y crea una nueva versión en AWS Elastic Beanstalk.
- **post deploy:** pruebas de API con Newman sobre el entorno desplegado.
- **publishing:** publica el reporte HTML de Newman en GitLab Pages.

**Variables de CI/CD:** `S3_BUCKET`, `AWS_ACCESS_KEY_ID` y `AWS_SECRET_ACCESS_KEY`.

### postman

Importa en Postman `cars-api.postman_collection.json` y `localhost.postman_environment.json`. El entorno apunta a `http://localhost:5000`, el puerto de `cars-api` en local.

## Notas

- GitLab solo ejecuta el `.gitlab-ci.yml` de la raíz del repositorio. Para correr el pipeline de un proyecto, súbelo como repositorio independiente en GitLab.
- El job `api testing` de `cars-api` espera encontrar en la raíz del proyecto la colección de Postman y un entorno `Production.postman_environment.json`. Para que funcione, copia la colección ahí y crea ese entorno con la URL de Elastic Beanstalk.
