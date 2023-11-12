package com.example.pokedexandroidbeta.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.pokedexandroidbeta.data.remote.responses.Pokemon
import com.example.pokedexandroidbeta.repository.PokemonRepository
import com.example.pokedexandroidbeta.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}