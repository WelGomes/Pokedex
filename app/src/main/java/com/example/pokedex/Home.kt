package com.example.pokedex

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.pokedex.model.Pokemon
import com.example.pokedex.network.Endpoint
import com.example.pokedex.network.NetworkUtils
import kotlinx.coroutines.launch

@Composable
fun Home(navController: NavController) {

    Image(painter = painterResource(id = R.drawable.pokemonsimbolo),
        contentDescription = " ",
        modifier = Modifier.fillMaxSize()
    )

    val retrofitClient = NetworkUtils
        .getRetrofitInstance("https://pokeapi.co/api/v2/")

    val pokeApi = retrofitClient.create(Endpoint::class.java)

    val context = LocalContext.current

    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    DisposableEffect(context) {
        scope.launch {
            try {
                val response = pokeApi.getPokemons(100)
                pokemonList = response.results
                isLoading = false
            } catch (e: Exception) {
                isError = true
            }
        }
        onDispose { }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        items(pokemonList.chunked(2)) { rowPokemons ->
            LazyRow {
                items(rowPokemons) { pokemon ->
                    //PokemonImage(pokemon = pokemon)
                    PokemonImage(navController = navController, pokemon = pokemon)
                }
            }
        }
    }
}

@Composable
fun PokemonImage(navController: NavController, pokemon: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        fun getImagePokemon(imageId: String): String {
            return "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${imageId.padStart(3, '0')}.png"
        }
        val imagePokemon = getImagePokemon(pokemon.url.replace("https://pokeapi.co/api/v2/pokemon/", "").replace("/", ""))

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { navController.navigate("battle/${pokemon.name}?imageUrl=${imagePokemon}") }
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(imagePokemon),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = pokemon.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 30.sp),
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
