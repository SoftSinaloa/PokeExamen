package com.luis.pokeexamen.domain.usecase

import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.domain.model.PokemonDetail
import com.luis.pokeexamen.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetPokemonListUseCaseTest {

    private val fakePokemon = listOf(
        Pokemon(1, "bulbasaur", "https://example.com/1.png"),
        Pokemon(4, "charmander", "https://example.com/4.png"),
        Pokemon(7, "squirtle", "https://example.com/7.png")
    )

    @Test
    fun successGetPokemonListTest() = runTest {
        val repo = FakeRepository(listResult = Result.success(fakePokemon))
        val useCase = GetPokemonListUseCase(repo)

        val result = useCase()

        Assert.assertNotNull("Pokemon list result", result.getOrNull())
        Assert.assertEquals("Pokemon list size", 3, result.getOrNull()?.size)
        Assert.assertEquals("First pokemon", "bulbasaur", result.getOrNull()?.first()?.name)
    }

    @Test
    fun failureGetPokemonListTest() = runTest {
        val repo = FakeRepository(listResult = Result.failure(Exception("Sin conexión")))
        val useCase = GetPokemonListUseCase(repo)

        val result = useCase()

        Assert.assertNotNull("Error message", result.exceptionOrNull()?.message)
        Assert.assertEquals("Error message", "Sin conexión", result.exceptionOrNull()?.message)
    }

    @Test
    fun successOffsetPaginationTest() = runTest {
        var capturedOffset = -1
        val repo = object : PokemonRepository {
            override suspend fun getPokemonList(offset: Int): Result<List<Pokemon>> {
                capturedOffset = offset
                return Result.success(emptyList())
            }
            override suspend fun getPokemonDetail(name: String) = Result.failure<PokemonDetail>(Exception())
            override suspend fun getFavoriteIds(): Set<Int> = emptySet()
            override fun observeFavoriteIds() = flowOf(emptySet<Int>())
            override suspend fun toggleFavorite(pokemonId: Int) = Unit
        }
        val useCase = GetPokemonListUseCase(repo)

        useCase(offset = 20)

        Assert.assertNotNull("Offset value", capturedOffset)
        Assert.assertEquals("Offset pagination", 20, capturedOffset)
    }
}

internal class FakeRepository(
    private val listResult: Result<List<Pokemon>> = Result.success(emptyList()),
    private val detailResult: Result<PokemonDetail> = Result.failure(Exception())
) : PokemonRepository {
    override suspend fun getPokemonList(offset: Int) = listResult
    override suspend fun getPokemonDetail(name: String) = detailResult
    override suspend fun getFavoriteIds(): Set<Int> = emptySet()
    override fun observeFavoriteIds() = flowOf(emptySet<Int>())
    override suspend fun toggleFavorite(pokemonId: Int) = Unit
}
