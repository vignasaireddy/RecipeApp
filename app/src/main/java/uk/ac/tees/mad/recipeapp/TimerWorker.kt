package uk.ac.tees.mad.recipeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.util.Locale

class TimerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val recipeName = inputData.getString("recipeName") ?: "Recipe"
        val timerDuration = inputData.getLong("timerDuration", 10 * 60 * 1000)
        Log.d("TimerWorker", "Starting timer for $recipeName")
        Log.d("TimerWorker", "Timer duration: $timerDuration")
        sendNotification(recipeName, "Timer started", timerDuration)

        var timeRemaining = timerDuration
        while (timeRemaining > 0) {
            delay(60 * 1000)
            timeRemaining -= 60000
            sendNotification(recipeName, null, timeRemaining)
        }

        sendNotification(recipeName, "Timer finished", null)

        return Result.success()
    }

    private fun sendNotification(recipeName: String, title: String?, timeRemaining: Long?) {
        Log.d("TimerWorker", "Sending notification for $recipeName, time remaining: $timeRemaining")
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "timer_channel"
            val channelName = "Timer"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "timer_channel")
            .setSmallIcon(R.drawable.recipe)
            .setContentTitle(title ?: recipeName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        


        if (timeRemaining != null) {
            val minutes = timeRemaining / 1000 / 60
            val seconds = timeRemaining / 1000 % 60
            val timeRemainingText =
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            builder.setContentText("$recipeName: $timeRemainingText remaining")
        } else {
            builder.setContentText("Your $recipeName recipe is ready!")
        }
        val notId = recipeName.hashCode()
        notificationManager.notify(notId, builder.build())
    }
}

