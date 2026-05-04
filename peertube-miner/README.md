## PeerTube Miner

Microservicio adaptador que lee canales de PeerTube y los envía al modelo común de VideoMiner.

### Estructura

- `controller`: expone la API REST del adaptador.
- `service`: integra con la API de PeerTube y con VideoMiner.
- `model`: define el modelo común y los objetos externos de la API.
- `exception`: traduce errores de recurso no encontrado a `404`.

### Endpoint principal

- `GET /api/channels/{id}?maxVideos=10&maxComments=2`
- `POST /api/channels/{id}?maxVideos=10&maxComments=2`

### Comportamiento

- Usa `comment threads` como comentarios.
- Usa `account` como usuario del canal o del vídeo.
- Devuelve `404` si el canal no existe.


