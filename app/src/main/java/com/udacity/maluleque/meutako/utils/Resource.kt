package com.udacity.maluleque.meutako.utils

/*
*
* */
data class Resource<out T>(val status: Status, val data: T, val message: String? = null) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String?, data: T): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T): Resource<T> {
            return Resource(Status.LOADING, data)
        }
    }
}