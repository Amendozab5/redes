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

## Clases detectadas

El modelo YOLO reconoce 9 clases de equipos del laboratorio:

| Clase | Tipo de equipo | Descripción |
|-------|----------------|-------------|
| `catalyst_8200` | Router de borde | Plataforma de borde / router SD-WAN (C8200L-1N-4T) |
| `catalyst_9200` | Switch moderno | Switch de acceso de Capa 2/3 (Catalyst 9200L, uplinks fijos) |
| `catalyst_2960` | Switch capa 2 | Switch de acceso de Capa 2 para conmutación LAN/VLAN |
| `catalyst_3750g` | Switch apilable | Switch Gigabit apilable de Capa 3 (enrutamiento inter-VLAN) |
| `cisco_2800` | Router ISR | Router de servicios integrados de generación clásica (uso didáctico) |
| `asa_5520` | Firewall | Appliance de seguridad perimetral (firewall/VPN) |
| `grandstream_ucm` | Central telefónica IP | Central IP (UCM6108) que gestiona las extensiones SIP |
| `nexxt_switch` | Switch no administrable | Switch de acceso no administrable de 24 puertos (sin VLANs/CLI/IP) |
| `telefono_yealink` | Teléfono IP | Terminal de telefonía IP de escritorio (se registra contra la central SIP) |

## Entrenamiento del modelo

- Cuaderno: `entrenamiento_redes_uteq_v2.ipynb` (en la raíz del repo).
- Modelo base: YOLO11n (pesos preentrenados en COCO).
- Entorno: Google Colab, GPU T4.
- Métrica v1 (144 imágenes, 6 clases): mAP50 = 0.995.
- Nota metodológica: el dataset v2 ampliado tiene 206 imágenes (144 train / 41 valid /
  21 test) sobre un plan de 800; es el modelo v0.
- Aclaración honesta: no se declara que el modelo "ya funciona" en producción; es un
  avance verificable.

## Base documental del RAG

- Carpeta `rag_docs/` en la raíz del repo.
- Contiene 10 archivos `.md`: 1 de seguridad general + 9 fichas técnicas (una por equipo).
- Estos documentos NO viajan dentro del APK: están subidos a OpenAI Storage
  (`purpose=assistants`) y se consultan mediante vector stores individuales por equipo
  (referenciados desde `EquipoRepository.kt`).
- El asistente usa la OpenAI Responses API con `file_search`.
- Cada respuesta incluye la fuente consultada.

## Estado actual

- [x] App Android (cámara, detección, ficha técnica, chat) — compila y corre.
- [x] Modelo `.tflite` (YOLO11n) integrado en `assets/redes_uteq.tflite`. Corresponde al
      modelo v2 (9 clases); el mAP50 = 0.995 documentado arriba corresponde a la v1 (6
      clases, 144 imágenes), no al modelo actual.
      Formato de entrada verificado contra el modelo real: NCHW `[1,3,640,640]`
      (detectado automáticamente en `YoloDetector`), salida `[1,10,8400]` sin NMS
      integrado. Orden de clases confirmado con la metadata embebida del propio
      `.tflite`.
- [ ] API key y Vector Stores de OpenAI configurados.
- [x] Base documental del RAG incluida en `rag_docs/` (10 archivos `.md`). Cuaderno de
      entrenamiento en `entrenamiento_redes_uteq_v2.ipynb`.
