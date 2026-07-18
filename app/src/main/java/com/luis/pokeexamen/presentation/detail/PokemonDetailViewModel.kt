package com.luis.pokeexamen.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.pokeexamen.domain.usecase.GetFavoriteIdsUseCase
import com.luis.pokeexamen.domain.usecase.GetPokemonDetailUseCase
import com.luis.pokeexamen.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val getPokemonDetail: GetPokemonDetailUseCase,
    private val getFavoriteIds: GetFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadDetail(name: String) {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            getPokemonDetail(name)
                .onSuccess { detail ->
                    _uiState.value = PokemonDetailUiState.Success(detail)
                    _isFavorite.value = detail.id in getFavoriteIds()
                }
                .onFailure {
                    _uiState.value = PokemonDetailUiState.Error(it.message ?: "Error desconocido")
                }
        }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(pokemonId)
            _isFavorite.value = !_isFavorite.value
        }
    }
}
