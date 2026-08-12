package com.salman.bitclock.data

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateManager @Inject constructor() {
    private val dataStore = ConcurrentHashMap<String, Any>()

    fun saveData(key: String, value: Any) {
        dataStore[key] = value
    }

    fun getData(key: String): Any? = dataStore[key]

    fun removeData(key: String) {
        dataStore.remove(key)
    }

    fun clearAll() {
        dataStore.clear()
    }
}
