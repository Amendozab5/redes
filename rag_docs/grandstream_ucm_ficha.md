# Grandstream UCM6108 — Central telefónica IP (IP PBX)

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `grandstream_ucm`
- **Fabricante / modelo:** Grandstream Networks — UCM6108, leído en la serigrafía frontal
- **Ubicación en el laboratorio:** rack de comunicaciones, sobre el switch Nexxt
- **Aspecto:** chasis negro de 1U con pantalla LCD a la derecha
- **Interfaz de gestión:** web (HTTPS), no por línea de comandos

## 2. Función
Central telefónica IP que gestiona toda la telefonía del laboratorio:

1. **Registro de extensiones SIP:** cada teléfono IP se registra contra la central.
2. **Enrutamiento de llamadas:** internas entre extensiones y externas hacia la red telefónica.
3. **Puente entre mundos:** conecta la red de datos (VoIP) con las líneas analógicas
   tradicionales mediante sus puertos FXO y FXS.
4. Servicios adicionales: buzón de voz, grabación, IVR y colas de llamada.

La designación **6108** indica la dotación de puertos: **8 FXO** y **2 FXS**.

## 3. Componentes principales
Leídos directamente del panel frontal:

- **8 puertos FXO** — se conectan a las **líneas telefónicas entrantes** del proveedor
- **2 puertos FXS**, rotulados *Phone 1* y *Phone 2* — se conectan a **teléfonos analógicos**
- Puertos Ethernet WAN y LAN
- Puerto **USB** y ranura **SD** para grabaciones y respaldos
- **Pantalla LCD** que muestra la dirección IP y el estado del sistema
- LED de estado: *Network*, *USB*, *Phone*, *ACT*, *SD*, y por línea (1 a 8)

> **Distinción clave que suele confundirse:** FXS *entrega* tono y alimenta un teléfono
> analógico; FXO *recibe* la línea del proveedor. Conectarlos al revés no funciona y puede
> dañar el equipo.

## 4. Procedimiento básico de uso
1. Conectar el puerto **LAN** a la red del laboratorio y encender el equipo.
2. Leer la dirección IP en la **pantalla LCD frontal**.
3. Abrir esa dirección en un navegador desde un equipo de la misma red y acceder con las
   credenciales de administrador.
4. Crear extensiones SIP en **Extension / Trunk → Extensions**: número, contraseña y permisos.
5. Configurar troncales y el plan de marcado (*Outbound / Inbound Routes*).
6. Registrar los teléfonos IP contra la dirección de la central.
7. Verificar el estado en **System Status → PBX Status**.
8. **Respaldar la configuración** desde *Maintenance → Backup* antes de cada práctica.

## 5. Elementos de protección personal
- Pulsera antiestática al manipular el hardware.
- **No manipular los puertos FXS con líneas conectadas:** un puerto FXS genera voltaje de
  timbrado que puede producir una descarga desagradable.
- Calzado cerrado durante el trabajo en el rack.

## 6. Riesgos asociados
- **Voltaje de timbrado en los puertos FXS** (decenas de voltios en corriente alterna durante
  el repique). Desconectar antes de manipular.
- Conectar una línea del proveedor a un puerto FXS por error: puede dañar la etapa de salida.
- **Un plan de marcado mal configurado deja al laboratorio sin telefonía**, incluidas las
  llamadas de emergencia.
- Descarga electrostática sobre los conectores RJ-11.

## 7. Mantenimiento
- Respaldar la configuración periódicamente en la tarjeta SD o por descarga.
- Mantener el firmware actualizado desde la interfaz web.
- Verificar que la ventilación del chasis no esté obstruida por el cableado del rack.

## 8. Prácticas académicas relacionadas
- Telefonía IP y protocolo SIP.
- Creación de extensiones y registro de terminales.
- Troncales, plan de marcado y rutas de salida.
- Interconexión entre telefonía IP y telefonía analógica (FXO/FXS).
