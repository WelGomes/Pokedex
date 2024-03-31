package com.example.pokedex

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
fun Battle(navController: NavController, name: String, imageUrl: String?) {

    Image(
        painter = painterResource(id = R.drawable.campobatalha),
        contentDescription = " ",
        modifier = Modifier.fillMaxSize()
    )


    var opponentPokemon by remember { mutableStateOf<Pokemon?>(null) }
    var battlesWon by remember { mutableStateOf(0) }
    var userHealth by remember { mutableStateOf(100) }
    var opponentHealth by remember { mutableStateOf(100) }
    val maxHealth = 100 // Define a saúde máxima do Pokémon
    var gameOver by remember { mutableStateOf(false) }
    var gameResultMessage by remember { mutableStateOf("") }
    var userAttackDamage by remember { mutableStateOf(0) }
    var opponentAttackDamage by remember { mutableStateOf(0) }
    val pokeApi = remember { NetworkUtils.getRetrofitInstance("https://pokeapi.co/api/v2/").create(Endpoint::class.java) }

    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        scope.launch {
            try {
                val response = pokeApi.getPokemons(100)
                opponentPokemon = response.results.random()
            } catch (e: Exception) {
            }
        }
        onDispose { }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        opponentPokemon?.let { pokemon ->
            Column(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text(text = "Opponent: ${pokemon.name}", color = androidx.compose.ui.graphics.Color.White)

                val opponentImageUrl = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/${pokemon.url.replace("https://pokeapi.co/api/v2/pokemon/", "").replace("/", "").padStart(3, '0')}.png"

                Image(
                    painter = rememberImagePainter(opponentImageUrl),
                    contentDescription = "Opponent Pokemon Image",
                    modifier = Modifier.size(120.dp)
                )
                Text(text = "Opponent Health: $opponentHealth", color = androidx.compose.ui.graphics.Color.White)
                if (opponentAttackDamage > 0) {
                    Text("Opponent attacked for $opponentAttackDamage damage", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }

    imageUrl?.let { url ->
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Image(
                    painter = rememberImagePainter(url),
                    contentDescription = "Pokemon Image",
                    modifier = Modifier.size(120.dp)
                )
                Text(
                    text = "Your: $name",
                    color = androidx.compose.ui.graphics.Color.White
                )
                if (userAttackDamage > 0) {
                    Text("You attacked for $userAttackDamage damage",
                        color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!gameOver) {
            Button(
                onClick = {
                    val userAttack = (0..50).random()
                    val opponentAttack = (0..50).random()
                    userAttackDamage = opponentAttack
                    opponentAttackDamage = userAttack
                    userHealth -= opponentAttack
                    opponentHealth -= userAttack

                    if (userHealth <= 0) {
                        gameOver = true
                        gameResultMessage = "Game Over - You Lost"
                    } else if (opponentHealth <= 0) {
                        battlesWon++
                        if (battlesWon >= 3) {
                            gameOver = true
                            gameResultMessage = "Congratulations! You Win!"
                        } else {
                            scope.launch {
                                try {
                                    val response = pokeApi.getPokemons(100)
                                    opponentPokemon = response.results.random()
                                    opponentHealth = maxHealth
                                } catch (e: Exception) {
                                }
                            }
                        }
                        userHealth = maxHealth
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Battle", color = androidx.compose.ui.graphics.Color.White)
            }

            Text(
                text = "Your Health ${userHealth}",
                modifier = Modifier.padding(bottom = 650.dp),
                color = androidx.compose.ui.graphics.Color.White
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = gameResultMessage,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        if (gameResultMessage == "Game Over - You Lost") {
                            navController.popBackStack()
                        } else {
                            userHealth = maxHealth
                            opponentHealth = maxHealth
                            battlesWon = 0
                            gameOver = false
                            scope.launch {
                                try {
                                    val response = pokeApi.getPokemons(100)
                                    opponentPokemon = response.results.random()
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                ) {
                    Text("Play Again", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}