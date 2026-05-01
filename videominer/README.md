# VideoMiner - API REST Central

**Microservicio integrador y almacenador de datos multimedia para el proyecto AISS Video Miner 2026**

---

## Descripción General

VideoMiner es un **microservicio Web API REST** desarrollado con **Spring Boot 3.2.1** que actúa como almacén centralizado de datos para el proyecto AISS Video Miner. Se encarga de:

- **Integrar y almacenar** datos de canales, vídeos, subtítulos y comentarios
- **Persistir datos** en base de datos H2 (en memoria)
- **Exponer endpoints REST** completamente funcionales con validación robusta
- **Documentar automáticamente** la API mediante Swagger/OpenAPI interactivo
- **Validar datos** con constraints automáticos y manejo centralizado de errores

---

## Stack Tecnológico

| Componente | Versión | Descripción |
|-----------|---------|------------|
| **Java** | 17+ | Lenguaje de programación |
| **Spring Boot** | 3.2.1 | Framework web |
| **Maven** | 3.6+ | Gestor de dependencias (wrapper incluido) |
| **Base de Datos** | H2 | BD en memoria (desarrollo/testing) |
| **ORM** | JPA/Hibernate | Mapeo objeto-relacional |
| **API Docs** | SpringDoc OpenAPI 2.3.0 | Documentación automática |
| **Validación** | Jakarta Validation | Constraints de datos |
| **Testing** | JUnit 5 + MockMvc | Tests de integración |

---

## Inicio Rápido

### Requisitos Previos

```bash
# Verificar Java 17+
java -version

# Verificar Maven 3.6+ (opcional, se incluye mvnw)
maven -version
```

### Instalación y Ejecutión

```powershell
# Navegar a la carpeta del proyecto
cd videominer

# Compilar el proyecto
.\mvnw.cmd clean compile

# Ejecutar los tests
.\mvnw.cmd test

# Iniciar la aplicación
.\mvnw.cmd spring-boot:run
```

### URLs de Acceso

Una vez iniciada la aplicación (por defecto en puerto **8080**):

| Recurso | URL |
|---------|-----|
| **API Base** | `http://localhost:8080/videominer` |
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **H2 Console** | `http://localhost:8080/h2-ui` |

---

## Modelo de Datos

VideoMiner gestiona **5 entidades principales**:

### 1️⃣ **Channel (Canal)**
```json
{
  "id": "channel-1",
  "name": "Mi Canal",
  "description": "Descripción del canal",
  "createdTime": "2026-04-30T10:00:00Z",
  "videos": []
}
```
- **Relación:** 1:N con Videos (cascade)
- **Validación:** `name` y `createdTime` requeridos

### 2️⃣ **Video (Vídeo)**
```json
{
  "id": "video-1",
  "name": "Mi Vídeo",
  "description": "Descripción del vídeo",
  "releaseTime": "2026-04-30T11:00:00Z",
  "author": {"id": "user-1", "name": "Autor"},
  "comments": [],
  "captions": []
}
```
- **Relación:** N:1 con User (autor), 1:N con Comments/Captions
- **Validación:** `name` y `releaseTime` requeridos

### 3️⃣ **User (Usuario)**
```json
{
  "id": "user-1",
  "name": "Juan Pérez",
  "user_link": "https://example.com/juan",
  "picture_link": "https://example.com/juan.jpg"
}
```
- **Relación:** Autor de N videos
- **Validación:** `name` requerido
- **Patrón:** `findOrCreate()` evita duplicados

### 4️⃣ **Caption (Subtítulo)**
```json
{
  "id": "caption-1",
  "link": "https://example.com/subtitles.vtt",
  "language": "es",
  "video": {"id": "video-1"}
}
```
- **Relación:** N:1 con Video
- **Validación:** `video` requerido

### 5️⃣ **Comment (Comentario)**
```json
{
  "id": "comment-1",
  "text": "Excelente vídeo",
  "createdOn": "2026-04-30T12:00:00Z",
  "video": {"id": "video-1"}
}
```
- **Relación:** N:1 con Video
- **Validación:** `video` requerido

---

## API Endpoints - Documentación Completa

### Canales - `/videominer/channels`

