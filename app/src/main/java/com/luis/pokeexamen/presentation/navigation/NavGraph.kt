package com.luis.pokeexamen.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luis.pokeexamen.presentation.detail.PokemonDetailScreen
import com.luis.pokeexamen.presentation.list.PokemonListScreen
import com.luis.pokeexamen.presentation.splash.SplashScreen

private object Routes {
    const val SPLASH = "splash"
    const val LIST = "pokemon_list"
    const val DETAIL = "pokemon_detail/{name}"
    fun detail(name: String) = "pokemon_detail/$name"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Routes.LIST) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LIST) {
            PokemonListScreen(
                onPokemonClick = { name -> navController.navigate(Routes.detail(name)) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: return@composable
            PokemonDetailScreen(
                pokemonName = name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
