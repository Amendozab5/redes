# Cisco 2800 Series ISR — Router de servicios integrados

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `cisco_2800`
- **Fabricante / modelo:** Cisco Systems — Integrated Services Router serie 2800
  (2801 / 2811 / 2821 / 2851 según variante — *verificar la etiqueta de la unidad*)
- **Ubicación en el laboratorio:** rack BEAUCOUP. Hay **cuatro unidades**
- **Aspecto:** chasis azul grisáceo de 1U
- **Estado del ciclo de vida:** fin de venta y fin de soporte. Se conserva con fines didácticos.

## 2. Función
Router de servicios integrados de generación clásica. Es el equipo con el que se practican los
fundamentos de enrutamiento:

- Direccionamiento IP y enrutamiento estático
- Protocolos dinámicos: RIP, OSPF, EIGRP
- Traducción de direcciones (NAT/PAT)
- Listas de control de acceso (ACL)

Al haber cuatro unidades, permite montar topologías con varios saltos.

## 3. Componentes principales
- Puertos Ethernet integrados WAN/LAN *(cantidad y velocidad según variante — verificar)*
- **Ranuras HWIC/WIC** para tarjetas de interfaz: serie, ADSL, switching
- Puerto de consola RJ-45 y puerto auxiliar
- Memoria Flash (CompactFlash) y DRAM
- Fuente de alimentación interna
- LED de estado del sistema y de actividad

## 4. Procedimiento básico de uso
1. Conectar por consola a **9600 8N1**.
2. Entrar a configuración:
   ```
   enable
   configure terminal
   ```
3. Configurar interfaces:
   ```
   interface FastEthernet0/0
    ip address 192.168.1.1 255.255.255.0
    no shutdown
   ```
   **Recordar `no shutdown`:** en los routers Cisco las interfaces están administrativamente
   apagadas por defecto. Es el error más frecuente en las prácticas.
4. Configurar el protocolo de enrutamiento de la práctica.
5. Verificar con `show ip interface brief`, `show ip route` y `show running-config`.
6. Guardar con `write memory`.

## 5. Elementos de protección personal
- Pulsera antiestática, **obligatoria** al insertar o retirar tarjetas HWIC/WIC.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- **Las tarjetas HWIC/WIC NO son de intercambio en caliente:** apagar el router antes de
  insertarlas o retirarlas. Hacerlo encendido puede dañar la tarjeta y la placa madre.
- Descarga electrostática sobre los conectores de las ranuras.
- **Una ACL mal escrita bloquea tráfico legítimo.** Recordar que toda ACL termina con un
  `deny any` implícito.
- Superficie caliente en la parte posterior tras uso prolongado.

## 7. Mantenimiento
- Mantener tapadas las ranuras HWIC vacías.
- Limpiar las rejillas de ventilación con el equipo apagado.
- Verificar el estado de la batería del reloj interno si el equipo pierde la hora.

## 8. Prácticas académicas relacionadas
- Enrutamiento estático y dinámico (RIP, OSPF, EIGRP).
- NAT y PAT hacia una red externa.
- Listas de control de acceso estándar y extendidas.
- Comparación entre un ISR clásico y una plataforma de borde moderna (Catalyst 8200).
