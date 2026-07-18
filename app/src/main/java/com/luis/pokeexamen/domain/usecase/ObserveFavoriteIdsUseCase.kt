package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteIdsUseCase(private val repository: PokemonRepository) {
    operator fun invoke(): Flow<Set<Int>> = repository.observeFavoriteIds()
}
