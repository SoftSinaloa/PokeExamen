package com.luis.pokeexamen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.luis.pokeexamen.presentation.navigation.NavGraph
import com.luis.pokeexamen.presentation.theme.PokeExamenTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeExamenTheme {
                NavGraph()
            }
        }
    }
}
