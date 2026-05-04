## Dailymotion Miner

Microservicio adaptador que lee datos de Dailymotion y los transforma al modelo común de VideoMiner.

### Estructura

- `controller`: expone la API REST del adaptador.
- `service`: integra con la API de Dailymotion y con VideoMiner.
- `model`: define el modelo común y los objetos externos de la API.
- `exception`: traduce errores de recurso no encontrado a `404`.

### Endpoint principal

- `GET /api/channels/{id}?maxVideos=10&maxPages=2`
- `POST /api/channels/{id}?maxVideos=10&maxPages=2`

### Comportamiento

- Usa `tags` como comentarios porque Dailymotion no ofrece comentarios equivalentes.
- Usa `subtitles` como captions.
- Devuelve `404` si la playlist no existe.


