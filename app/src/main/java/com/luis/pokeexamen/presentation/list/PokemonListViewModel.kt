package com.luis.pokeexamen.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luis.pokeexamen.domain.usecase.ObserveFavoriteIdsUseCase
import com.luis.pokeexamen.domain.usecase.GetPokemonListUseCase
import com.luis.pokeexamen.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class PokemonListViewModel(
    private val getPokemonList: GetPokemonListUseCase,
    private val observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PokemonListState())
    val state: StateFlow<PokemonListState> = _state.asStateFlow()

    private var currentOffset = 0

    init {
        viewModelScope.launch {
            observeFavoriteIds().collect { ids ->
                _state.update { it.copy(favoriteIds = ids) }
            }
        }
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            currentOffset = 0
            getPokemonList(0)
                .onSuccess { list ->
                    currentOffset = list.size
                    _state.update {
                        it.copy(
                            allPokemon = list,
                            isLoading = false,
                            hasMore = list.size >= PAGE_SIZE
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
                }
        }
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMore || current.searchQuery.isNotBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            getPokemonList(currentOffset)
                .onSuccess { list ->
                    currentOffset += list.size
                    _state.update {
                        it.copy(
                            allPokemon = it.allPokemon + list,
                            isLoadingMore = false,
                            hasMore = list.size >= PAGE_SIZE
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingMore = false) }
                }
        }
    }

    fun onSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onTypeFilter(type: String?) {
        _state.update { it.copy(selectedType = type) }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(pokemonId)
        }
    }
}
