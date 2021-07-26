package com.example.routes

import com.example.data.Room
import com.example.data.models.BasicApiResponse
import com.example.data.models.CreateRoomRequest
import com.example.data.models.RoomResponse
import com.example.other.Constants.MAX_ROOM_SIZE
import com.example.server
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.createRoomRoute() {
    route("/api/createRoom") {
        post {
            val roomRequest = call.receiveOrNull<CreateRoomRequest>()

            if(roomRequest == null) {
                call.respond(BadRequest)
                return@post
            }

            if(server.rooms[roomRequest.name] != null) {
                call.respond(
                    OK,
                    BasicApiResponse(false, "Room already exists.")
                )
                return@post
            }

            if(roomRequest.maxPlayers < 2) {
                call.respond(
                    OK,
                    BasicApiResponse(false, "The minimum rooms size is 2.")
                )
                return@post
            }

            if(roomRequest.maxPlayers > MAX_ROOM_SIZE) {
                call.respond(
                    OK,
                    BasicApiResponse(false, "The maximum room size is $MAX_ROOM_SIZE")
                )
                return@post
            }

            val room = Room(
                roomRequest.name,
                roomRequest.maxPlayers
            )
            server.rooms[roomRequest.name] = room

            call.respond(
                OK,
                BasicApiResponse(true)
            )

        }
    }
}

fun Route.getRoomsRoute() {
    route("/api/getRooms") {
        get {
            val searchQuery = call.parameters["searchQuery"]
            if(searchQuery == null) {
                call.respond(BadRequest)
                return@get
            }

            val roomsResult = server.rooms.filterKeys {
                it.contains(searchQuery, ignoreCase = true)
            }

            val roomResponses = roomsResult.values.map {
                RoomResponse(it.name, it.maxPlayers, it.players.size)
            }.sortedBy { it.name }

            call.respond(OK, roomResponses)

        }
    }
}

fun Route.joinRoomRoute() {
    route("/api/joinRoom") {
        get {
            val username = call.parameters["username"]
            val roomName = call.parameters["roomName"]

            if(username == null || roomName == null) {
                call.respond(BadRequest)
                return@get
            }

            val room = server.rooms[roomName]

            when {
                room == null -> {
                    call.respond(
                        OK,
                        BasicApiResponse(false, "Room not found.")
                    )
                }

                room.containsPlayer(username) -> {
                    call.respond(
                        OK,
                        BasicApiResponse(false, "A player with this username already joined.")
                    )
                }

                room.players.size >= room.maxPlayers -> {
                    call.respond(
                        OK,
                        BasicApiResponse(false, "This room is already full.")
                    )
                }

                else -> {
                    call.respond(
                        OK,
                        BasicApiResponse(true)
                    )
                }
            }


        }
    }
}






