package com.example.proyecto.data

/**
 * Ficha tecnica de referencia general para cada clase detectada por el modelo del
 * Laboratorio de Redes y Telecomunicaciones. El contenido es informativo mientras
 * se suben las guias oficiales del laboratorio a OpenAI: el chat (vectorStoreId)
 * es el que debe responder con precision citando la fuente exacta.
 */
object EquipoRepository {

    // TODO: reemplaza cada vectorStoreId vacio por el ID real del Vector Store de
    // OpenAI (vs_...) creado con los manuales/guias de ese equipo.
    private val equipos = listOf(
        Equipo(
            clase = "asa_5520",
            nombre = "Cisco ASA 5520",
            categoria = "Firewall / appliance de seguridad adaptable",
            funcion = "Dispositivo de seguridad perimetral que combina firewall, VPN y prevención de " +
                "intrusiones para proteger la red del laboratorio frente a tráfico no autorizado.",
            componentes = listOf(
                "Puertos Ethernet (interfaces de red)",
                "Puerto de consola (gestión por CLI)",
                "Módulo de fuente de poder",
                "Indicadores LED de estado"
            ),
            procedimientoBasico = "Se conecta mediante cable de consola o SSH para configurar reglas de " +
                "firewall, políticas de VPN y monitoreo de tráfico desde la CLI de Cisco ASA.",
            epp = listOf(
                "Pulsera antiestática al manipular el hardware",
                "No se requiere EPP especial durante la operación normal"
            ),
            riesgos = listOf(
                "Descarga electrostática al manipular tarjetas internas",
                "Una configuración incorrecta puede dejar la red sin protección"
            ),
            practicasRelacionadas = "Seguridad perimetral, configuración de VPN site-to-site y políticas " +
                "de firewall."
        ),
        Equipo(
            clase = "catalyst_2960",
            nombre = "Cisco Catalyst 2960",
            categoria = "Switch de acceso — Capa 2",
            funcion = "Switch Ethernet de capa 2 usado para interconectar dispositivos finales dentro de " +
                "una LAN y practicar configuración de VLANs, puertos troncales y seguridad de puertos.",
            componentes = listOf(
                "Puertos Fast/Gigabit Ethernet",
                "Puerto de consola RJ-45/USB",
                "Puerto uplink (SFP en algunos modelos)",
                "Indicadores LED por puerto"
            ),
            procedimientoBasico = "Conectar por cable de consola, acceder a la CLI de IOS y configurar " +
                "VLANs, trunking (802.1Q) y seguridad de puertos según la práctica asignada.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Bucles de red (loops) si no se configura Spanning Tree"
            ),
            practicasRelacionadas = "Configuración de VLANs, trunking, Spanning Tree Protocol y seguridad " +
                "de puertos."
        ),
        Equipo(
            clase = "catalyst_3750g",
            nombre = "Cisco Catalyst 3750G",
            categoria = "Switch apilable — Capa 3, Gigabit",
            funcion = "Switch Gigabit con capacidades de enrutamiento de Capa 3, usado en prácticas de " +
                "apilamiento (StackWise), enrutamiento inter-VLAN y redundancia de red.",
            componentes = listOf(
                "Puertos Gigabit Ethernet",
                "Puertos de apilamiento StackWise",
                "Puerto de consola",
                "Fuente de poder redundante (según modelo)"
            ),
            procedimientoBasico = "Configuración vía CLI de IOS para enrutamiento inter-VLAN, protocolos " +
                "de enrutamiento y apilamiento de varios switches como una sola unidad lógica.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Peso del equipo al montarlo/desmontarlo del rack"
            ),
            practicasRelacionadas = "Enrutamiento inter-VLAN, EtherChannel y apilamiento StackWise."
        ),
        Equipo(
            clase = "catalyst_8200",
            nombre = "Cisco Catalyst 8200 Series Edge Platform (C8200L-1N-4T)",
            categoria = "Router de borde / SD-WAN",
            funcion = "Router empresarial de nueva generación para conectividad WAN, SD-WAN y servicios " +
                "de borde entre la red del laboratorio y redes externas. En el rack FYTEC hay dos " +
                "unidades, alternadas con los Catalyst 9200.",
            componentes = listOf(
                "Puertos WAN/LAN",
                "Módulos de servicio intercambiables (NIM)",
                "Puerto de consola/gestión",
                "Indicadores LED de estado"
            ),
            procedimientoBasico = "Configuración de interfaces WAN, protocolos de enrutamiento y " +
                "políticas SD-WAN desde la CLI de IOS-XE.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Errores de configuración de WAN pueden interrumpir la conectividad"
            ),
            practicasRelacionadas = "Enrutamiento WAN, SD-WAN y servicios de borde de red."
        ),
        Equipo(
            clase = "catalyst_9200",
            nombre = "Cisco Catalyst 9200",
            categoria = "Switch de acceso moderno — Capa 2/3",
            funcion = "Switch de la familia Catalyst 9000 usado en prácticas de redes modernas con " +
                "soporte para automatización, seguridad avanzada y alto rendimiento.",
            componentes = listOf(
                "Puertos Gigabit/Multigigabit Ethernet",
                "Puerto de consola",
                "Puertos uplink (SFP+)",
                "Indicadores LED por puerto"
            ),
            procedimientoBasico = "Configuración vía CLI de IOS-XE para VLANs, seguridad de puertos, QoS " +
                "y automatización básica de red.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf("Descarga electrostática"),
            practicasRelacionadas = "VLANs, QoS, seguridad de puertos y fundamentos de automatización de red."
        ),
        Equipo(
            clase = "cisco_2800",
            nombre = "Cisco 2800 Series Integrated Services Router",
            categoria = "Router de servicios integrados",
            funcion = "Router usado en prácticas de enrutamiento entre redes, NAT, listas de control de " +
                "acceso (ACL) y protocolos de enrutamiento dinámico.",
            componentes = listOf(
                "Puertos Ethernet WAN/LAN",
                "Puerto de consola",
                "Slots para módulos WIC/HWIC",
                "Fuente de poder"
            ),
            procedimientoBasico = "Configuración vía CLI de IOS para direccionamiento IP, NAT, ACLs y " +
                "protocolos de enrutamiento como RIP, OSPF o EIGRP.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Una ACL mal configurada puede bloquear tráfico legítimo"
            ),
            practicasRelacionadas = "Enrutamiento dinámico, NAT y listas de control de acceso."
        ),
        Equipo(
            clase = "grandstream_ucm",
            nombre = "Grandstream UCM6108",
            categoria = "Central telefónica IP (IP PBX)",
            funcion = "Central telefónica IP que gestiona la telefonía del laboratorio: registra las " +
                "extensiones SIP, enruta llamadas internas y externas, y conecta la red de datos con " +
                "las líneas analógicas tradicionales.",
            componentes = listOf(
                "8 puertos FXO (líneas telefónicas entrantes)",
                "2 puertos FXS (teléfonos analógicos)",
                "Puertos Ethernet WAN/LAN",
                "Puerto USB y ranura SD para grabaciones y respaldos",
                "Pantalla LCD de estado en el panel frontal"
            ),
            procedimientoBasico = "Se configura desde su interfaz web: creación de extensiones SIP, " +
                "troncales, planes de marcado y buzones de voz. Los teléfonos IP se registran contra " +
                "su dirección para poder llamar.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Voltaje de timbrado en los puertos FXS: no manipular con líneas conectadas",
                "Una configuración incorrecta del plan de marcado puede dejar el laboratorio sin telefonía"
            ),
            practicasRelacionadas = "Telefonía IP, protocolo SIP, troncales y plan de marcado."
        ),
        Equipo(
            clase = "nexxt_switch",
            nombre = "Nexxt Solutions — Switch de 24 puertos",
            categoria = "Switch no administrable — Capa 2",
            funcion = "Switch de acceso de 24 puertos usado para ampliar la conectividad del rack. Al no " +
                "ser administrable, sirve de contraste práctico frente a los Catalyst: conmuta tramas " +
                "pero no permite VLANs ni configuración por CLI.",
            componentes = listOf(
                "24 puertos Fast/Gigabit Ethernet",
                "Indicadores LED de enlace y actividad por puerto",
                "Fuente de poder integrada"
            ),
            procedimientoBasico = "Funciona al conectarlo (plug and play), sin configuración. Se usa para " +
                "distribuir conexiones dentro del rack y para comparar su comportamiento con el de un " +
                "switch administrable.",
            epp = listOf("Pulsera antiestática al manipular el hardware"),
            riesgos = listOf(
                "Descarga electrostática",
                "Bucles de red: al no tener Spanning Tree configurable, un cable mal conectado puede " +
                    "saturar el segmento"
            ),
            practicasRelacionadas = "Conmutación básica, dominios de colisión y difusión, y diferencias " +
                "entre switch administrable y no administrable."
        ),
        Equipo(
            clase = "telefono_yealink",
            nombre = "Teléfono IP de escritorio (Yealink)",
            categoria = "Terminal de telefonía IP",
            funcion = "Teléfono de escritorio que se registra como extensión SIP contra la central " +
                "Grandstream UCM6108. Es el equipo terminal con el que se prueban las llamadas en las " +
                "prácticas de telefonía IP.",
            componentes = listOf(
                "Pantalla LCD",
                "Teclado numérico y teclas de función/línea",
                "Puerto Ethernet LAN (y PC passthrough en algunos modelos)",
                "Auricular con cable rizado",
                "Alimentación por PoE o adaptador"
            ),
            procedimientoBasico = "Se conecta a la red, obtiene IP y se configura la cuenta SIP " +
                "(usuario, contraseña y dirección del servidor) desde el menú del teléfono o desde su " +
                "interfaz web.",
            epp = listOf("No se requiere EPP especial durante la operación normal"),
            riesgos = listOf(
                "Caída del equipo al manipularlo sobre el escritorio",
                "Credenciales SIP mal configuradas impiden el registro de la extensión"
            ),
            practicasRelacionadas = "Registro de extensiones SIP, pruebas de llamada y calidad de voz."
        )
    )

    fun porClase(clase: String?): Equipo? = equipos.firstOrNull { it.clase.equals(clase, ignoreCase = true) }

    fun todos(): List<Equipo> = equipos
}
