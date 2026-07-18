package com.luis.pokeexamen.presentation.list

import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.domain.model.PokemonDetail
import com.luis.pokeexamen.domain.repository.PokemonRepository
import com.luis.pokeexamen.domain.usecase.GetPokemonListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakePokemon = listOf(
        Pokemon(1, "bulbasaur", "url"),
        Pokemon(4, "charmander", "url"),
        Pokemon(7, "squirtle", "url")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun successLoadPokemonListTest() = runTest {
        val viewModel = PokemonListViewModel(GetPokemonListUseCase(FakeRepo(Result.success(fakePokemon))))

        advanceUntilIdle()

        Assert.assertNotNull("Pokemon list", viewModel.state.value.allPokemon)
        Assert.assertEquals("Pokemon list size", 3, viewModel.state.value.allPokemon.size)
        Assert.assertFalse("Loading state", viewModel.state.value.isLoading)
        Assert.assertNull("Error message", viewModel.state.value.error)
    }

    @Test
    fun failureLoadPokemonListTest() = runTest {
        val viewModel = PokemonListViewModel(GetPokemonListUseCase(FakeRepo(Result.failure(Exception("Sin red")))))

        advanceUntilIdle()

        Assert.assertNotNull("Error message", viewModel.state.value.error)
        Assert.assertEquals("Error message", "Sin red", viewModel.state.value.error)
        Assert.assertFalse("Loading state", viewModel.state.value.isLoading)
    }

    @Test
    fun successSearchFilterTest() = runTest {
        val viewModel = PokemonListViewModel(GetPokemonListUseCase(FakeRepo(Result.success(fakePokemon))))

        advanceUntilIdle()
        viewModel.onSearchQuery("char")

        Assert.assertNotNull("Filtered list", viewModel.state.value.displayPokemon)
        Assert.assertEquals("Filtered list size", 1, viewModel.state.value.displayPokemon.size)
        Assert.assertEquals("Filtered pokemon name", "charmander", viewModel.state.value.displayPokemon.first().name)
    }

    @Test
    fun successClearSearchTest() = runTest {
        val viewModel = PokemonListViewModel(GetPokemonListUseCase(FakeRepo(Result.success(fakePokemon))))

        advanceUntilIdle()
        viewModel.onSearchQuery("char")
        viewModel.onSearchQuery("")

        Assert.assertEquals("Full list after clear", fakePokemon, viewModel.state.value.displayPokemon)
    }
}

private class FakeRepo(
    private val result: Result<List<Pokemon>>
) : PokemonRepository {
    override suspend fun getPokemonList(offset: Int) = result
    override suspend fun getPokemonDetail(name: String) = Result.failure<PokemonDetail>(Exception())
}
