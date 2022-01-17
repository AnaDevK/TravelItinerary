package com.app.mapyourway.database

import androidx.room.*
import com.app.mapyourway.models.Place

@Dao
interface PlacesDao {
@Insert(onConflict = OnConflictStrategy.ABORT)
suspend fun insert(place: Place)
@Update
suspend fun update(place: Place)
@Delete
suspend fun delete(place: Place)
//@Query("SELECT * from places_table WHERE placeId =:id")
//fun getPlaceById(id: Long): LiveData<Place>
//@Query("SELECT * from places_table")
//fun getAll(): LiveData<List<Place>>
}