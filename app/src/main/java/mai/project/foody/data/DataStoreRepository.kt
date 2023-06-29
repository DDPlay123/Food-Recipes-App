package mai.project.foody.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import mai.project.foody.data.DataStoreRepository.PreferenceKeys.Preferences_BackOnline
import mai.project.foody.data.DataStoreRepository.PreferenceKeys.Preferences_DIET_TYPE
import mai.project.foody.data.DataStoreRepository.PreferenceKeys.Preferences_DIET_TYPE_ID
import mai.project.foody.data.DataStoreRepository.PreferenceKeys.Preferences_MEAL_TYPE
import mai.project.foody.data.DataStoreRepository.PreferenceKeys.Preferences_MEAL_TYPE_ID
import mai.project.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import mai.project.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import mai.project.foody.util.Method
import java.io.IOException
import javax.inject.Inject

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)

// Build DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "FOODY_PREFERENCES",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, "FOODY_PREFERENCES"))
    })

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        const val Preferences_MEAL_TYPE = "mealType"
        const val Preferences_MEAL_TYPE_ID = "mealTypeId"
        const val Preferences_DIET_TYPE = "dietType"
        const val Preferences_DIET_TYPE_ID = "dietTypeId"
        const val Preferences_BackOnline = "backOnline"
    }

    // DataStore Point
    private val dataStore = context.dataStore

    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getMealAndDietType", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            MealAndDietType(
                it[stringPreferencesKey(Preferences_MEAL_TYPE)] ?: DEFAULT_MEAL_TYPE,
                it[intPreferencesKey(Preferences_MEAL_TYPE_ID)] ?: 0,
                it[stringPreferencesKey(Preferences_DIET_TYPE)] ?: DEFAULT_DIET_TYPE,
                it[intPreferencesKey(Preferences_DIET_TYPE_ID)] ?: 0
            )
        }

    suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        putData(Preferences_MEAL_TYPE, mealType)
        putData(Preferences_MEAL_TYPE_ID, mealTypeId)
        putData(Preferences_DIET_TYPE, dietType)
        putData(Preferences_DIET_TYPE_ID, dietTypeId)
    }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getBackOnline", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[booleanPreferencesKey(Preferences_BackOnline)] ?: false
        }

    suspend fun saveBackOnline(backOnline: Boolean) {
        putData(Preferences_BackOnline, backOnline)
    }

    // PUT Any Data
    private suspend fun <T> putData(key: String, value: T) {
        when (value) {
            is Int -> putIntData(key, value)
            is Long -> putLongData(key, value)
            is String -> putStringData(key, value)
            is Boolean -> putBooleanData(key, value)
            is Float -> putFloatData(key, value)
            is Double -> putDoubleData(key, value)
            else -> throw IllegalArgumentException("This type cannot be saved to the Data Store")
        }
    }

    // GET Any Data
    private fun <T> getData(key: String, defaultValue: T): T {
        val data = when (defaultValue) {
            is Int -> getIntData(key, defaultValue)
            is Long -> getLongData(key, defaultValue)
            is String -> getStringData(key, defaultValue)
            is Boolean -> getBooleanData(key, defaultValue)
            is Float -> getFloatData(key, defaultValue)
            is Double -> getDoubleData(key, defaultValue)
            else -> throw IllegalArgumentException("This type cannot be saved to the Data Store")
        }
        return data as T
    }

    // PUT Int Variable
    private suspend fun putIntData(key: String, value: Int) = dataStore.edit {
        it[intPreferencesKey(key)] = value
    }

    // GET Int Variable
    private fun getIntData(key: String, default: Int = 0): Int = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getIntData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[intPreferencesKey(key)] ?: default
        }.first()
    }

    // PUT Long Variable
    private suspend fun putLongData(key: String, value: Long) = dataStore.edit {
        it[longPreferencesKey(key)] = value
    }

    // GET Long Variable
    private fun getLongData(key: String, default: Long = 0): Long = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getLongData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[longPreferencesKey(key)] ?: default
        }.first()
    }

    // PUT String Variable
    private suspend fun putStringData(key: String, value: String) = dataStore.edit {
        it[stringPreferencesKey(key)] = value
    }

    // GET String Variable
    private fun getStringData(key: String, default: String? = null): String = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getStringData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[stringPreferencesKey(key)] ?: default
        }.first().toString()
    }

    // PUT Boolean Variable
    private suspend fun putBooleanData(key: String, value: Boolean) = dataStore.edit {
        it[booleanPreferencesKey(key)] = value
    }

    // GET Boolean Variable
    private fun getBooleanData(key: String, default: Boolean = false): Boolean = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getBooleanData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[booleanPreferencesKey(key)] ?: default
        }.first()
    }

    // PUT Float Variable
    private suspend fun putFloatData(key: String, value: Float) = dataStore.edit {
        it[floatPreferencesKey(key)] = value
    }

    // GET Float Variable
    private fun getFloatData(key: String, default: Float = 0.0f): Float = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getFloatData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[floatPreferencesKey(key)] ?: default
        }.first()
    }

    // PUT Double Variable
    private suspend fun putDoubleData(key: String, value: Double) = dataStore.edit {
        it[doublePreferencesKey(key)] = value
    }

    // GET Double Variable
    private fun getDoubleData(key: String, default: Double = 0.00): Double = runBlocking {
        return@runBlocking dataStore.data.catch { exception ->
            if (exception is IOException) {
                Method.logE("DataStore", "Error getDoubleData", exception)
                emit(emptyPreferences())
            } else throw exception
        }.map {
            it[doublePreferencesKey(key)] ?: default
        }.first()
    }
}