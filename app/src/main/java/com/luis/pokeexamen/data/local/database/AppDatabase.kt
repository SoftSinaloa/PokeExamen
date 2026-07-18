package com.luis.pokeexamen.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.luis.pokeexamen.data.local.converter.Converters
import com.luis.pokeexamen.data.local.dao.PokemonDao
import com.luis.pokeexamen.data.local.entity.FavoriteEntity
import com.luis.pokeexamen.data.local.entity.PokemonDetailEntity
import com.luis.pokeexamen.data.local.entity.PokemonEntity

@Database(
    entities = [PokemonEntity::class, PokemonDetailEntity::class, FavoriteEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
