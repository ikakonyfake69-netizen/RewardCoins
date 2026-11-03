
package com.example.rewardcoins

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "rewardcoins_preferences"
)

class MonedasDataStore(private val context: Context) {

    companion object {
        private val MONEDAS_KEY = intPreferencesKey("monedas")
    }

    val monedas: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MONEDAS_KEY] ?: 0
    }

    suspend fun agregarMonedas(cantidad: Int) {
        context.dataStore.edit { preferences ->
            val actuales = preferences[MONEDAS_KEY] ?: 0
            preferences[MONEDAS_KEY] = actuales + cantidad
        }
    }
}
