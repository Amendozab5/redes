# Cisco ASA 5520 — Appliance de seguridad adaptable

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `asa_5520`
- **Fabricante / modelo:** Cisco Systems — ASA 5520 (Adaptive Security Appliance)
- **Ubicación en el laboratorio:** rack FYTEC, posición inferior
- **Formato:** chasis de 1U para rack de 19", color azul oscuro con rejilla frontal diagonal
- **Estado del ciclo de vida:** equipo declarado fin de venta y fin de soporte por el fabricante.
  Se conserva con fines exclusivamente didácticos.

## 2. Función
Dispositivo de seguridad perimetral que concentra en un solo appliance tres funciones:

1. **Firewall con inspección de estado (stateful):** decide qué tráfico pasa entre zonas de
   distinta confianza (inside / outside / DMZ) manteniendo tabla de conexiones.
2. **Concentrador VPN:** termina túneles IPsec site-to-site y de acceso remoto.
3. **Control de aplicaciones y NAT:** traducción de direcciones e inspección de protocolos.

En el laboratorio se usa como frontera entre la red de prácticas y la red externa.

## 3. Componentes principales
- Interfaces Ethernet de cobre para las zonas de seguridad *(verificar cantidad en la placa)*
- Puerto de consola RJ-45 para gestión por CLI
- Puerto de gestión dedicado (management)
- Ranura para módulo de servicios (SSM/SSP) según configuración
- Fuente de alimentación interna
- LED de estado: Power, Status, Active, VPN

## 4. Procedimiento básico de uso
1. Verificar que el equipo esté energizado y con el LED **Status** en verde fijo.
2. Conectar el cable de consola (RJ-45 a USB/serie) al puerto **Console**.
3. Abrir un emulador de terminal a **9600 8N1, sin control de flujo**.
4. Entrar a modo privilegiado y luego a configuración:
   ```
   enable
   configure terminal
   ```
5. Configurar interfaces con su **nivel de seguridad** (`security-level`), que es el concepto
   propio del ASA: por defecto el tráfico fluye de mayor a menor nivel y se bloquea al revés.
   ```
   interface GigabitEthernet0/0
    nameif outside
    security-level 0
    ip address <dirección> <máscara>
    no shutdown
   ```
6. Definir listas de acceso y NAT según la práctica.
7. Guardar la configuración con `write memory`. **Si no se guarda, se pierde al reiniciar.**

## 5. Elementos de protección personal
- Pulsera antiestática al manipular módulos internos o tarjetas.
- Calzado cerrado durante el trabajo en el rack.
- No se requiere EPP adicional durante la operación normal por consola.

## 6. Riesgos asociados
- **Descarga electrostática** sobre componentes internos al abrir el chasis.
- **Pérdida de conectividad de todo el laboratorio** si se aplica una política de firewall
  incorrecta: el ASA deniega por defecto lo que no está explícitamente permitido.
- **Bloqueo del propio acceso de gestión** al aplicar una ACL sobre la interfaz por la que se
  está administrando. Trabajar siempre con acceso por consola disponible como respaldo.
- Peso del equipo al montarlo o desmontarlo del rack: hacerlo entre dos personas.

## 7. Mantenimiento
- Mantener libres las rejillas de ventilación frontal y posterior.
- Limpieza externa con brocha antiestática, con el equipo apagado.
- Respaldar la configuración antes de cualquier práctica destructiva.

## 8. Prácticas académicas relacionadas
- Seguridad perimetral y definición de zonas de confianza.
- Configuración de VPN IPsec site-to-site.
- Listas de control de acceso y NAT en el borde de la red.
- Comparación entre firewall de estado y ACL en router.
