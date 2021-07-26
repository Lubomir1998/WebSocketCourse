package com.example.data.models

import com.example.data.Player

data class RoomResponse(
    val name: String,
    val maxPlayers: Int,
    val playerCount: Int
)
