# Cisco Catalyst 2960 — Switch de acceso de Capa 2

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `catalyst_2960`
- **Fabricante / modelo:** Cisco Systems — Catalyst serie 2960
- **Ubicación en el laboratorio:** rack BEAUCOUP
- **Formato:** 1U para rack de 19", chasis metálico
- **Puertos:** unidad de 48 puertos de acceso *(verificar la variante exacta en la etiqueta)*

## 2. Función
Switch Ethernet de **Capa 2** para conectar dispositivos finales dentro de una LAN. No enruta
entre redes: conmuta tramas dentro del mismo dominio de difusión y segmenta el tráfico mediante
VLANs. Es el equipo de referencia para las prácticas de conmutación.

## 3. Componentes principales
- Puertos de acceso Fast Ethernet o Gigabit Ethernet según variante
- Puertos uplink (GBIC/SFP en algunas variantes)
- Puerto de consola RJ-45
- LED por puerto: enlace, actividad y velocidad
- Botón **Mode** para cambiar qué indica el LED de cada puerto
- Fuente de alimentación interna

## 4. Procedimiento básico de uso
1. Conectar por consola a **9600 8N1**.
2. Entrar a configuración:
   ```
   enable
   configure terminal
   ```
3. Crear VLANs y asignar puertos de acceso:
   ```
   vlan 10
    name ADMINISTRACION
   interface range FastEthernet0/1 - 12
    switchport mode access
    switchport access vlan 10
   ```
4. Configurar enlaces troncales entre switches con encapsulación 802.1Q:
   ```
   interface GigabitEthernet0/1
    switchport mode trunk
    switchport trunk allowed vlan 10,20,30
   ```
5. Verificar con `show vlan brief`, `show interfaces trunk` y `show mac address-table`.
6. Guardar con `write memory`.

## 5. Elementos de protección personal
- Pulsera antiestática al manipular el hardware o insertar módulos SFP.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- **Bucles de red (loops):** conectar dos puertos del mismo switch entre sí, o dos switches por
  dos cables, genera una tormenta de difusión que satura el segmento en segundos. El Spanning
  Tree Protocol lo previene: **no desactivarlo** salvo indicación expresa del docente.
- Descarga electrostática sobre los puertos o los módulos SFP.
- Dejar un puerto troncal mal configurado puede exponer VLANs que deberían estar aisladas.

## 7. Mantenimiento
- Verificar que los ventiladores no estén obstruidos.
- Revisar el estado de los conectores RJ-45: las pestañas rotas causan enlaces intermitentes.
- Etiquetar el patch cord antes de desconectarlo.

## 8. Prácticas académicas relacionadas
- Creación y asignación de VLANs.
- Enlaces troncales 802.1Q.
- Spanning Tree Protocol: elección de puente raíz y estados de puerto.
- Seguridad de puertos (port-security) y control de direcciones MAC.
