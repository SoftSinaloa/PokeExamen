package com.luis.pokeexamen.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luis.pokeexamen.domain.model.PokemonDetail
import com.luis.pokeexamen.presentation.common.PokeLoader
import org.koin.androidx.compose.koinViewModel

@Composable
fun PokemonDetailScreen(
    pokemonName: String,
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = koinViewModel()
) {
    LaunchedEffect(pokemonName) { viewModel.loadDetail(pokemonName) }

    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    when (val state = uiState) {
        is PokemonDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PokeLoader(size = 120.dp)
            }
        }
        is PokemonDetailUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Error al cargar", style = MaterialTheme.typography.titleMedium)
                    Text(state.message, textAlign = TextAlign.Center)
                    Button(onClick = { viewModel.loadDetail(pokemonName) }) { Text("Reintentar") }
                }
            }
        }
        is PokemonDetailUiState.Success -> {
            DetailContent(
                detail = state.detail,
                isFavorite = isFavorite,
                onBack = onBack,
                onToggleFavorite = { viewModel.toggleFavorite(state.detail.id) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    detail: PokemonDetail,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val primaryType = detail.types.firstOrNull() ?: "normal"
    val typeCol = typeColor(primaryType)
    val gradientColors = listOf(typeCol.copy(alpha = 0.75f), typeCol)
    val weaknesses = typeWeaknesses(detail.types)
    val resistances = typeResistances(detail.types)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .background(Brush.verticalGradient(gradientColors))
        ) {
            AsyncImage(
                model = detail.imageUrl,
                contentDescription = detail.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.Center)
                    .padding(top = 24.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp, start = 12.dp)
                    .statusBarsPadding()
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                    .clickable(onClick = onBack)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 8.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "#${detail.id.toString().padStart(4, '0')}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFE53935) else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 20.dp)
            ) {
                Text(
                    text = detail.name.capitalize(Locale.current),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    detail.types.forEach { type -> HeroBadge(type) }
                    if (detail.types.size < 2) {
                        Text(
                            text = "Sin tipo secundario",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoBox("⚖️", "${detail.weight / 10.0} kg", "Peso")
                    InfoDivider()
                    InfoBox("📏", "${detail.height / 10.0} m", "Altura")
                    InfoDivider()
                    InfoBox("⭐", "${detail.baseExperience}", "Exp. Base")
                    InfoDivider()
                    InfoBox("✨", "${detail.abilities.size}", "Habilidades")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Base Stats",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = typeCol
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        detail.stats.forEach { (key, value) ->
                            StatRow(key, value, typeCol)
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(200.dp)
                            .background(Color(0xFFEEEEEE))
                            .align(Alignment.CenterVertically)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = typeCol
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AboutRow("🏷️", "Categoría", "—")
                        AboutRow(
                            "⚡",
                            "Habilidades",
                            detail.abilities.joinToString(", ") {
                                it.replace('-', ' ').capitalize(Locale.current)
                            }
                        )
                        AboutRow("🌿", "Hábitat", "—")
                        AboutRow("⭐", "Exp. Base", "${detail.baseExperience}")
                        AboutRow("📈", "Crecimiento", "—")
                        AboutRow("🎯", "Captura", "—")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                if (weaknesses.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Debilidades",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE53935)
                        )
                        Text(
                            text = "Resistencias",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF43A047)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        FlowRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            weaknesses.take(4).forEach { type ->
                                TypeMultiplierChip(type = type, multiplier = "2×")
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        FlowRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            resistances.take(4).forEach { type ->
                                TypeMultiplierChip(type = type, multiplier = "½×")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroBadge(type: String) {
    val color = typeColor(type)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.25f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = typeEmoji(type), fontSize = 12.sp)
            Text(
                text = type.capitalize(Locale.current),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun InfoBox(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun InfoDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(44.dp)
            .background(Color(0xFFEEEEEE))
    )
}

@Composable
private fun StatRow(key: String, value: Int, color: Color) {
    val (emoji, label) = statInfo(key)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.width(42.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(22.dp),
            textAlign = TextAlign.End,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        LinearProgressIndicator(
            progress = { (value / 255f).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun AboutRow(emoji: String, label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color.Gray,
                lineHeight = 11.sp
            )
        }
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.padding(start = 14.dp),
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun TypeMultiplierChip(type: String, multiplier: String) {
    val color = typeColor(type)
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(text = typeEmoji(type), fontSize = 10.sp)
            Text(
                text = type.capitalize(Locale.current),
                fontSize = 9.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = multiplier,
                fontSize = 9.sp,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

private fun statInfo(key: String): Pair<String, String> = when (key) {
    "hp"              -> "❤️" to "HP"
    "attack"          -> "⚔️" to "ATK"
    "defense"         -> "🛡️" to "DEF"
    "special-attack"  -> "💫" to "Sp.ATK"
    "special-defense" -> "🔵" to "Sp.DEF"
    "speed"           -> "⚡" to "VEL"
    else              -> "●" to key.take(6).uppercase()
}

private fun typeWeaknesses(types: List<String>): List<String> {
    val primary = types.firstOrNull() ?: "normal"
    return when (primary) {
        "fire"     -> listOf("water", "ground", "rock")
        "water"    -> listOf("electric", "grass")
        "grass"    -> listOf("fire", "ice", "poison", "flying", "bug")
        "electric" -> listOf("ground")
        "psychic"  -> listOf("bug", "ghost", "dark")
        "ice"      -> listOf("fire", "fighting", "rock", "steel")
        "dragon"   -> listOf("ice", "dragon", "fairy")
        "dark"     -> listOf("fighting", "bug", "fairy")
        "fairy"    -> listOf("poison", "steel")
        "fighting" -> listOf("flying", "psychic", "fairy")
        "poison"   -> listOf("ground", "psychic")
        "ground"   -> listOf("water", "grass", "ice")
        "flying"   -> listOf("electric", "ice", "rock")
        "bug"      -> listOf("fire", "flying", "rock")
        "rock"     -> listOf("water", "grass", "fighting", "ground", "steel")
        "ghost"    -> listOf("ghost", "dark")
        "steel"    -> listOf("fire", "fighting", "ground")
        "normal"   -> listOf("fighting")
        else       -> emptyList()
    }
}

private fun typeResistances(types: List<String>): List<String> {
    val primary = types.firstOrNull() ?: "normal"
    return when (primary) {
        "fire"     -> listOf("fire", "grass", "ice", "bug", "steel", "fairy")
        "water"    -> listOf("fire", "water", "ice", "steel")
        "grass"    -> listOf("water", "electric", "grass", "ground")
        "electric" -> listOf("electric", "flying", "steel")
        "psychic"  -> listOf("fighting", "psychic")
        "ice"      -> listOf("ice")
        "dragon"   -> listOf("fire", "water", "electric", "grass")
        "dark"     -> listOf("ghost", "dark")
        "fairy"    -> listOf("fighting", "bug", "dark")
        "fighting" -> listOf("bug", "rock", "dark")
        "poison"   -> listOf("grass", "fighting", "poison", "bug")
        "ground"   -> listOf("poison", "rock")
        "flying"   -> listOf("grass", "fighting", "bug")
        "bug"      -> listOf("grass", "fighting", "ground")
        "rock"     -> listOf("normal", "fire", "poison", "flying")
        "ghost"    -> listOf("poison", "bug")
        "steel"    -> listOf("normal", "grass", "ice", "flying", "psychic", "bug", "rock", "dragon", "steel")
        "normal"   -> emptyList()
        else       -> emptyList()
    }
}

private fun typeColor(type: String): Color = when (type) {
    "fire"     -> Color(0xFFFF6B35)
    "water"    -> Color(0xFF4A90D9)
    "grass"    -> Color(0xFF5DB85D)
    "electric" -> Color(0xFFF5C518)
    "psychic"  -> Color(0xFFFF5599)
    "ice"      -> Color(0xFF96D9D6)
    "dragon"   -> Color(0xFF7038F8)
    "dark"     -> Color(0xFF705848)
    "fairy"    -> Color(0xFFEE99AC)
    "fighting" -> Color(0xFFC03028)
    "poison"   -> Color(0xFFA040A0)
    "ground"   -> Color(0xFFE0C068)
    "flying"   -> Color(0xFF89AAE3)
    "bug"      -> Color(0xFFA8B820)
    "rock"     -> Color(0xFFB8A038)
    "ghost"    -> Color(0xFF705898)
    "steel"    -> Color(0xFFB8B8D0)
    "normal"   -> Color(0xFFA8A878)
    else       -> Color(0xFF888888)
}

private fun typeEmoji(type: String): String = when (type) {
    "fire"     -> "🔥"
    "water"    -> "💧"
    "grass"    -> "🌿"
    "electric" -> "⚡"
    "psychic"  -> "🔮"
    "ice"      -> "❄️"
    "dragon"   -> "🐉"
    "dark"     -> "🌑"
    "fairy"    -> "✨"
    "fighting" -> "👊"
    "poison"   -> "☠️"
    "ground"   -> "🌍"
    "flying"   -> "🌬️"
    "bug"      -> "🐛"
    "rock"     -> "🪨"
    "ghost"    -> "👻"
    "steel"    -> "⚙️"
    "normal"   -> "⭐"
    else       -> "●"
}
