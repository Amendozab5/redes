# Base documental del RAG

Carpeta con los documentos fuente que alimentan al asistente inteligente del proyecto:
las fichas técnicas de cada equipo del laboratorio y las normas generales de seguridad,
redactadas a partir de la documentación pública del fabricante y de la inspección directa
de los equipos. Estos documentos viven en el repositorio por razones de trazabilidad y
mantenimiento, no porque se empaqueten en la app.

## Arquitectura RAG (v0)

- Los archivos `.md` están subidos a OpenAI Storage con `purpose=assistants`.
- Cada equipo tiene su propio vector store en OpenAI, referenciado desde
  `EquipoRepository.kt`.
- El cliente Android llama a la OpenAI Responses API con `file_search` sobre el vector
  store correspondiente al equipo seleccionado.
- Los documentos NO viajan dentro del APK. Desde el teléfono solo se envía la pregunta
  del usuario.
- Cada respuesta cita la fuente consultada. Si el asistente no encuentra información,
  responde que no dispone de datos suficientes y recomienda consultar al docente o al
  responsable del laboratorio.

## Aclaraciones sobre la v0

- No existe todavía un backend o servidor MCP propio.
- La rúbrica pide un backend que busque fragmentos y entregue solo el contexto necesario.
  En la v0 esa tarea la realiza OpenAI mediante `file_search` sobre los vector stores.
- Limitación conocida: la API key de OpenAI viaja dentro del APK (vía `local.properties`
  compilado en `BuildConfig`). La siguiente etapa contempla migrar a un backend MCP propio
  para cumplir literalmente la rúbrica y eliminar la exposición de la key.

## Índice

| Archivo | Descripción |
|---------|-------------|
| `00_seguridad_general_laboratorio.md` | Normas generales de seguridad y operación aplicables a todos los equipos del laboratorio. |
| `asa_5520_ficha.md` | Cisco ASA 5520: appliance de seguridad adaptable (firewall/VPN). |
| `catalyst_2960_ficha.md` | Cisco Catalyst 2960: switch de acceso de Capa 2 para conmutación LAN/VLAN. |
| `catalyst_3750g_ficha.md` | Cisco Catalyst 3750G: switch Gigabit apilable de Capa 3 con enrutamiento inter-VLAN. |
| `catalyst_8200_ficha.md` | Cisco Catalyst 8200 (C8200L-1N-4T): plataforma de borde / router SD-WAN. |
| `catalyst_9200_ficha.md` | Cisco Catalyst 9200L: switch de acceso de Capa 2/3 con uplinks fijos. |
| `cisco_2800_ficha.md` | Cisco 2800 Series ISR: router de servicios integrados de generación clásica (uso didáctico). |
| `grandstream_ucm_ficha.md` | Grandstream UCM6108: central telefónica IP (IP PBX) que gestiona las extensiones SIP. |
| `nexxt_switch_ficha.md` | Nexxt Solutions: switch no administrable de 24 puertos. |
| `telefono_yealink_ficha.md` | Teléfono IP de escritorio (Yealink): terminal de telefonía IP. |
