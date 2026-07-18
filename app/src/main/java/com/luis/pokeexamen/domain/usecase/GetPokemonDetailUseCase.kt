package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.model.PokemonDetail
import com.luis.pokeexamen.domain.repository.PokemonRepository

class GetPokemonDetailUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(name: String): Result<PokemonDetail> = repository.getPokemonDetail(name)
}
