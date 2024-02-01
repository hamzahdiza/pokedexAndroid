package com.example.pokedexandroidbeta.pokemondetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedexandroidbeta.data.PokedexDetailEntry
import com.example.pokedexandroidbeta.data.remote.responses.Pokemon
import com.example.pokedexandroidbeta.repository.PokemonRepository
import com.example.pokedexandroidbeta.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokedexDetailResult = mutableStateOf<Pokemon?>(null)
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)


    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }

    suspend fun loadDetailPokemon(pokemonName: String) {
        viewModelScope.launch {
            isLoading.value = true
            when (val result = repository.getPokemonInfo(pokemonName)) {
                is Resource.Success -> {
                    PokedexDetailEntry(result.data)
                    isLoading.value = false

                }

                is Resource.Error -> {
                    loadError.value = result.message ?: "An unknown error occurred."
                    isLoading.value = false
                }

                else -> null
            }
        }
    }

}