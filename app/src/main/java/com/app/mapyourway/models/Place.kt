package com.app.mapyourway.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Place(
    @PrimaryKey(autoGenerate = true)
    var placeId: Long = 0L,
    @ColumnInfo(name = "mapId")
    var mapId: Long = 0L,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "link")
    var link: String = "",
    @ColumnInfo(name = "lat")
    var latitude: Double = 0.0,
    @ColumnInfo(name = "long")
    var longitude: Double = 0.0
) : Serializable