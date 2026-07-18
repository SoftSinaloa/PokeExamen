package com.luis.pokeexamen.presentation.list

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luis.pokeexamen.domain.model.Pokemon
import com.luis.pokeexamen.presentation.common.PokeLoader
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf("home") }
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler { showExitDialog = true }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "¿Salir de la Pokédex?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCC0000)
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres cerrar la Pokédex de Luis Madrid?",
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { activity?.finish() },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFCC0000)
                    )
                ) {
                    Text("Salir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Quedarme", color = Color(0xFFCC0000))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color(0xFFCC0000), CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Pokédex", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFCC0000),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            PokedexBottomNav(
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                onHomeReset = {
                    selectedTab = "home"
                    viewModel.onTypeFilter(null)
                    viewModel.onSearchQuery("")
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PokeLoader(size = 140.dp)
                    }
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error!!,
                        onRetry = viewModel::loadPokemon,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    if (selectedTab == "discover") {
                        DiscoverTab()
                    } else if (selectedTab == "more") {
                        StatsTab(
                            allPokemon = state.allPokemon,
                            favoriteIds = state.favoriteIds
                        )
                    } else if (selectedTab == "collection") {
                        CollectionTab(
                            favorites = state.allPokemon.filter { it.id in state.favoriteIds },
                            favoriteIds = state.favoriteIds,
                            onPokemonClick = onPokemonClick,
                            onFavoriteClick = viewModel::toggleFavorite
                        )
                    } else {
                        Column {
                            PokedexSearchBar(
                                query = state.searchQuery,
                                onQueryChange = viewModel::onSearchQuery,
                            )
                            if (state.availableTypes.isNotEmpty()) {
                                TypeFilterRow(
                                    types = state.availableTypes,
                                    selectedType = state.selectedType,
                                    onTypeSelect = viewModel::onTypeFilter
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            PokemonGrid(
                                pokemon = state.displayPokemon,
                                favoriteIds = state.favoriteIds,
                                isLoadingMore = state.isLoadingMore,
                                hasMore = state.hasMore,
                                searchQuery = state.searchQuery,
                                selectedType = state.selectedType,
                                onPokemonClick = onPokemonClick,
                                onLoadMore = viewModel::loadMore,
                                onFavoriteClick = viewModel::toggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokedexSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(fontSize = 15.sp, color = Color.DarkGray),
                cursorBrush = SolidColor(Color(0xFFCC0000)),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            "Buscar Pokémon o tipo",
                            style = TextStyle(fontSize = 15.sp, color = Color.LightGray)
                        )
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
private fun TypeFilterRow(
    types: List<String>,
    selectedType: String?,
    onTypeSelect: (String?) -> Unit
) {
    val rowState = androidx.compose.foundation.lazy.rememberLazyListState()
    LaunchedEffect(selectedType) {
        if (selectedType == null) rowState.scrollToItem(0)
    }
    LazyRow(
        state = rowState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types) { type ->
            val selected = type == selectedType
            val color = typeColor(type)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (selected) color else Color.White,
                shadowElevation = if (selected) 0.dp else 1.dp,
                modifier = Modifier.clickable { onTypeSelect(if (selected) null else type) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = typeEmoji(type),
                        fontSize = 13.sp
                    )
                    Text(
                        text = typeNameEs(type),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Color.White else Color.DarkGray,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

private data class Region(val name: String, val generation: String, val color1: Color, val color2: Color, val emoji: String)

private val regions = listOf(
    Region("Kanto",   "Gen I",   Color(0xFFE53935), Color(0xFFFF7043), "🔥"),
    Region("Johto",   "Gen II",  Color(0xFF43A047), Color(0xFF66BB6A), "🌿"),
    Region("Hoenn",   "Gen III", Color(0xFF1E88E5), Color(0xFF42A5F5), "🌊"),
    Region("Sinnoh",  "Gen IV",  Color(0xFF8E24AA), Color(0xFFAB47BC), "❄️"),
    Region("Teselia", "Gen V",   Color(0xFF00ACC1), Color(0xFF26C6DA), "⚡"),
    Region("Kalos",   "Gen VI",  Color(0xFFF06292), Color(0xFFF48FB1), "✨"),
    Region("Alola",   "Gen VII", Color(0xFFFFB300), Color(0xFFFFCA28), "🌺"),
    Region("Galar",   "Gen VIII",Color(0xFF5C6BC0), Color(0xFF7986CB), "⚔️"),
    Region("Paldea",  "Gen IX",  Color(0xFF6D4C41), Color(0xFF8D6E63), "🏔️")
)

@Composable
private fun DiscoverTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFCC0000), Color(0xFFFF5252))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Regiones del mundo Pokémon",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Explora cada región y sus Pokémon",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(regions) { region ->
                RegionCard(region = region)
            }
        }
    }
}

@Composable
private fun RegionCard(region: Region) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(region.color1, region.color2))
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = region.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = region.generation,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Text(
                text = region.emoji,
                fontSize = 30.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun CollectionTab(
    favorites: List<Pokemon>,
    favoriteIds: Set<Int>,
    onPokemonClick: (String) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Aún no tienes favoritos",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                Text(
                    text = "Dale corazón a los Pokémon que te gusten",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(favorites, key = { it.id }) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    isFavorite = pokemon.id in favoriteIds,
                    onClick = { onPokemonClick(pokemon.name) },
                    onFavoriteClick = { onFavoriteClick(pokemon.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun PokemonGrid(
    pokemon: List<Pokemon>,
    favoriteIds: Set<Int>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    searchQuery: String,
    selectedType: String?,
    onPokemonClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(selectedType, searchQuery) {
        gridState.scrollToItem(0)
    }
    val reachedEnd by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 6
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd) onLoadMore()
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(pokemon, key = { it.id }) { p ->
            PokemonCard(
                pokemon = p,
                isFavorite = p.id in favoriteIds,
                onClick = { onPokemonClick(p.name) },
                onFavoriteClick = { onFavoriteClick(p.id) },
                modifier = Modifier.animateItem()
            )
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PokeLoader(size = 56.dp)
                }
            }
        }

        if (!hasMore && pokemon.isNotEmpty() && searchQuery.isBlank()) {
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Ya los tienes todos",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: Pokemon,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryType = pokemon.types.firstOrNull() ?: "normal"
    val typeCol = typeColor(primaryType)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Pokémon ${pokemon.name}" },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    fontSize = 9.sp
                )
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color(0xFFE53935) else Color.LightGray,
                    modifier = Modifier
                        .size(15.dp)
                        .clickable(onClick = onFavoriteClick)
                )
            }

            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(72.dp)
                    .padding(vertical = 4.dp)
            )

            Text(
                text = pokemon.name.capitalize(Locale.current),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = typeCol.copy(alpha = 0.15f)
            ) {
                Text(
                    text = typeNameEs(primaryType),
                    style = MaterialTheme.typography.labelSmall,
                    color = typeCol,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun PokedexBottomNav(
    selectedTab: String,
    onTabSelect: (String) -> Unit,
    onHomeReset: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                selected = selectedTab == "home",
                onClick = { onTabSelect("home") }
            )
            BottomNavItem(
                icon = if (selectedTab == "collection") Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = "Colección",
                selected = selectedTab == "collection",
                onClick = { onTabSelect("collection") }
            )

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFCC0000), CircleShape)
                    .clickable { onHomeReset() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFCC0000), CircleShape)
                    )
                }
            }

            BottomNavItem(
                icon = Icons.Default.Explore,
                label = "Descubrir",
                selected = selectedTab == "discover",
                onClick = { onTabSelect("discover") }
            )
            BottomNavItem(
                icon = Icons.Default.GridView,
                label = "Mas",
                selected = selectedTab == "more",
                onClick = { onTabSelect("more") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(4.dp).clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFFCC0000) else Color.LightGray,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (selected) Color(0xFFCC0000) else Color.LightGray,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Algo salió mal", style = MaterialTheme.typography.titleMedium)
        Text(text = message, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

private fun typeColor(type: String): Color = when (type) {
    "fire" -> Color(0xFFFF6B35)
    "water" -> Color(0xFF4A90D9)
    "grass" -> Color(0xFF5DB85D)
    "electric" -> Color(0xFFF5C518)
    "psychic" -> Color(0xFFFF5599)
    "ice" -> Color(0xFF96D9D6)
    "dragon" -> Color(0xFF7038F8)
    "dark" -> Color(0xFF705848)
    "fairy" -> Color(0xFFEE99AC)
    "fighting" -> Color(0xFFC03028)
    "poison" -> Color(0xFFA040A0)
    "ground" -> Color(0xFFE0C068)
    "flying" -> Color(0xFF89AAE3)
    "bug" -> Color(0xFF8CB820)
    "rock" -> Color(0xFFB8A038)
    "ghost" -> Color(0xFF705898)
    "steel" -> Color(0xFF9EB8D0)
    "normal" -> Color(0xFFA8A878)
    else -> Color(0xFF888888)
}

private fun typeNameEs(type: String): String = when (type) {
    "fire" -> "Fuego"
    "water" -> "Agua"
    "grass" -> "Planta"
    "electric" -> "Eléctrico"
    "psychic" -> "Psíquico"
    "ice" -> "Hielo"
    "dragon" -> "Dragón"
    "dark" -> "Oscuro"
    "fairy" -> "Hada"
    "fighting" -> "Lucha"
    "poison" -> "Veneno"
    "ground" -> "Tierra"
    "flying" -> "Volador"
    "bug" -> "Bicho"
    "rock" -> "Roca"
    "ghost" -> "Fantasma"
    "steel" -> "Acero"
    "normal" -> "Normal"
    else -> type.capitalize(Locale.current)
}

private fun typeEmoji(type: String): String = when (type) {
    "fire" -> "🔥"
    "water" -> "💧"
    "grass" -> "🌿"
    "electric" -> "⚡"
    "psychic" -> "🔮"
    "ice" -> "❄️"
    "dragon" -> "🐉"
    "dark" -> "🌑"
    "fairy" -> "✨"
    "fighting" -> "👊"
    "poison" -> "☠️"
    "ground" -> "🌍"
    "flying" -> "🌬️"
    "bug" -> "🐛"
    "rock" -> "🪨"
    "ghost" -> "👻"
    "steel" -> "⚙️"
    "normal" -> "⭐"
    else -> "●"
}

@Composable
private fun StatsTab(allPokemon: List<Pokemon>, favoriteIds: Set<Int>) {
    val scrollState = rememberScrollState()

    val seen = allPokemon.size
    val favCount = favoriteIds.size
    val topType = allPokemon.flatMap { it.types }
        .groupingBy { it }.eachCount()
        .maxByOrNull { it.value }?.key ?: "normal"
    val progress = (seen / 151f).coerceIn(0f, 1f)
    val typeDist = allPokemon.flatMap { it.types }
        .groupingBy { it }.eachCount()
        .entries.sortedByDescending { it.value }.take(8)
    val maxCount = typeDist.maxOfOrNull { it.value } ?: 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1021))
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color(0xFFCC0000), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ENTRENADOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 3.sp
                )
                Text(
                    text = "Luis Madrid",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("👁️", "$seen", "Pokémon vistos", Color(0xFF1E88E5), Modifier.weight(1f))
            StatCard("❤️", "$favCount", "Favoritos", Color(0xFFE53935), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(typeEmoji(topType), typeNameEs(topType), "Tipo más visto", typeColor(topType), Modifier.weight(1f))
            StatCard("📊", "${(progress * 100).toInt()}%", "Progreso Pokédex", Color(0xFF43A047), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 Progreso de Pokédex",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$seen / 151",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFF43A047),
                    trackColor = Color(0xFFE8F5E9)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (progress >= 1f) "¡Pokédex completa! 🎉" else "Sigue explorando para completarla",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (typeDist.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 Distribución por tipo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    typeDist.forEach { (type, count) ->
                        TypeBar(type = type, count = count, maxCount = maxCount)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TypeBar(type: String, count: Int, maxCount: Int) {
    val color = typeColor(type)
    val fraction = count.toFloat() / maxCount.toFloat()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = typeEmoji(type), fontSize = 14.sp, modifier = Modifier.width(22.dp))
        Text(
            text = typeNameEs(type),
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            modifier = Modifier.width(64.dp)
        )
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
    }
}
