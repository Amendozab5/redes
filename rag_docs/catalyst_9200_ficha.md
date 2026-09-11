# Cisco Catalyst 9200L — Switch de acceso de Capa 2/3

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `catalyst_9200`
- **Fabricante / modelo:** Cisco Systems — Catalyst 9200L, leído en la serigrafía frontal
- **Ubicación en el laboratorio:** rack FYTEC. Hay **dos unidades**, alternadas con los
  Catalyst 8200
- **Sistema operativo:** Cisco IOS-XE
- **Puertos:** banda de 24 puertos de acceso marcados de `1X` a `24X`, más un grupo de
  uplinks fijos identificados como `G1`, `G2`, `G3`…

> La designación **9200L** corresponde a la variante de **uplinks fijos** (no modulares), a
> diferencia del 9200 estándar. *(Verificar contra el datasheet del modelo exacto.)*

## 2. Función
Switch de acceso de la familia Catalyst 9000, la generación actual de conmutación empresarial de
Cisco. Frente al Catalyst 2960 aporta:

- Sistema operativo **IOS-XE**, con soporte de automatización y programabilidad
- Mayor rendimiento y capacidad de uplink
- Funciones de seguridad y telemetría más avanzadas

En el laboratorio es el equipo de referencia para prácticas de redes modernas.

## 3. Componentes principales
- 24 puertos de acceso Gigabit Ethernet (numerados 1X a 24X)
- Puertos uplink fijos
- Puertos USB y micro-USB de consola en el frente
- Puerto de consola RJ-45 y puerto de gestión
- LED por puerto y LED de estado del sistema
- Ventilación frontal y posterior

## 4. Procedimiento básico de uso
1. Conectar por consola a **9600 8N1** (RJ-45 o micro-USB frontal).
2. Entrar a configuración:
   ```
   enable
   configure terminal
   ```
3. Configurar VLANs y puertos igual que en un 2960, con sintaxis IOS-XE.
4. Para enrutamiento entre VLANs, habilitar `ip routing` y crear las SVI correspondientes.
5. Verificar con `show version`, `show vlan brief` e `show interfaces status`.
6. Guardar con `write memory`.

## 5. Elementos de protección personal
- Pulsera antiestática al manipular el hardware.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- Descarga electrostática sobre puertos y uplinks.
- Bucles de red si se altera la configuración de Spanning Tree.
- **Confusión con el Catalyst 8200** al documentar el inventario: verificar siempre la
  serigrafía antes de registrar el modelo.
- Los tiempos de arranque de IOS-XE son notablemente mayores que los de IOS clásico: no
  interrumpir la alimentación durante el arranque.

## 7. Mantenimiento
- No obstruir la ventilación frontal ni la posterior.
- Verificar el estado del LED de sistema antes de iniciar la práctica.
- Respaldar la configuración antes de cada sesión.

## 8. Prácticas académicas relacionadas
- VLANs, trunking y seguridad de puertos sobre IOS-XE.
- Calidad de servicio (QoS) en el acceso.
- Fundamentos de automatización y programabilidad de red.
- Comparación de generaciones: Catalyst 2960 frente a Catalyst 9200.
