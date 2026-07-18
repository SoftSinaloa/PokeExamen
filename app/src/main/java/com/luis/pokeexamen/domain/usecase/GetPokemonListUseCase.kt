package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.domain.repository.PokemonRepository

class GetPokemonListUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(offset: Int = 0): Result<List<Pokemon>> =
        repository.getPokemonList(offset)
}
