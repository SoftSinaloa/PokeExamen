package com.luis.pokeexamen.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luis.pokeexamen.data.local.dao.PokemonDao
import com.luis.pokeexamen.data.local.entity.FavoriteEntity
import com.luis.pokeexamen.data.local.entity.PokemonDetailEntity
import com.luis.pokeexamen.data.local.entity.PokemonEntity
import com.luis.pokeexamen.data.remote.api.PokeApiService
import com.luis.pokeexamen.data.remote.dto.extractId
import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.domain.model.PokemonDetail
import com.luis.pokeexamen.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl(
    private val api: PokeApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    private val gson = Gson()

    override suspend fun getPokemonList(offset: Int): Result<List<Pokemon>> = runCatching {
        if (offset == 0) {
            val cached = dao.getAll()
            if (cached.isNotEmpty()) return@runCatching cached.map { it.toDomain(gson) }
        }

        val response = api.getPokemonList(limit = PAGE_SIZE, offset = offset)

        val entities = coroutineScope {
            response.results.map { result ->
                async {
                    val id = result.extractId()
                    val types = try {
                        api.getPokemonDetail(result.name).types.map { it.type.name }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    PokemonEntity(
                        id = id,
                        name = result.name,
                        imageUrl = spriteUrl(id),
                        types = gson.toJson(types)
                    )
                }
            }.awaitAll()
        }

        dao.insertAll(entities)
        entities.map { it.toDomain(gson) }
    }

    override suspend fun getPokemonDetail(name: String): Result<PokemonDetail> = runCatching {
        val cached = dao.getDetail(name)
        if (cached != null) return@runCatching cached.toDomain(gson)

        val response = api.getPokemonDetail(name)
        val entity = PokemonDetailEntity(
            id = response.id,
            name = response.name,
            height = response.height,
            weight = response.weight,
            baseExperience = response.baseExperience ?: 0,
            types = gson.toJson(response.types.map { it.type.name }),
            abilities = gson.toJson(response.abilities.map { it.ability.name }),
            stats = gson.toJson(response.stats.associate { it.stat.name to it.baseStat }),
            imageUrl = response.sprites.frontDefault ?: spriteUrl(response.id)
        )
        dao.insertDetail(entity)
        entity.toDomain(gson)
    }

    override suspend fun getFavoriteIds(): Set<Int> =
        dao.getFavoriteIds().toSet()

    override fun observeFavoriteIds(): Flow<Set<Int>> =
        dao.observeFavoriteIds().map { it.toSet() }

    override suspend fun toggleFavorite(pokemonId: Int) {
        val current = dao.getFavoriteIds()
        if (pokemonId in current) dao.deleteFavorite(FavoriteEntity(pokemonId))
        else dao.insertFavorite(FavoriteEntity(pokemonId))
    }

    private fun spriteUrl(id: Int) =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

    companion object {
        const val PAGE_SIZE = 20
    }
}

private fun PokemonEntity.toDomain(gson: Gson): Pokemon {
    val typeList: List<String> = gson.fromJson(types, Array<String>::class.java).toList()
    return Pokemon(id = id, name = name, imageUrl = imageUrl, types = typeList)
}

private fun PokemonDetailEntity.toDomain(gson: Gson): PokemonDetail {
    val typeList: List<String> = gson.fromJson(types, Array<String>::class.java).toList()
    val abilityList: List<String> = gson.fromJson(abilities, Array<String>::class.java).toList()
    val statsMap: Map<String, Int> = gson.fromJson(stats, object : TypeToken<Map<String, Int>>() {}.type)
    return PokemonDetail(
        id = id,
        name = name,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        types = typeList,
        abilities = abilityList,
        stats = statsMap,
        imageUrl = imageUrl
    )
}
