package com.udacity.maluleque.meutako.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.maluleque.meutako.model.User
import com.udacity.maluleque.meutako.repository.AuthRepository
import com.udacity.maluleque.meutako.utils.Resource

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private var authRepository: AuthRepository = AuthRepository()

    fun saveUser(user: User): LiveData<Resource<User>> {
        val authenticatedUser: MutableLiveData<Resource<User>> = MutableLiveData()
        authRepository.saveAuthenticatedUser(user).observeForever {
            authenticatedUser.postValue(it)
        }
        return authenticatedUser
    }

}