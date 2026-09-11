# Teléfono IP de escritorio (Yealink) — Terminal de telefonía IP

> **Documento de referencia técnica — Laboratorio de Redes y Telecomunicaciones, UTEQ**
> Elaborado por el grupo de trabajo a partir de documentación pública del fabricante y de la
> inspección directa del equipo instalado en el laboratorio. **No sustituye al manual oficial
> del fabricante ni a la guía de práctica emitida por el docente.** Los valores marcados como
> *(verificar)* deben contrastarse contra el datasheet del modelo exacto o la placa del equipo.


## 1. Identificación
- **Clase en el detector:** `telefono_yealink`
- **Fabricante:** Yealink — *(verificar el modelo exacto en la etiqueta inferior del aparato)*
- **Ubicación en el laboratorio:** escritorios de trabajo, junto a los equipos de cómputo
- **Aspecto:** teléfono de escritorio gris con pantalla LCD, teclado numérico y auricular con
  cable rizado

> **Nota sobre esta clase.** Es la clase con **menos ejemplos** en el conjunto de datos
> (41 instancias). Su métrica individual es poco robusta y debe reforzarse en la próxima
> campaña de captura de fotografías.

## 2. Función
Terminal de telefonía IP. Se registra como **extensión SIP** contra la central Grandstream
UCM6108 y es el aparato con el que se realizan y reciben las llamadas en las prácticas de
telefonía IP.

A diferencia de un teléfono analógico, no se conecta a una línea telefónica sino a la **red de
datos**: la voz viaja digitalizada en paquetes IP.

## 3. Componentes principales
- Pantalla LCD que muestra la extensión, el estado del registro y la fecha
- Teclado numérico y teclas de función y de línea
- Teclas de navegación y teclas programables (softkeys)
- **Puerto Ethernet LAN** para conectar a la red
- **Puerto PC** para conectar en cascada un computador *(según modelo — verificar)*
- Auricular con cable rizado
- Alimentación por **PoE** (a través del cable de red) o por adaptador externo

## 4. Procedimiento básico de uso
1. Conectar el puerto **LAN** del teléfono a un puerto de switch de la red del laboratorio.
2. Encender: si el switch entrega PoE, el teléfono arranca solo; si no, conectar el adaptador.
3. Esperar a que obtenga dirección IP por DHCP. La IP se consulta en
   **Menú → Estado** (*Status*).
4. Configurar la cuenta SIP, por cualquiera de estas dos vías:
   - **Desde el teléfono:** Menú → Ajustes → Avanzado → Cuentas, e ingresar usuario,
     contraseña y dirección del servidor (la IP de la central UCM6108).
   - **Desde la interfaz web:** abrir la IP del teléfono en un navegador e ir a *Account*.
5. Verificar que el indicador de registro muestre la extensión activa.
6. Probar una llamada interna marcando la extensión de otro teléfono.

## 5. Elementos de protección personal
- No se requiere EPP especial durante la operación normal.
- Precaución al manipular el cableado bajo el escritorio.

## 6. Riesgos asociados
- **Caída del equipo** al manipularlo sobre el escritorio: sujetarlo por la base.
- Credenciales SIP incorrectas impiden el registro de la extensión: es el fallo más común y se
  reconoce porque la pantalla no muestra la extensión.
- Desconectar el cable de red de un teléfono alimentado por PoE lo apaga inmediatamente.
- Higiene: el auricular es de uso compartido. Limpiar con paño humedecido entre usuarios.

## 7. Mantenimiento
- Limpiar la pantalla y el teclado con paño seco o ligeramente humedecido, nunca con solventes.
- Verificar el estado del cable rizado del auricular: es la pieza que más se daña.
- Respaldar la configuración desde la interfaz web antes de restablecer de fábrica.

## 8. Prácticas académicas relacionadas
- Registro de extensiones SIP contra una central IP.
- Pruebas de llamada interna y externa.
- Calidad de voz: latencia, jitter y pérdida de paquetes.
- Alimentación por Ethernet (PoE) y su verificación en el switch.
