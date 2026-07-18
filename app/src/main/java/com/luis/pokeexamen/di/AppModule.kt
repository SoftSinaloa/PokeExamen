package com.luis.pokeexamen.di

import androidx.room.Room
import com.luis.pokeexamen.data.local.database.AppDatabase
import com.luis.pokeexamen.data.remote.api.PokeApiService
import com.luis.pokeexamen.data.repository.PokemonRepositoryImpl
import com.luis.pokeexamen.domain.repository.PokemonRepository
import com.luis.pokeexamen.domain.usecase.GetFavoriteIdsUseCase
import com.luis.pokeexamen.domain.usecase.GetPokemonDetailUseCase
import com.luis.pokeexamen.domain.usecase.GetPokemonListUseCase
import com.luis.pokeexamen.domain.usecase.ObserveFavoriteIdsUseCase
import com.luis.pokeexamen.domain.usecase.ToggleFavoriteUseCase
import com.luis.pokeexamen.presentation.detail.PokemonDetailViewModel
import com.luis.pokeexamen.presentation.list.PokemonListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "pokeexamen.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().pokemonDao() }

    single<PokemonRepository> { PokemonRepositoryImpl(get(), get()) }

    factory { GetPokemonListUseCase(get()) }
    factory { GetPokemonDetailUseCase(get()) }
    factory { GetFavoriteIdsUseCase(get()) }
    factory { ObserveFavoriteIdsUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }

    viewModel { PokemonListViewModel(get(), get(), get()) }
    viewModel { PokemonDetailViewModel(get(), get(), get()) }
}