| Método | Endpoint | Status | Descripción |
|--------|----------|--------|-------------|
| **GET** | `/` | 200 | Listar canales con paginación/filtro |
| **GET** | `/{id}` | 200/404 | Obtener canal por ID |
| **POST** | `/` | 201/400 | Crear nuevo canal |
| **PUT** | `/{id}` | 200/404 | Actualizar canal existente |
| **DELETE** | `/{id}` | 204/404 | Eliminar canal |

**Parámetros de paginación:**
```
?page=0&size=10&sortBy=id&name=optional-filter
```

---

### Vídeos - `/videominer/videos`

| Método | Endpoint | Status | Descripción |
|--------|----------|--------|-------------|
| **GET** | `/` | 200 | Listar vídeos |
| **GET** | `/{id}` | 200/404 | Obtener vídeo |
| **GET** | `/{id}/captions` | 200/404 | Obtener subtítulos de un vídeo |
| **POST** | `/` | 201/400 | Crear vídeo |
| **PUT** | `/{id}` | 200/404 | Actualizar vídeo |
| **DELETE** | `/{id}` | 204/404 | Eliminar vídeo |

---

### Subtítulos - `/videominer/captions`

| Método | Endpoint | Status | Descripción |
|--------|----------|--------|-------------|
| **GET** | `/` | 200 | Listar subtítulos |
| **GET** | `/{id}` | 200/404 | Obtener subtítulo |
| **GET** | `/video/{videoId}` | 200/404 | Subtítulos de un vídeo |
| **POST** | `/` | 201/400 | Crear subtítulo |
| **PUT** | `/{id}` | 200/404 | Actualizar subtítulo |
| **DELETE** | `/{id}` | 204/404 | Eliminar subtítulo |

---

### Comentarios - `/videominer/comments`

| Método | Endpoint | Status | Descripción |
|--------|----------|--------|-------------|
| **GET** | `/` | 200 | Listar comentarios |
| **GET** | `/{id}` | 200/404 | Obtener comentario |
| **GET** | `/video/{videoId}` | 200/404 | Comentarios de un vídeo |
| **POST** | `/` | 201/400 | Crear comentario |
| **PUT** | `/{id}` | 200/404 | Actualizar comentario |
| **DELETE** | `/{id}` | 204/404 | Eliminar comentario |

---

### Usuarios - `/videominer/users`

| Método | Endpoint | Status | Descripción |
|--------|----------|--------|-------------|
| **GET** | `/` | 200 | Listar usuarios |
| **GET** | `/{id}` | 200/404 | Obtener usuario |
| **POST** | `/` | 201/400 | Crear usuario |
| **PUT** | `/{id}` | 200/404 | Actualizar usuario |
| **DELETE** | `/{id}` | 204/404 | Eliminar usuario |

---

## Testing

VideoMiner incluye **21 tests de integración** que cubren toda la funcionalidad:

### Ejecutar Tests

```powershell
# Todos los tests
.\mvnw.cmd test

# Test específico
.\mvnw.cmd test -Dtest=VideominerApiTests

# Con cobertura
.\mvnw.cmd test jacoco:report
```

### Cobertura de Tests

**CRUD Completo** para 5 entidades = 25 operaciones  
**Validaciones** de campos requeridos  
**Búsquedas** con filtros y paginación  
**Relaciones** entre entidades (cascades, foreign keys)  
**Manejo de errores** (404, 400, 500)  
**Status HTTP** correctos (201, 200, 204, 404)  

### Resultados (BUILD SUCCESS)

```
Tests run: 21
Failures: 0
Errors: 0
Skipped: 0
Time: ~35 segundos
```

---

## Colección Postman

Incluye **colección completa** con todos los endpoints CRUD organizados por recurso:

**Ubicación:** `postman/VideoMiner.postman_collection.json`

### Importar en Postman

1. Abrir Postman
2. **Collections → Import**
3. Seleccionar archivo `VideoMiner.postman_collection.json`
4. La variable `{{baseUrl}}` se configura automáticamente a `http://localhost:8080`

### Estructura de la Colección

```
CANALES (6 requests) - POST, GET, GET por ID, PUT, DELETE, Búsqueda
VÍDEOS (7 requests) - CRUD + obtener captions
SUBTÍTULOS (6 requests) - CRUD + por vídeo
COMENTARIOS (7 requests) - CRUD + búsqueda por texto + por vídeo
USUARIOS (6 requests) - CRUD + búsqueda por nombre
UTILIDADES (2 requests) - Swagger + H2 Console
```

