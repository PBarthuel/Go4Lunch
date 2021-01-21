package paul.barthuel.go4lunch.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import org.threeten.bp.LocalTime
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.ui.notification.data.NotificationDao
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationActivity : AppCompatActivity() {
    private var saveRequest: PeriodicWorkRequest? = null
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val notificationDao = NotificationDao(applicationContext)
        val switchNotification = findViewById<Switch>(R.id.switch_notification_activity)
        val notificationEnabled = notificationDao.isEnabled
        switchNotification.isChecked = true && notificationEnabled
        switchNotification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                notificationDao.notificationEnabled(true)
                if (FirebaseAuth.getInstance().currentUser != null) {
                    notificationDao.userId = FirebaseAuth.getInstance().currentUser!!.uid
                }
                startAlarm()
            } else {
                cancelAlarm()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startAlarm() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val delay: Long = when {
            LocalTime.now().hour < 11 -> {
                (11 - LocalTime.now().hour) * 60 + (60 - LocalTime.now().minute).toLong()
            }
            LocalTime.now().hour in 11..12 -> {
                60 - LocalTime.now().minute.toLong()
            }
            LocalTime.now().hour > 12 -> {
                (23 - LocalTime.now().hour) * 60 + (60 - LocalTime.now().minute) + 720.toLong()
            }
            else -> {
                0
            }
        }
        saveRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(this)
                .enqueue(saveRequest!!)
        saveId()
    }

    private fun cancelAlarm() {
        val notificationDao = NotificationDao(applicationContext)
        notificationDao.notificationEnabled(false)
        if (loadId() != null && loadId()!!.isNotEmpty()) {
            WorkManager.getInstance(this).cancelWorkById(UUID.fromString(loadId()))
        }
    }

    @SuppressLint("RestrictedApi")
    fun saveId() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ID, saveRequest!!.stringId)
        editor.apply()
    }

    private fun loadId(): String? {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(ID, "")
    }

    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val ID = "id"
    }
}