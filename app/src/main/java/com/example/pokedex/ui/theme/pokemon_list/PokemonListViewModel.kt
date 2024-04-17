package com.example.pokedex.ui.theme.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.PokemonRepositoryImpl
import com.example.pokedex.model.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val pokemonRepository: PokemonRepository = PokemonRepositoryImpl()
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<PokemonListViewState>(PokemonListViewState.Loading)
    val pokemonState = _pokemonList.asStateFlow()

    private fun getPokemon() {
        viewModelScope.launch {
            pokemonRepository.getPokemon(
                onSuccess = {
                    if (it.isEmpty()) {
                        _pokemonList.value = PokemonListViewState.Empty
                    } else {
                        _pokemonList.value = PokemonListViewState.Success(it)
                    }
                },
                onError = {
                    _pokemonList.value = PokemonListViewState.Error(it)
                }
            )
        }
    }

    init {
        getPokemon()
    }
}

sealed class PokemonListViewState {
    data object Loading : PokemonListViewState()
    data object Empty : PokemonListViewState()
    data class Success(val pokemonList: List<Pokemon>) : PokemonListViewState()
    data class Error(val message: String) : PokemonListViewState()
}