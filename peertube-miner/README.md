# PeerTubeMiner: Adaptador de Extracción y Minería

**Microservicio adaptador encargado de extraer, transformar y cargar datos desde PeerTube hacia el sistema central VideoMiner.**

---

## 1. Descripción General
PeerTubeMiner actúa como un puente (o adaptador) entre la API externa de **PeerTube** y el modelo de datos unificado de nuestro proyecto. Su función principal es "minar" canales específicos, procesar sus vídeos, comentarios y subtítulos, y enviarlos automáticamente al almacén central **VideoMiner** para su persistencia.

---

## 2. Arquitectura y Tecnologías
Este microservicio está construido con **Spring Boot 3.5.13** y **Java 21**, operando de forma independiente en el puerto **8081**.

*   **Modelo de Capas**: Controller, Service, Mapper y Model (interno/externo).
*   **External Models**: Clases espejo que reciben el JSON crudo de la API de PeerTube.
*   **Mapper Estático**: Lógica de traducción de tipos y normalización de datos.
*   **Integración REST**: Uso de `RestTemplate` para comunicación síncrona con servicios externos.

---

## 3. Reglas de Mapeo (Traducción)
Para cumplir con el modelo unificado, el servicio aplica las siguientes transformaciones automáticas:

| Concepto PeerTube | Atributo Destino (VideoMiner) | Nota de Transformación | |
| :--- | :--- | :--- | :--- |
| **Account** | **User** | Mapea `displayName` a `name` y recupera el primer avatar. | |
| **Comment Threads** | **Comments** | Extrae el texto y la fecha original de creación. | |
| **Video Metadata** | **Video** | Traduce `publishedAt` a `releaseTime`. | |
| **Captions** | **Captions** | Genera un enlace funcional basado en el ID del subtítulo. | |

---

## 4. Documentación Interactiva (Swagger OAS)
Puedes probar la extracción y previsualizar los datos transformados en tiempo real.

*   **Cómo entrar**: 
    *   URL: `http://localhost:8081/swagger-ui/index.html`
*   **Endpoints Principales**:
    *   `GET /api/channels/{id}`: Extrae los datos y los muestra en JSON (sin guardarlos).
    *   `POST /api/channels/{id}`: Extrae los datos y los envía automáticamente a VideoMiner.
*   **Parámetros de Minería**:
    *   `maxVideos` (defecto 10): Límite de vídeos a extraer.
    *   `maxComments` (defecto 2): Límite de comentarios por vídeo.
    *   Soporta filtrado por `name` (nombre del vídeo), paginación y ordenación.

---

## 5. Gestión de Errores
El microservicio garantiza una comunicación REST limpia:

*   **404 Not Found**: Si el ID del canal no existe en PeerTube, se lanza una `ChannelNotFoundException` que devuelve un error 404 estandarizado.
*   **Validación (400)**: Los parámetros `maxVideos` y `maxComments` están protegidos para no aceptar valores negativos.

---

## 6. Calidad y Testing
Hemos implementado una suite de pruebas exhaustiva para asegurar la fiabilidad del adaptador:

*   **Tests de Integración (MockMvc)**: Validan el comportamiento de los endpoints y el respeto a los parámetros por defecto.

---

## 7. Ejecución
### Requisitos: VideoMiner debe estar iniciado en el puerto 8080.

```powershell
# 1. Compilar y pasar los tests de calidad
.\mvnw.cmd clean test

# 2. Iniciar el adaptador
.\mvnw.cmd spring-boot:run