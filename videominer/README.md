# VideoMiner: API REST Central de Almacenamiento

**Microservicio integrador y almacenador de datos multimedia para el proyecto AISS 2026.**

---

## 1. Descripción General
VideoMiner es el componente central encargado de recibir, unificar y persistir la información extraída de plataformas como **PeerTube** y **Dailymotion**. Actúa como un almacén persistente que expone una API REST profesional para la consulta de creadores de contenido, vídeos, comentarios y subtítulos.

---

## 2. Arquitectura y Tecnologías
El microservicio está desarrollado con **Spring Boot 3.5.13** y **Java 21**, siguiendo un diseño robusto de cuatro capas:

*   **Modelos**: Entidades JPA que definen la estructura en la base de datos H2.
*   **Repositorios**: Interfaces de Spring Data JPA para la persistencia de datos.
*   **Servicios**: Capa de lógica de negocio (validaciones, prevención de autores duplicados y gestión de relaciones).
*   **Controladores**: Puntos de entrada REST que gestionan las peticiones y validan los datos.

**Stack Principal:** Spring Boot, JPA/Hibernate, H2 (BD en memoria), SpringDoc OpenAPI (Swagger) y JUnit 5 (Testing).

---

## 3. Modelo de Datos Unificado
Hemos diseñado un esquema común para estandarizar la información proveniente de distintas fuentes:

1.  **Channel (Canal)**: Datos del canal y su lista de vídeos asociados.
2.  **Video (Vídeo)**: Información del metraje, incluyendo su autor, comentarios y subtítulos.
3.  **User (Usuario)**: Representa al autor. 
4.  **Caption (Subtítulo)**: Enlaces e idiomas de los subtítulos vinculados a un vídeo.
5.  **Comment (Comentario)**: Texto y fecha del comentario (o etiquetas en el caso de Dailymotion).

---

## 4. Documentación Interactiva (Swagger OAS)
La API está documentada automáticamente bajo el estándar **OpenAPI 3**.

*   **Cómo entrar**: Una vez arrancada la aplicación, accede a:
    *   URL: `http://localhost:8080/swagger-ui/index.html`
*   **Funcionalidades**:
    *   Visualización de todos los controladores y métodos disponibles.
    *   Botón **"Try it out"** para realizar peticiones reales directamente desde el navegador.
    *   Esquemas detallados de los objetos JSON de entrada y salida.

---

## 5. Acceso a la Base de Datos (H2 Console)
Utilizamos una base de datos **H2 en memoria**, ideal para desarrollo y pruebas rápidas.

*   **Cómo entrar**: Accede vía navegador a:
    *   URL: `http://localhost:8080/h2-ui`
*   **Configuración de Login**:
    *   **JDBC URL**: `jdbc:h2:mem:testdb`
    *   **User Name**: `sa`
    *   **Password**: (vacío)
*   **Nota**: Al ser en memoria, los datos se borran al reiniciar el servicio.

---

## 6. Pruebas con Postman (Flujo Dinámico)
Incluimos una colección avanzada diseñada para entornos con **IDs autogenerativos (UUID)**. El sistema genera identificadores aleatorios en cada inserción, por lo que la colección no utiliza IDs fijos.

* **Archivo**: `postman/VideoMiner.postman_collection.json`
* **Gestión Dinámica de IDs**:
    * La colección utiliza **scripts de prueba (Tests)** que capturan automáticamente el ID generado por la base de datos tras un `POST`.
    * Este ID se almacena en variables de colección (ej: `{{lastUserId}}`, `{{lastChannelId}}`).
    * Las peticiones de `UPDATE` y `DELETE` consumen estas variables automáticamente para probar el ciclo de vida completo de cada recurso.
* **Pasos para realizar las pruebas**:
    1.  Importar la colección en Postman.
    2.  Asegurarse de que la variable base apunta a `http://localhost:8080`.
    3.  **Ejecutar en orden**: Es obligatorio realizar primero los `POST` para que las variables de ID se rellenen antes de intentar actualizar o borrar.
---

## 7. Manejo de Errores y Calidad
El microservicio garantiza respuestas profesionales mediante un manejador global de excepciones:

*   **404 Not Found**: Obligatorio cuando un ID consultado no existe en el sistema.
*   **400 Bad Request**: Devuelto cuando fallan las validaciones de datos (ej: campos obligatorios vacíos).
*   **Paginación**: Todos los listados soportan parámetros `page`, `size` y `sortBy` para asegurar la escalabilidad.

---

## 8. Ejecución del Proyecto
### Requisitos: Java 21+ instalado.

```powershell
# 1. Compilar y ejecutar tests de integración
.\mvnw.cmd clean test

# 2. Iniciar el microservicio
.\mvnw.cmd spring-boot:run