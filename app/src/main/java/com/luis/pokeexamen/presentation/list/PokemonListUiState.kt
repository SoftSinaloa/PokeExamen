package com.luis.pokeexamen.presentation.list

import com.luis.pokeexamen.domain.model.Pokemon

data class PokemonListState(
    val allPokemon: List<Pokemon> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
    val searchQuery: String = "",
    val selectedType: String? = null,
    val favoriteIds: Set<Int> = emptySet()
) {
    val displayPokemon: List<Pokemon>
        get() {
            var result = if (searchQuery.isBlank()) allPokemon
                         else allPokemon.filter { it.name.contains(searchQuery, ignoreCase = true) }
            if (selectedType != null) {
                result = result.filter { selectedType in it.types }
            }
            return result
        }

    val availableTypes: List<String>
        get() = allPokemon.flatMap { it.types }.distinct().sorted()
}
