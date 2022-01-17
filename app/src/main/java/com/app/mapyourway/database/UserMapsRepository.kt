package com.app.mapyourway.database

import androidx.lifecycle.LiveData
import com.app.mapyourway.models.UserMap
import com.app.mapyourway.models.UserMapWithPlaces

class UserMapsRepository(private val userMapsDao: UserMapsDao) {

    val allUserMaps: LiveData<List<UserMapWithPlaces>> = userMapsDao.getUserMapsWithPlaces()

    suspend fun insert(item: UserMap) {
        userMapsDao.insertJoin(item)
    }

    suspend fun update(item: UserMap) {
        userMapsDao.updateUserMapWithPlaces(item)
    }

    suspend fun delete(item: UserMap) {
        userMapsDao.deleteUserMapWithPlaces(item)
    }

}