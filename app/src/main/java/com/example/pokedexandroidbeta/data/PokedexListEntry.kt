package com.example.pokedexandroidbeta.data

import com.example.pokedexandroidbeta.data.remote.responses.Pokemon

data class PokedexListEntry(
    val pokemonName: String,
    val imageUrl: String,
    val number: Int,
    val data: Pokemon?
)
