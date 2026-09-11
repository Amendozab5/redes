# Cisco Catalyst 8200 (C8200L-1N-4T) — Plataforma de borde / router SD-WAN

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `catalyst_8200`
- **Fabricante / modelo:** Cisco Systems — Catalyst 8200 Series Edge Platform,
  referencia **C8200L-1N-4T** leída en la serigrafía del chasis
- **Ubicación en el laboratorio:** rack FYTEC. Hay **dos unidades**, alternadas con los
  Catalyst 9200
- **Sistema operativo:** Cisco IOS-XE

> **Nota de nomenclatura importante.** En una versión anterior del conjunto de datos este equipo
> se etiquetó por error como *catalyst_8300*. La serigrafía del chasis dice **C8200L-1N-4T**, por
> lo que la clase correcta es `catalyst_8200`. Si aparece "catalyst_8300" en algún archivo
> antiguo del proyecto, es el nombre equivocado.

## 2. Función
Router empresarial de borde de nueva generación. Conecta la red del laboratorio con redes
externas y sirve como plataforma para prácticas de:

- Enrutamiento WAN y protocolos dinámicos
- **SD-WAN**: políticas de selección de camino y superposición de red
- Servicios de borde: NAT, ACL, calidad de servicio

Su designación describe el hardware: **1N** = una ranura para módulo NIM, **4T** = cuatro
puertos de red integrados.

## 3. Componentes principales
- Cuatro puertos Ethernet integrados WAN/LAN, con los grupos de puertos marcados en amarillo
- **Una ranura NIM** (Network Interface Module) para módulos de servicio intercambiables
- Puerto de consola y puerto de gestión dedicado
- Puerto USB para configuración o licenciamiento
- LED de estado del sistema y por puerto

## 4. Procedimiento básico de uso
1. Conectar por consola a **9600 8N1**.
2. Entrar a configuración:
   ```
   enable
   configure terminal
   ```
3. Configurar una interfaz WAN:
   ```
   interface GigabitEthernet0/0/0
    ip address <dirección> <máscara>
    no shutdown
   ```
4. Configurar el protocolo de enrutamiento que indique la práctica (OSPF, EIGRP o BGP).
5. Verificar con `show ip interface brief`, `show ip route` y `show platform`.
6. Guardar con `write memory` o `copy running-config startup-config`.

## 5. Elementos de protección personal
- Pulsera antiestática, **obligatoria** al insertar o retirar módulos NIM.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- **Inserción o extracción de módulos NIM con el equipo encendido:** salvo que el módulo sea
  específicamente de intercambio en caliente, se debe apagar el equipo. Consultar al docente.
- Descarga electrostática sobre el conector del NIM, que queda expuesto con la bahía vacía.
- **Interrupción de la conectividad del laboratorio** por un error en la configuración WAN:
  este equipo está en la ruta hacia el exterior.
- Confundirlo con el Catalyst 9200 al etiquetar o documentar: a distancia son dos cajas blancas
  del mismo tamaño. Se distinguen porque el 8200 tiene **pocos puertos con recuadro amarillo y
  una bahía de módulo grande**, mientras el 9200 tiene **una banda continua de 24 puertos**.

## 7. Mantenimiento
- Mantener tapada la bahía NIM cuando esté vacía, para evitar polvo y contacto accidental.
- Verificar la versión de IOS-XE antes de aplicar configuraciones de práctica.
- Respaldar la configuración antes de cada sesión.

## 8. Prácticas académicas relacionadas
- Enrutamiento WAN y protocolos dinámicos.
- Fundamentos de SD-WAN y políticas de camino.
- Servicios de borde: NAT, ACL y QoS.
- Comparación entre un ISR clásico (Cisco 2800) y una plataforma de borde moderna.
