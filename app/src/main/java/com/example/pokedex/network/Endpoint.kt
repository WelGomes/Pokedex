package com.example.pokedex.network

import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Endpoint {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int): PokemonListResponse

    @GET("pokemon/{name}")
     fun getPokemonDetails(@Path("name") name: String): Pokemon
}

