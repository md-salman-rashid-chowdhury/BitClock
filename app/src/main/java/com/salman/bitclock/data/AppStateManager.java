package com.salman.bitclock.data;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Central state manager for the app.
 * Uses a HashMap to store and retrieve application data and settings in-memory.
 */
@Singleton
public class AppStateManager {

    // HashMap to store any kind of app data (Alarms, Timers, Settings)
    // Key: String identifier, Value: Object (the data)
    private final Map<String, Object> dataStore;

    @Inject
    public AppStateManager() {
        this.dataStore = new HashMap<>();
    }

    /**
     * Stores data in the map.
     * @param key The unique identifier for the data.
     * @param value The object to store.
     */
    public void saveData(String key, Object value) {
        dataStore.put(key, value);
    }

    /**
     * Retrieves data from the map.
     * @param key The identifier for the data.
     * @return The stored object, or null if not found.
     */
    public Object getData(String key) {
        return dataStore.get(key);
    }

    /**
     * Removes data from the map.
     * @param key The identifier to remove.
     */
    public void removeData(String key) {
        dataStore.remove(key);
    }

    /**
     * Clears all stored data.
     */
    public void clearAll() {
        dataStore.clear();
    }
}
