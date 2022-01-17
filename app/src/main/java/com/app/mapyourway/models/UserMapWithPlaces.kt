package com.app.mapyourway.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class UserMapWithPlaces(@Embedded val userMap: UserMap,
                             @Relation(
                             parentColumn = "userMapId",
                             entityColumn = "mapId"
                         )
                         val places: List<Place>)
