package com.example.pokedex.data

import com.example.pokedex.model.Pokemon
import com.example.pokedex.network.Endpoint
import com.example.pokedex.network.NetworkUtils

interface PokemonRepository {
    suspend fun getPokemon(onSuccess: (List<Pokemon>) -> Unit, onError: (String) -> Unit = {})
}

class PokemonRepositoryImpl(
    val pokeApi: Endpoint = NetworkUtils.getRetrofitInstance("https://pokeapi.co/api/v2/")
        .create(Endpoint::class.java)
) : PokemonRepository {
    override suspend fun getPokemon(onSuccess: (List<Pokemon>) -> Unit, onError: (String) -> Unit) {
        runCatching {
            pokeApi.getPokemons(100).results
        }.onFailure {
            onError(it.message ?: "An error occurred")
        }.onSuccess {
            onSuccess(it)
        }
    }
}