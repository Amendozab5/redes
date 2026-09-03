package com.example.proyecto.data

data class Equipo(
    val clase: String,
    val nombre: String,
    val categoria: String,
    val funcion: String,
    val componentes: List<String>,
    val procedimientoBasico: String,
    val epp: List<String>,
    val riesgos: List<String>,
    val practicasRelacionadas: String,
    val vectorStoreId: String = ""
)
