package com.app.mapyourway.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.mapyourway.database.UserMapsDatabase
import com.app.mapyourway.database.UserMapsRepository
import com.app.mapyourway.models.UserMap
import com.app.mapyourway.models.UserMapWithPlaces
import kotlinx.coroutines.launch

class UserMapViewModel(application: Application): AndroidViewModel(application) {

    private var repository: UserMapsRepository
    var allUserMaps: LiveData<List<UserMapWithPlaces>>
    init {
        val dao = UserMapsDatabase.getDatabase(application).userMapsDatabaseDao()
        repository = UserMapsRepository(dao)
        allUserMaps = repository.allUserMaps
    }

    fun insert(item: UserMap) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: UserMap) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: UserMap) = viewModelScope.launch {
        repository.delete(item)
    }
}