# Cisco Catalyst 3750G — Switch apilable de Capa 3

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `catalyst_3750g`
- **Fabricante / modelo:** Cisco Systems — Catalyst 3750G
- **Ubicación en el laboratorio:** rack FYTEC, sobre el ASA 5520
- **Aspecto:** chasis 1U gris oliva, con la fibra amarilla del laboratorio enrollada al frente
- **Puertos:** Gigabit Ethernet de cobre más uplinks *(verificar cantidad en la etiqueta)*

## 2. Función
Switch Gigabit con capacidades de **enrutamiento de Capa 3**. Cumple dos papeles didácticos:

1. **Enrutamiento inter-VLAN:** permite que dispositivos de VLANs distintas se comuniquen sin
   necesitar un router externo, usando interfaces virtuales de VLAN (SVI).
2. **Apilamiento StackWise:** varios 3750G se unen por cables de apilamiento traseros y se
   administran como **una sola unidad lógica**, con una única dirección de gestión.

## 3. Componentes principales
- Puertos Gigabit Ethernet de cobre
- Puertos uplink SFP para fibra
- **Puertos StackWise en la parte trasera** (no confundir con puertos de datos)
- Puerto de consola RJ-45
- Conector para fuente de alimentación redundante RPS *(según variante)*
- LED de estado del stack y del máster

## 4. Procedimiento básico de uso
1. Conectar por consola a **9600 8N1**.
2. Habilitar el enrutamiento, que en este switch **está desactivado por defecto**:
   ```
   enable
   configure terminal
   ip routing
   ```
3. Crear las interfaces virtuales de VLAN:
   ```
   interface vlan 10
    ip address 192.168.10.1 255.255.255.0
    no shutdown
   ```
4. Verificar con `show ip route`, `show ip interface brief` y `show vlan brief`.
5. Para apilamiento: apagar los switches, conectar los cables StackWise en anillo, encender
   primero el que se desea como máster y verificar con `show switch`.
6. Guardar con `write memory`.

## 5. Elementos de protección personal
- Pulsera antiestática al manipular módulos SFP o cables StackWise.
- Calzado cerrado y guantes de manipulación al montar el equipo en el rack.

## 6. Riesgos asociados
- **Peso del equipo:** el chasis es notablemente más pesado que un switch de acceso. Montarlo
  entre dos personas y sujetarlo hasta colocar los cuatro tornillos.
- **Conexión o desconexión de cables StackWise con el equipo encendido:** puede provocar la
  reelección del máster y la caída momentánea de todo el stack.
- Descarga electrostática sobre los módulos SFP.
- Olvidar `ip routing` es la causa más frecuente de que "no funcione" el inter-VLAN: el
  equipo se comporta como un switch de Capa 2 hasta que se habilita.

## 7. Mantenimiento
- Limpiar los conectores de fibra con paño libre de pelusa antes de insertarlos en el SFP.
- No mirar directamente el extremo de una fibra conectada.
- Verificar la sujeción de los cables StackWise: se aflojan con la vibración.

## 8. Prácticas académicas relacionadas
- Enrutamiento inter-VLAN con interfaces SVI.
- Apilamiento StackWise y administración unificada.
- EtherChannel y agregación de enlaces.
- Redundancia de primer salto (HSRP/VRRP) en topologías de laboratorio.
