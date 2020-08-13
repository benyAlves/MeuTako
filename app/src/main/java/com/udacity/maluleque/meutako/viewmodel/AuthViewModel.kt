package com.udacity.maluleque.meutako.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.maluleque.meutako.model.User
import com.udacity.maluleque.meutako.repository.AuthRepository
import com.udacity.maluleque.meutako.utils.Resource
import javax.inject.Inject

class AuthViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel() {

    fun saveUser(user: User): LiveData<Resource<User>> {
        val authenticatedUser: MutableLiveData<Resource<User>> = MutableLiveData()
        authRepository.saveAuthenticatedUser(user).observeForever {
            authenticatedUser.postValue(it)
        }
        return authenticatedUser
    }

}