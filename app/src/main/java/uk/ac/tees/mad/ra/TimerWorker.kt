package uk.ac.tees.mad.ra

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
            delay(3 * 60 * 1000)
            timeRemaining -= 180000
            sendNotification(recipeName, null, timeRemaining)
        }

        sendNotification(recipeName, "Timer finished", null)
        Log.d("TimerWorker", "Timer finished for $recipeName")
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
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val cancelIntent =
            Intent(applicationContext, CancelNotificationReceiver::class.java).apply {
                putExtra("recipeName", recipeName)
            }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        val builder = NotificationCompat.Builder(applicationContext, "timer_channel")
            .setSmallIcon(R.drawable.recipe)
            .setContentTitle(title ?: recipeName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(
                R.drawable.recipe,
                "Cancel Timer",
                cancelPendingIntent
            )



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

