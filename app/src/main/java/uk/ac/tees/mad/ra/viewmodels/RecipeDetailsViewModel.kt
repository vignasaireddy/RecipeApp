package uk.ac.tees.mad.ra.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.ra.TimerWorker
import uk.ac.tees.mad.ra.data.Recipe
import uk.ac.tees.mad.ra.data.api.RetrofitInstance

class RecipeDetailsViewModel : ViewModel() {
    private val _recipeDetails = MutableStateFlow<Recipe?>(null)
    val recipeDetails = _recipeDetails.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()


    private val _timerState = MutableStateFlow<TimerState>(TimerState.Stopped)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun fetchRecipeDetails(uri: String?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitInstance.api.getRecipeDetails(
                    uri = uri ?: ""
                )
                Log.d("URI", "URI: $uri")
                Log.d("RecipeDetailsViewModel", "fetchRecipeDetails: $response")
                _recipeDetails.value = response.hits.firstOrNull()?.recipe
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }


    fun startTimer(recipeName: String, duration: Double, context: Context) {
        if (timerState.value == TimerState.Stopped) {
            val data = Data.Builder()
                .putString("recipeName", recipeName)
                .putLong("timerDuration", convertDoubleToMillies(duration))
                .build()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val request = OneTimeWorkRequestBuilder<TimerWorker>()
                .setInputData(data)
                .setConstraints(constraints)

                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("timer_worker_$recipeName", ExistingWorkPolicy.REPLACE, request)
            Log.d("Converted Time", duration.toString())
            _timerState.value = TimerState.Running(convertDoubleToMillies(duration))
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("timer_worker_$recipeName")
            _timerState.value = TimerState.Stopped
        }
    }

    fun cancelNotification(context: Context, recipeName: String) {
        WorkManager.getInstance(context).cancelUniqueWork("timer_worker_$recipeName")
        Log.d("RecipeDetailsViewModel", "cancelNotification: $recipeName")
        _timerState.value = TimerState.Stopped
    }
}

sealed class TimerState {
    object Stopped : TimerState()
    data class Running(val timeRemaining: Long) : TimerState()
    object Finished : TimerState()
}

fun convertDoubleToMillies(duration: Double): Long {
    val mins = duration.toInt()
    val seconds = ((duration - mins) * 60).toInt()
    return (mins * 60 * 1000 + seconds * 1000).toLong()
}
