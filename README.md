# Asistente Móvil Inteligente — Laboratorio de Redes y Telecomunicaciones (UTEQ)

Proyecto de la asignatura **Aplicaciones Móviles** (Ingeniería de Software, UTEQ).
App Android que detecta en tiempo real equipos del Laboratorio de Redes y
Telecomunicaciones usando un modelo YOLO (TensorFlow Lite), muestra la ficha
técnica del equipo detectado y permite preguntarle a un asistente inteligente
(OpenAI Responses API + `file_search`/RAG) que responde solo con base en los
manuales oficiales del laboratorio.

## Estructura

- `detection/` — cámara en vivo (CameraX), inferencia TFLite (`YoloDetector`) y
  overlay de cuadros delimitadores.
- `data/` — ficha técnica de cada equipo (`Equipo`, `EquipoRepository`).
- `ui/` — pantallas de ficha técnica y chat.
- `network/` — cliente de la API de OpenAI (`OpenAIClient`).

## Cómo correrlo

1. Abrir el proyecto en Android Studio y esperar el sync de Gradle.
2. Copiar el modelo entrenado y sus labels en `app/src/main/assets/`
   (ver [`app/src/main/assets/LEEME.txt`](app/src/main/assets/LEEME.txt)).
3. Configurar `OPENAI_API_KEY` en `local.properties` (no se sube al repo) y los
   `vectorStoreId` de cada equipo en
   [`EquipoRepository.kt`](app/src/main/java/com/example/proyecto/data/EquipoRepository.kt).
4. Ejecutar en un dispositivo/emulador con cámara.

## Estado actual

- [x] App Android (cámara, detección, ficha técnica, chat) — compila y corre.
- [x] Modelo `.tflite` entrenado (YOLO11n, mAP50 0.995) integrado en `assets/redes_uteq.tflite`.
      Formato de entrada verificado contra el modelo real: NCHW `[1,3,640,640]`
      (detectado automáticamente en `YoloDetector`), salida `[1,10,8400]` sin NMS
      integrado. Orden de clases confirmado con la metadata embebida del propio
      `.tflite`.
- [ ] API key y Vector Stores de OpenAI configurados.
