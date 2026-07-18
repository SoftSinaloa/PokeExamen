package com.luis.pokeexamen.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luis.pokeexamen.data.local.entity.FavoriteEntity
import com.luis.pokeexamen.data.local.entity.PokemonDetailEntity
import com.luis.pokeexamen.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon")
    suspend fun getAll(): List<PokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon_detail WHERE name = :name LIMIT 1")
    suspend fun getDetail(name: String): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: PokemonDetailEntity)

    @Query("SELECT pokemonId FROM favorites")
    suspend fun getFavoriteIds(): List<Int>

    @Query("SELECT pokemonId FROM favorites")
    fun observeFavoriteIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(fav: FavoriteEntity)
}
