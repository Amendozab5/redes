# Normas generales de seguridad y operación — Laboratorio de Redes y Telecomunicaciones

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


Este documento aplica a **todos** los equipos del laboratorio. Las fichas individuales de cada
equipo lo complementan con los riesgos específicos de ese modelo.

## 1. Antes de iniciar la práctica
1. Solicitar autorización al docente o responsable del laboratorio.
2. Verificar que el rack esté energizado y que no haya cables sueltos en el piso.
3. Identificar la ubicación del interruptor general y del extintor.
4. **Respaldar la configuración** de los equipos que se vayan a modificar.
5. Documentar el estado inicial: qué equipo está en qué posición y con qué cables.

## 2. Elementos de protección personal
| Situación | EPP requerido |
|---|---|
| Operación por consola, sin abrir equipos | Ninguno especial |
| Manipulación de tarjetas, módulos NIM/HWIC/SFP | **Pulsera antiestática** |
| Montaje o desmontaje de equipos del rack | Calzado cerrado; dos personas |
| Trabajo con fibra óptica | No mirar el extremo de la fibra |
| Manipulación de puertos FXS de la central telefónica | Desconectar líneas primero |

## 3. Riesgos transversales del laboratorio

**Descarga electrostática (ESD).** Es el riesgo más frecuente y el más subestimado: no se siente
y aun así daña componentes. Usar pulsera antiestática conectada al chasis siempre que se
manipule hardware interno.

**Bucles de red.** Un patch cord conectado por error entre dos puertos del mismo switch genera
una tormenta de difusión que satura el segmento en segundos. Verificar ambos extremos antes de
conectar. No desactivar Spanning Tree sin autorización.

**Pérdida de conectividad por configuración.** Los equipos de borde (ASA 5520, Catalyst 8200)
están en la ruta hacia el exterior: un error de configuración deja sin servicio a todo el
laboratorio. Trabajar siempre con acceso por consola disponible como respaldo.

**Peso de los equipos.** Los switches de Capa 3 y los appliances son pesados. Montarlos entre
dos personas y colocar los cuatro tornillos antes de soltar.

**Superficies calientes.** La parte posterior de los equipos alcanza temperaturas elevadas tras
uso prolongado.

## 4. Buenas prácticas de operación
- **Guardar siempre la configuración** con `write memory`. Lo que no se guarda se pierde al
  reiniciar.
- Etiquetar los patch cords antes de desconectarlos.
- No obstruir las rejillas de ventilación con cableado.
- Un cambio a la vez, verificando después de cada uno.
- Dejar el rack como se encontró al terminar la práctica.

## 5. En caso de incidente
1. Si hay olor a quemado, humo o chispas: **cortar la alimentación desde el interruptor general**
   y avisar de inmediato al responsable del laboratorio.
2. Si un equipo deja de responder: no forzar el reinicio repetidamente; consultar al docente.
3. Si se pierde la conectividad del laboratorio: informar antes de intentar revertir cambios,
   para que quede registro de qué se modificó.
4. Reportar todo daño físico, por menor que parezca.

## 6. Inventario de equipos cubiertos
| Clase del detector | Equipo | Unidades | Ubicación |
|---|---|---|---|
| `asa_5520` | Cisco ASA 5520 | 1 | Rack FYTEC |
| `catalyst_2960` | Cisco Catalyst 2960 | 1 | Rack BEAUCOUP |
| `catalyst_3750g` | Cisco Catalyst 3750G | 1 | Rack FYTEC |
| `catalyst_8200` | Cisco Catalyst 8200 (C8200L-1N-4T) | 2 | Rack FYTEC |
| `catalyst_9200` | Cisco Catalyst 9200L | 2 | Rack FYTEC |
| `cisco_2800` | Cisco 2800 Series ISR | 4 | Rack BEAUCOUP |
| `grandstream_ucm` | Grandstream UCM6108 | 1 | Rack de comunicaciones |
| `nexxt_switch` | Nexxt Solutions 24 puertos | 1 | Rack de comunicaciones |
| `telefono_yealink` | Teléfono IP Yealink | varios | Escritorios |
