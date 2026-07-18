package com.luis.pokeexamen.presentation.detail

import com.luis.pokeexamen.domain.model.PokemonDetail

sealed class PokemonDetailUiState {
    data object Loading : PokemonDetailUiState()
    data class Success(val detail: PokemonDetail) : PokemonDetailUiState()
    data class Error(val message: String) : PokemonDetailUiState()
}
