# DailymotionMiner: Adaptador de Extracción y Minería

**Microservicio adaptador encargado de extraer, transformar y cargar datos desde Dailymotion hacia el sistema central VideoMiner.**

---

## 1. Descripción General
DailymotionMiner funciona como un adaptador (o miner) que se conecta a la API de **Dailymotion** para extraer información de canales (basados en playlists), sus vídeos, comentarios (etiquetas) y subtítulos. Los datos se normalizan al modelo común y se envían a **VideoMiner** para su almacenamiento definitivo.
---

## 2. Arquitectura y Tecnologías
Este microservicio está desarrollado bajo una arquitectura de microservicios modular y desacoplada.

*   **Stack**: Spring Boot 3.5.13 y Java 21.
*   **Puerto**: Configurado en el **8082** para permitir la ejecución simultánea con otros mineros.
*   **Componentes**: 
    *   **Servicios Especializados**: División de lógica en `ApiSubtitleService`, `ApiCommentService`, `ApiVideoUserService` y `ApiChannelService`.
    *   **Mapeo**: Uso de un `DailymotionMapper` estático para la transformación de datos externos a internos.

---

## 3. Reglas de Mapeo (Adaptación)
El servicio aplica transformaciones específicas para que los datos de Dailymotion encajen en el modelo unificado del proyecto:

| Concepto Dailymotion | Atributo Destino (VideoMiner) | Transformación Aplicada |
| :--- | :--- | :--- |
| **Playlist** | **Channel** | Se mapea `name` y `description`. Los Unix timestamps se convierten a ISO 8601. |
| **Video owner** | **User** | Extrae el `username` y `avatar_120_url` para el perfil del autor. |
| **Tags** | **Comments** | Se transforman las etiquetas del vídeo en comentarios de texto para el modelo común. |
| **Subtitles** | **Captions** | Mapea el enlace directo (`link`) y el código de idioma del subtítulo. |

---

## 4. Documentación Interactiva (Swagger OAS)
El microservicio expone su interfaz de pruebas y documentación técnica a través de Swagger UI.

*   **Acceso**: `http://localhost:8082/swagger-ui/index.html`
*   **Endpoints**:
    *   `GET /api/channels/{id}`: Extrae y visualiza los datos en JSON sin persistirlos.
    *   `POST /api/channels/{id}`: Extrae los datos y los envía automáticamente a VideoMiner (`8080`).
*   **Parámetros**:
    *   `maxVideos` (defecto 10): Cantidad de vídeos a procesar.
    *   `maxPages` (defecto 2): Límite de páginas a consultar en la API de Dailymotion.

---

## 5. Gestión de Errores
El microservicio gestiona las excepciones de red y recursos de forma estandarizada:

*   **404 Not Found**: Si la playlist o canal no existe, se lanza una `ChannelNotFoundException` que devuelve un error REST 404 con el mensaje "El canal no existe en Dailymotion".
*   **Validación**: Los parámetros de entrada están protegidos mediante `@Min(1)` para asegurar valores positivos.

---

## 6. Calidad y Testing
Se ha implementado una suite de pruebas completa para asegurar el correcto funcionamiento del adaptador:

*   **Tests de Integración (MockMvc)**: `DailymotionMinerApiTests` verifica el comportamiento de los controladores y la integración de servicios.
*   **Tests Unitarios (Mockito)**: `ApiChannelServiceTests` valida la lógica de extracción, construcción de URLs y manejo de respuestas nulas.
*   **Tests de Modelo**: `CaptionValidationTests` asegura la integridad de los datos en los modelos internos.

---

## 7. Instrucciones de Ejecución
### Requisito: VideoMiner debe estar iniciado en el puerto 8080.

```bash
# Ejecutar suite de pruebas
.\mvnw.cmd clean test

# Iniciar el microservicio
.\mvnw.cmd spring-boot:run