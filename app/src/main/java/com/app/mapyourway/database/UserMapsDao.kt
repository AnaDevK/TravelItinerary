package com.app.mapyourway.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.mapyourway.models.Place
import com.app.mapyourway.models.UserMap
import com.app.mapyourway.models.UserMapWithPlaces

@Dao
interface UserMapsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMap(userMap: UserMap)

    @Update
    suspend fun update(userMap: UserMap)

    @Delete
    suspend fun delete(userMap: UserMap)

    @Query("SELECT * from UserMap WHERE userMapId =:id")
    fun getUserMapById(id: Long): UserMap

    @Query("SELECT * from UserMap ORDER BY userMapId DESC")
    fun getAllUserMaps(): LiveData<List<UserMap>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPlace(place: Place)

    @Update
    suspend fun update(place: Place)

    @Delete
    suspend fun delete(place: Place)

    @Query("SELECT * from Place WHERE placeId =:id")
    fun getPlaceById(id: Long): LiveData<Place>

    @Query("SELECT * from Place")
    fun getAllPlaces(): LiveData<List<Place>>

    @Transaction
    @Query("SELECT * FROM UserMap")
    fun getUserMapsWithPlaces(): LiveData<List<UserMapWithPlaces>>

    @Transaction
    @Query("SELECT * FROM UserMap WHERE userMapId =:id")
    fun getUserMapsWithPlacesById(id: Long): LiveData<List<UserMapWithPlaces>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertWithId(userMap: UserMap): Long

    @Transaction
    suspend fun insertJoin(userMap: UserMap) {
        val id = insertWithId(userMap)
        userMap.places.forEach { i ->
            i.mapId = id
            insertPlace(i)
        }
    }

    @Transaction
    suspend fun deleteUserMapWithPlaces(userMap: UserMap) {
        delete(userMap)
        userMap.places.forEach { place ->
            delete(place)
        }
    }

    @Transaction
    suspend fun updateUserMapWithPlaces(userMap: UserMap) {
        val places = getAllPlacesOnMap(userMap.userMapId)
        places.forEach { place ->
            delete(place)
        }
        userMap.places.forEach { place ->
            insertPlace(place)
        }
    }
    @Query("SELECT * from Place WHERE mapId = :userMapId")
    fun getAllPlacesOnMap(userMapId: Long): List<Place>
}
