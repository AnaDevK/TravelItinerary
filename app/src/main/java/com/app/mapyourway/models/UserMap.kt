package com.app.mapyourway.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class UserMap(
    @PrimaryKey(autoGenerate = true)
    var userMapId: Long = 0L,
    @ColumnInfo(name = "title")
    var title: String = "Test user map",
    @Ignore
    var places: MutableList<Place>
) : Serializable {
    constructor() : this(0L, "", mutableListOf<Place>())
}