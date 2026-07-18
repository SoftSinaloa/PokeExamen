package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.repository.PokemonRepository

class GetFavoriteIdsUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(): Set<Int> = repository.getFavoriteIds()
}
