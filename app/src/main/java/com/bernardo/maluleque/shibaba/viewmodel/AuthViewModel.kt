package com.bernardo.maluleque.shibaba.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bernardo.maluleque.shibaba.model.User
import com.bernardo.maluleque.shibaba.repository.AuthRepository
import com.bernardo.maluleque.shibaba.utils.Resource

class AuthViewModel(val authRepository: AuthRepository) : ViewModel() {

    fun saveUser(user: User): LiveData<Resource<User>> {
        val authenticatedUser: MutableLiveData<Resource<User>> = MutableLiveData()
        authRepository.saveAuthenticatedUser(user).observeForever {
            authenticatedUser.postValue(it)
        }
        return authenticatedUser
    }

}