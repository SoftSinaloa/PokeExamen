package com.luis.pokeexamen.data.remote.dto

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
)

fun PokemonResult.extractId(): Int = url.trimEnd('/').split('/').last().toInt()
