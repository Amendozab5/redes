# Nexxt Solutions — Switch no administrable de 24 puertos

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `nexxt_switch`
- **Fabricante:** Nexxt Solutions — *(verificar el modelo exacto en la etiqueta inferior)*
- **Ubicación en el laboratorio:** rack de comunicaciones, bajo la central Grandstream UCM6108
- **Aspecto:** chasis blanco de 1U, 24 puertos, con el logotipo naranja del fabricante
- **Tipo:** switch **no administrable** (unmanaged)

## 2. Función
Switch de acceso que amplía la cantidad de puertos disponibles en el rack. Conmuta tramas
Ethernet aprendiendo direcciones MAC, pero **no admite configuración**: no tiene VLANs, ni CLI,
ni dirección IP de gestión.

Su valor didáctico es precisamente ese contraste: puesto junto a un Catalyst, permite demostrar
en la práctica qué se pierde al usar un switch no administrable.

| | Switch administrable (Catalyst) | Switch no administrable (Nexxt) |
|---|---|---|
| VLANs | Sí | No |
| Consola / CLI | Sí | No |
| Spanning Tree configurable | Sí | No |
| Monitoreo y estadísticas | Sí | Solo LED |
| Costo | Alto | Bajo |

## 3. Componentes principales
- 24 puertos Ethernet RJ-45 con negociación automática de velocidad y dúplex
- LED de enlace y actividad por puerto
- Fuente de alimentación integrada
- **No tiene** puerto de consola ni interfaz de gestión

## 4. Procedimiento básico de uso
1. Conectar el equipo a la alimentación.
2. Conectar los cables de red en cualquier puerto: funciona por conexión directa
   (*plug and play*), sin configuración.
3. Verificar el enlace observando el LED del puerto: encendido indica enlace establecido,
   parpadeo indica tráfico.
4. Para diagnosticar, se depende únicamente de los LED y de pruebas desde los equipos finales
   (`ping`, `tracert`), porque el switch no reporta nada por sí mismo.

## 5. Elementos de protección personal
- Pulsera antiestática al manipular el hardware.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- **Bucles de red:** este es el riesgo principal. Al no tener Spanning Tree configurable, un
  cable conectado por error entre dos puertos del mismo switch genera una tormenta de difusión
  que puede saturar todo el segmento y dejar la red inutilizable. **Verificar siempre ambos
  extremos de cada patch cord antes de conectarlo.**
- Descarga electrostática sobre los puertos.
- Sobrecarga del enlace hacia el switch principal: todo el tráfico de los 24 puertos comparte
  la subida.

## 7. Mantenimiento
- Revisar las pestañas de los conectores RJ-45: las rotas causan enlaces intermitentes.
- Mantener el cableado ordenado para no obstruir la ventilación.
- Etiquetar los patch cords: sin gestión, la única forma de rastrear una conexión es el cable.

## 8. Prácticas académicas relacionadas
- Conmutación básica y aprendizaje de direcciones MAC.
- Dominios de colisión y dominios de difusión.
- Diferencias prácticas entre switch administrable y no administrable.
- Diagnóstico de conectividad sin herramientas de gestión.
