package com.luis.pokeexamen.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String): List<String> =
        gson.fromJson(value, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun toStringList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun fromStringIntMap(value: String): Map<String, Int> =
        gson.fromJson(value, object : TypeToken<Map<String, Int>>() {}.type)

    @TypeConverter
    fun toStringIntMap(map: Map<String, Int>): String = gson.toJson(map)
}
