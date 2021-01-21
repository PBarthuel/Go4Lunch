package paul.barthuel.go4lunch.ui.notification.data

import android.content.Context
import android.content.SharedPreferences

class NotificationDao(context: Context) {
    private val sharedPreferences: SharedPreferences
    fun notificationEnabled(isEnabled: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isEnabled", isEnabled!!)
        editor.apply()
    }

    val isEnabled: Boolean
        get() = sharedPreferences.getBoolean("isEnabled", false)

    var userId: String?
        get() = sharedPreferences.getString(KEY_USER_ID, null)
        set(userId) {
            sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
        }

    companion object {
        private const val KEY_USER_ID = "KEY_USER_ID"
    }

    init {
        val SHARED_PREFS = "sharedPrefs"
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }
}