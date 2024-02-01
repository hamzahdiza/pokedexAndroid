package com.example.pokedexandroidbeta.pokemonlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.pokedexandroidbeta.R
import com.example.pokedexandroidbeta.data.PokedexListEntry
import com.example.pokedexandroidbeta.data.remote.responses.Type
import com.example.pokedexandroidbeta.ui.theme.BgTop
import com.example.pokedexandroidbeta.ui.theme.RobotoCondensed
import com.example.pokedexandroidbeta.ui.theme.TypeBug
import com.example.pokedexandroidbeta.ui.theme.TypeDark
import com.example.pokedexandroidbeta.ui.theme.TypeDragon
import com.example.pokedexandroidbeta.ui.theme.TypeElectric
import com.example.pokedexandroidbeta.ui.theme.TypeFairy
import com.example.pokedexandroidbeta.ui.theme.TypeFighting
import com.example.pokedexandroidbeta.ui.theme.TypeFire
import com.example.pokedexandroidbeta.ui.theme.TypeFlying
import com.example.pokedexandroidbeta.ui.theme.TypeGhost
import com.example.pokedexandroidbeta.ui.theme.TypeGrass
import com.example.pokedexandroidbeta.ui.theme.TypeGround
import com.example.pokedexandroidbeta.ui.theme.TypeIce
import com.example.pokedexandroidbeta.ui.theme.TypeNormal
import com.example.pokedexandroidbeta.ui.theme.TypePoison
import com.example.pokedexandroidbeta.ui.theme.TypePsychic
import com.example.pokedexandroidbeta.ui.theme.TypeRock
import com.example.pokedexandroidbeta.ui.theme.TypeSteel
import com.example.pokedexandroidbeta.ui.theme.TypeWater
import java.util.Locale

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Column(
                modifier = Modifier
                    .background(BgTop)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                    contentDescription = "Pokemon",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally)
                )
                SearchBar(
                    hint = "Search...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    viewModel.searchPokemonList(it)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            PokemonList(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it.isFocused != true && text.isEmpty()
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if (it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.loadPokemonPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }

}

//@Composable
//fun PokedexEntry(
//    entry: PokedexListEntry,
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    viewModel: PokemonListViewModel = hiltViewModel()
//
//) {
//    val defaultDominantColor = MaterialTheme.colors.surface
//    var dominantColor by remember {
//        mutableStateOf(defaultDominantColor)
//    }
//    Log.i("PokedexEntry", "Entry data: $entry")
//    println(entry)
//    Box(
//        contentAlignment = Center,
//        modifier = modifier
//            .shadow(5.dp, RoundedCornerShape(10.dp))
//            .clip(RoundedCornerShape(10.dp))
//            .aspectRatio(1f)
//            // past
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        dominantColor,
//                        defaultDominantColor
//                    )
//                )
//            )
//
//            // new
////            .background(
////                dominantColor
////            )
//            .clickable {
//                navController.navigate(
//                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
//                )
//            }
//    ) {
//        Column {
////            CoilImage(
////                request = ImageRequest.Builder(LocalContext.current)
////                    .data(entry.imageUrl)
////                    .target {
////                        viewModel.calcDominantColor(it) { color ->
////                            dominantColor = color
////                        }
////                    }
////                    .build(),
////                contentDescription = entry.pokemonName,
////                fadeIn = true,
////                modifier = Modifier
////                    .size(120.dp)
////                    .align(CenterHorizontally)
////            )
////            {
////                CircularProgressIndicator(
////                    color = MaterialTheme.colors.primary,
////                    modifier = Modifier.scale(0.5f)
////                )
////            }
//
//            SubcomposeAsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(entry.imageUrl)
//                    .crossfade(true)
//                    .build(),
//                contentDescription = entry.pokemonName,
//                loading = {
//                    CircularProgressIndicator(
//                        color = MaterialTheme.colors.primary, modifier = Modifier.scale(0.5F)
//                    )
//                },
//                success = { success ->
//                    viewModel.calcDominantColor(success.result.drawable) {
//                        dominantColor = it
//                    }
//                    SubcomposeAsyncImageContent()
//                },
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally)
//            )
//
//                Text(
//                    text = entry.pokemonName,
//                    fontFamily = RobotoCondensed,
//                    fontSize = 20.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//        }
//    }
//}

@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Log.i("PokedexEntry", "Entry data: ${entry.data}")
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
//                    .background(Color.Cyan)
                    .padding(16.dp)
                    .weight(0.8f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
            ) {

                Text(
                    text = entry.pokemonName,
                    fontFamily = RobotoCondensed,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                )
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(entry.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = entry.pokemonName,
                    loading = {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.size(80.dp)
                        )
                    },
                    success = { success ->
                        viewModel.calcDominantColor(success.result.drawable) {
                            dominantColor = it
                        }
                        SubcomposeAsyncImageContent(
                            modifier = Modifier
                                .size(100.dp)
                        )
                    },
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .zIndex(-1f)
                .offset(x = 80.dp, y = 85.dp),
        ){
            val icPokeball: Painter = painterResource(id = R.drawable.ic_pokeball_bg)
            Image(
                painter = icPokeball,
                contentDescription = "pokeball",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.5f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            entry?.data?.types?.let { PokemonTypes(types = it) }
            Log.i("Types", entry.data?.types?.get(0)?.type?.name.toString())
        }
    }
}

@Composable
fun PokemonTypes(types: List<Type?>) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 15.dp, start = 8.dp)
            .fillMaxWidth(0.5f)
            .zIndex(3f)
            .offset(y = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Log.i("COBA", types.toString())

        items(types) { item ->
            val chipColor = when (item?.type?.name?.lowercase(Locale.ROOT)) {
                "normal" -> TypeNormal
                "fire" -> TypeFire
                "water" -> TypeWater
                "electric" -> TypeElectric
                "grass" -> TypeGrass
                "ice" -> TypeIce
                "fighting" -> TypeFighting
                "poison" -> TypePoison
                "ground" -> TypeGround
                "flying" -> TypeFlying
                "psychic" -> TypePsychic
                "bug" -> TypeBug
                "rock" -> TypeRock
                "ghost" -> TypeGhost
                "dragon" -> TypeDragon
                "dark" -> TypeDark
                "steel" -> TypeSteel
                "fairy" -> TypeFairy
                else -> Color.Gray
            }
            TypeChip(type = item?.type?.name.toString(), chipColor = chipColor)
        }
    }
}


@Composable
fun TypeChip(type: String, chipColor: Color) {
    Box(
        modifier = Modifier
            .padding(top = 8.dp, end = 8.dp)
            .height(32.dp)
            .background(
                color = chipColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),

        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.capitalize(Locale.ROOT),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
) {
    Column {
        Row {
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowIndex * 2 + 2) {
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}