**Total: 34 requests listos para usar**

---

## Manejo de Errores

### Respuesta de Error Estándar

```json
{
  "timestamp": "2026-04-30T10:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación",
  "fieldErrors": {
    "name": "El nombre no puede estar vacío",
    "releaseTime": "La fecha es requerida"
  }
}
```

### Códigos HTTP

| Status | Significado | Ejemplo |
|--------|------------|---------|
| **200 OK** | Operación exitosa (GET, PUT) | Actualizar canal |
| **201 Created** | Recurso creado exitosamente | Crear vídeo |
| **204 No Content** | Operación exitosa sin contenido | Eliminar comentario |
| **400 Bad Request** | Validación fallida | Campo vacío |
| **404 Not Found** | Recurso no existe | ID inválido |
| **500 Internal Server** | Error del servidor | Excepción no prevista |

---

## Configuración

**Archivo:** `src/main/resources/application.properties`

```properties
# Servidor
server.port=8080

# Base de Datos H2 (en memoria)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-ui
```

### Notas sobre la Configuración

- **DDL Auto = update:** Crea/actualiza esquema automáticamente
- **BD en memoria:** Los datos se pierden al reiniciar
- **Logs SQL:** Habilitados para debugging
- **H2 Console:** Acceso a BD en `http://localhost:8080/h2-ui`

---

## Estructura del Proyecto

```
videominer/
├── src/main/java/aiss/videominer/
│   ├── VideominerApplication.java         ← Main Spring Boot
│   ├── controller/                        ← 5 controladores REST
│   │   ├── ChannelController.java
│   │   ├── VideoController.java
│   │   ├── CaptionController.java
│   │   ├── CommentController.java
│   │   └── UserController.java
│   ├── service/                           ← 5 servicios (lógica negocio)
│   ├── model/                             ← 5 entidades JPA
│   ├── repository/                        ← 5 repositorios Data JPA
│   └── exception/                         ← Manejo centralizado errores
├── src/main/resources/
│   ├── application.properties
│   ├── static/                            ← Archivos estáticos
│   └── templates/                         ← Plantillas Thymeleaf
├── src/test/java/aiss/videominer/
│   ├── VideominerApiTests.java           ← 21 tests de integración
│   └── VideominerApplicationTests.java   ← Test context load
├── pom.xml                                ← Dependencias Maven
├── mvnw / mvnw.cmd                       ← Maven wrapper
└── postman/
    └── VideoMiner.postman_collection.json ← 34 requests Postman
```

---

## Ejemplo: Flujo Completo de Creación

```request
1. Crear Usuario
   POST /videominer/users
   {"id": "user-1", "name": "Juan"}
   ← 201 Created

2. Crear Vídeo con Usuario como Autor
   POST /videominer/videos
   {"id": "video-1", "name": "Mi vídeo", "author": {"id": "user-1"}}
   ← 201 Created

3. Crear Subtítulo para Vídeo
   POST /videominer/captions
   {"id": "caption-es", "language": "es", "video": {"id": "video-1"}}
   ← 201 Created

4. Crear Comentario en Vídeo
   POST /videominer/comments
   {"id": "comment-1", "text": "¡Excelente!", "video": {"id": "video-1"}}
   ← 201 Created

5. Obtener Vídeo con sus Relaciones
   GET /videominer/videos/video-1
   ← 200 OK + JSON con author, comments, captions
```

---

## Arquitectura

```
┌─────────────────────────────────────────┐
│         Cliente REST (Postman)          │
└──────────────────┬──────────────────────┘
                   │ HTTP (JSON)
┌──────────────────▼──────────────────────┐
│         ChannelController              │
│         VideoController                │
│         CaptionController              │
│         CommentController              │
│         UserController                 │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         ChannelService                 │
│         VideoService (validaciones)    │
│         CaptionService                 │
│         CommentService                 │
│         UserService (findOrCreate)     │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Spring Data JPA Repositories       │
│      (Queries automáticas)              │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      Hibernate ORM (Mapeo)              │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Base de Datos H2 (memoria)      │
│    (5 tablas: channels, videos, etc)   │
└─────────────────────────────────────────┘

Exception Handler (Centralizado)
  ↓
GlobalExceptionHandler (@RestControllerAdvice)
  → MethodArgumentNotValidException → 400 Bad Request
  → ResponseStatusException → custom status
  → All Exceptions → 500 Internal Server Error
```

