package uk.ac.tees.mad.recipeapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.recipeapp.data.Recipe
import uk.ac.tees.mad.recipeapp.data.api.RetrofitInstance

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


    fun startTimer(recipeName: String, duration: Double) {
        if (timerState.value == TimerState.Stopped) {
            timerJob = viewModelScope.launch {
                val startTime = System.currentTimeMillis()
                val endTime = startTime + duration.toLong() * 60 * 1000
                _timerState.value = TimerState.Running(startTime)
                while (System.currentTimeMillis() < endTime) {
                    delay(1000)
                    _timerState.value = TimerState.Running(endTime - System.currentTimeMillis())
                }
                _timerState.value = TimerState.Finished
                sendNotification(recipeName)
            }
        } else {
            timerJob?.cancel()
            _timerState.value = TimerState.Stopped
        }
    }

    private fun sendNotification(recipeName: String) {
        // Implement notification logic here
    }


}

sealed class TimerState {
    object Stopped : TimerState()
    data class Running(val timeRemaining: Long) : TimerState()
    object Finished : TimerState()
}
