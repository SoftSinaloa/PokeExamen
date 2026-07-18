package com.luis.pokeexamen.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val types: List<String>,
    val abilities: List<String>,
    val stats: Map<String, Int>,
    val imageUrl: String
)
