package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.repository.PokemonRepository

class ToggleFavoriteUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(pokemonId: Int) = repository.toggleFavorite(pokemonId)
}
