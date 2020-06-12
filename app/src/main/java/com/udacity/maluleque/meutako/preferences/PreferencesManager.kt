package com.udacity.maluleque.meutako.preferences

import android.content.SharedPreferences

class PreferencesManager(var pref: SharedPreferences) {

    var isFirstLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    var transactions: String?
        get() = pref.getString(KEY_TRANSACTIONS, "No Transactions today. \n Remember to add")
        set(transactions) {
            if (transactions!!.isNotBlank()) {
                editor.putString(KEY_TRANSACTIONS, transactions)
                editor.commit()
            }
        }


    companion object {
        private const val KEY_TRANSACTIONS = "transactions"
        private var INSTANCE: PreferencesManager? = null
        lateinit var editor: SharedPreferences.Editor
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

        @JvmStatic
        @Synchronized
        fun getInstance(prefs: SharedPreferences): PreferencesManager? {
            if (INSTANCE == null) {
                INSTANCE = PreferencesManager(prefs)
            }
            return INSTANCE
        }
    }

    init {
        editor = pref.edit()
    }
}