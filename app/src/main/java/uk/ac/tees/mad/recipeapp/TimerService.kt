package uk.ac.tees.mad.recipeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private lateinit var timer: CountDownTimer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duration = intent?.getLongExtra("DURATION", 600000) ?: 600000
        val recipeName = intent?.getStringExtra("recipeName") ?: "current"
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TODO("Not yet implemented")
            }

            override fun onFinish() {
                sendNotification("Your recipe is cooked.")
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun sendNotification(recipeName: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "timer_channel"
        val channelName = "Recipe cook time"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Recipe cooked")
            .setContentText("Your $recipeName recipe is ready!")
            .setSmallIcon(R.drawable.recipe)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}