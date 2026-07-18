package com.luis.pokeexamen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerializedName("base_experience") val baseExperience: Int?,
    val types: List<TypeSlot>,
    val abilities: List<AbilitySlot>,
    val stats: List<StatSlot>,
    val sprites: Sprites
)

data class TypeSlot(val type: TypeInfo)
data class TypeInfo(val name: String)

data class AbilitySlot(
    val ability: AbilityInfo,
    @SerializedName("is_hidden") val isHidden: Boolean
)
data class AbilityInfo(val name: String)

data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: StatInfo
)
data class StatInfo(val name: String)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?
)
