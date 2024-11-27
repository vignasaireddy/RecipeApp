package uk.ac.tees.mad.recipeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import uk.ac.tees.mad.recipeapp.viewmodels.RecipeDetailsViewModel

class CancelNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val recipeName = intent.getStringExtra("recipeName") ?: return
        val notificationId =
            recipeName.hashCode()
        val recipeDetailsViewModel = RecipeDetailsViewModel()
        recipeDetailsViewModel.cancelNotification(context, recipeName)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(notificationId)


        Log.d("CancelNotificationReceiver", "Notification canceled for recipe: $recipeName")
    }

}

