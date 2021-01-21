package paul.barthuel.go4lunch.ui.notification

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import paul.barthuel.go4lunch.R

class NotificationHelper internal constructor(private val context: Context) {
    private var mManager: NotificationManager? = null

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        manager!!.createNotificationChannel(channel)
    }

    private val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }

    fun displayNotification(message: String?) {
        val notification = NotificationCompat.Builder(context, channelID)
                .setContentTitle("Time to lunch !")
                .setSmallIcon(R.drawable.ic_baseline_emoji_people_24)
                .setContentText(message)
                .build()
        manager!!.notify(0, notification)
    }

    companion object {
        private const val channelID = "channelID"
        private const val channelName = "Channel Name"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}