# VideoMiner

Microservicio central del proyecto AISS Video Miner. Se encarga de integrar y almacenar datos de canales, videos, captions y comments en una base de datos H2 y exponerlos mediante API REST.

## Requisitos

- Java 17+
- Maven Wrapper (`mvnw.cmd`)

## Ejecucion

```powershell
./mvnw.cmd spring-boot:run
```

Aplicacion disponible en:

- API base: `http://localhost:8080/videominer`
- H2 console: `http://localhost:8080/h2-ui`
- OpenAPI/Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Coleccion Postman

Coleccion incluida en:

- `postman/VideoMiner.postman_collection.json`

Importa ese archivo en Postman y ejecuta las requests en orden (primero create channel/video y luego captions/comments).

## Endpoints principales

### Channels

- `GET /videominer/channels?page=0&size=10&sortBy=id&name=optional`
- `GET /videominer/channels/{id}`
- `POST /videominer/channels`
- `PUT /videominer/channels/{id}`
- `DELETE /videominer/channels/{id}`

Payload ejemplo (POST/PUT):

```json
{
	"id": "channel-1",
	"name": "Canal Demo",
	"description": "Canal de prueba",
	"createdTime": "2026-04-30T10:00:00Z",
	"videos": []
}
```

### Videos

- `GET /videominer/videos?page=0&size=10&sortBy=id&name=optional`
- `GET /videominer/videos/{id}`
- `POST /videominer/videos`
- `PUT /videominer/videos/{id}`
- `DELETE /videominer/videos/{id}`
- `GET /videominer/videos/{id}/captions?page=0&size=10&sortBy=id`

Payload ejemplo (POST/PUT):

```json
{
	"id": "video-1",
	"name": "Video Demo",
	"description": "Descripcion del video",
	"releaseTime": "2026-04-30T11:00:00Z",
	"comments": [],
	"captions": []
}
```

### Captions

- `GET /videominer/captions?page=0&size=10&sortBy=id`
- `GET /videominer/captions/{id}`
- `GET /videominer/captions/video/{videoId}?page=0&size=10&sortBy=id`
- `POST /videominer/captions`
- `PUT /videominer/captions/{id}`
- `DELETE /videominer/captions/{id}`

Payload ejemplo (POST/PUT):

```json
{
	"id": "caption-1",
	"link": "https://example.com/subtitle.vtt",
	"language": "es",
	"video": {
		"id": "video-1"
	}
}
```

### Comments

- `GET /videominer/comments?page=0&size=10&sortBy=id&text=optional`
- `GET /videominer/comments/{id}`
- `GET /videominer/comments/video/{videoId}?page=0&size=10&sortBy=id`
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