---

## Despliegue con Nube (Cloud)

### Opciones de Despliegue

VideoMiner está preparado para desplegar en:

#### 1. **Azure App Service**
```bash
# Compilar JAR
.\mvnw.cmd clean package

# Usar mvnw wrapper en Azure
# La apk se crea en target/videominer-0.0.1-SNAPSHOT.jar
```

#### 2. **Docker**
```dockerfile
FROM openjdk:17-slim
COPY target/videominer-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 3. **Bases de Datos en Nube**
Reemplazar H2 por:
- **PostgreSQL** (Azure Database for PostgreSQL)
- **MySQL** (Azure Database for MySQL)
- **SQL Server** (Azure SQL Database)

Ver `pom.xml` para agregar dependencias.

---

## Swagger UI (Documentación Interactiva)

Una vez corriendo, accede a:
```
http://localhost:8080/swagger-ui/index.html
```

**Características:**
- Todos los endpoints documentados
- Prueba los endpoints directamente
- Esquemas JSON de entrada/salida
- Códigos de respuesta explicados

---

## Características Principales

- **Transaccional:** `@Transactional` en servicios  
- **Validación robusta:** Constraints automáticos + GlobalExceptionHandler  
- **Paginación:** Spring Data con `Pageable`  
- **Búsquedas:** Filtros case-insensitive  
- **Relaciones:** Cascades correctas, sin ciclos infinitos en JSON  
- **Documentación:** Swagger/OpenAPI automático  
- **Tests:** 21 tests de integración con 100% pass rate  
- **Código limpio:** Arquitectura de 5 capas, SOLID principles  
- **Postman:** Colección lista con 34 requests  

---

## Troubleshooting

### Error: "No compiler is provided in this environment"
**Causa:** JRE en lugar de JDK  
**Solución:** Instalar JDK 17+ y configurar JAVA_HOME

### Error: "Database is locked"
**Causa:** Múltiples procesos accediendo a H2  
**Solución:** Cerrar instancias anteriores de VideoMiner

### H2 Console no accesible
**Verificar:** `spring.h2.console.enabled=true` en application.properties

---

## Notas de Desarrollo

- **Maven Wrapper:** No requiere Maven instalado, usa `./mvnw.cmd`
- **Hot Reload:** Usa Spring DevTools para cambios en caliente (opcional)
- **Spring Boot Version:** 3.2.1 es LTS y estable para 2026
- **Java 17:** Último LTS, compatible hasta 2029

---

## Licencia

Copyright © 2026 - AISS Proyecto Integración  
VideoMiner es parte del proyecto de integración de sistemas software

---

## Autor

**Desarrollado por:** AISS Development Team  
**Versión:** 1.0  
**Última actualización:** 2026-04-30  

**Para más información:**  
📍 Contacto: aiss@ejemplo.com  
Documentación: `/swagger-ui`  
🗄️ Base de Datos: `http://localhost:8080/h2-ui`  

- `POST /videominer/comments`
- `PUT /videominer/comments/{id}`
- `DELETE /videominer/comments/{id}`

Payload ejemplo (POST/PUT):

```json
{
	"id": "comment-1",
	"text": "Buen video",
	"createdOn": "2026-04-30T12:00:00Z",
	"video": {
		"id": "video-1"
	}
}
```

## Errores y buenas practicas REST

- `404 Not Found` para recursos inexistentes.
- `400 Bad Request` para payloads invalidos (`@Valid` + handler global).
- `204 No Content` en eliminaciones correctas.

Formato de error (ejemplo):

```json
{
	"timestamp": "2026-04-30T12:34:56Z",
	"status": 400,
	"error": "Bad Request",
	"message": "Error de validación",
	"fieldErrors": {
		"name": "El nombre del vídeo no puede estar vacío"
	}
}
```

## Tests

Ejecutar todos los tests:

```powershell
./mvnw.cmd test
```

Incluye pruebas de:

- CRUD de `Channel` y `Video`.
- Casos de error de validacion.
- Casos limite de `PUT` y `DELETE` para ids no existentes.
- Consultas por id y por relacion (`captions/video/{id}`, `comments/video/{id}`).
