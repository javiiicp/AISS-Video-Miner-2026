# AISS-Video-Miner-2026
Herramienta de minería de datos para procesar información de creadores en plataformas multimedia como PeerTube y Dailymotion.

## Arquitectura

El proyecto se divide en tres microservicios:

- [videominer](videominer) centraliza la persistencia en H2 y expone la API REST pública.
- [peertube-miner](peertube-miner) lee canales de PeerTube y los envía a VideoMiner.
- [dailymotion-miner](dailymotion-miner) lee canales de Dailymotion y los envía a VideoMiner.

## Estructura

- Cada microservicio tiene su propio `pom.xml`, código fuente y tests.
- Los adaptadores comparten una estructura homogénea basada en `controller`, `service`, `model` y `exception`.
- VideoMiner añade `repository` porque es el único módulo que persiste datos.

## Requisitos cubiertos

- PeerTubeMiner expone una operación `POST /api/channels/{id}` con `maxVideos` y `maxComments`.
- DailymotionMiner expone una operación `POST /api/channels/{id}` con `maxVideos` y `maxPages`.
- VideoMiner expone operaciones para `Channel`, `Video`, `Caption` y `Comment`.
- Los tres servicios devuelven `404` cuando un recurso no existe.

## Ejecución

Cada módulo puede ejecutarse de forma independiente desde su propia carpeta con Maven Wrapper.

<img width="476" height="291" alt="image" src="https://github.com/user-attachments/assets/4dcf3572-3e56-4132-a91f-3f4e45ad7e73" />
