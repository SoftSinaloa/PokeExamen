package com.luis.pokeexamen.domain.repository

import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonList(offset: Int = 0): Result<List<Pokemon>>
    suspend fun getPokemonDetail(name: String): Result<PokemonDetail>
    suspend fun getFavoriteIds(): Set<Int>
    fun observeFavoriteIds(): Flow<Set<Int>>
    suspend fun toggleFavorite(pokemonId: Int)
}
