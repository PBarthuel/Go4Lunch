package paul.barthuel.go4lunch.ui.notification.data;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationDao {

    private SharedPreferences sharedPreferences;
    private static String SHARED_PREFS = "sharedPrefs";
    private static final String KEY_USER_ID = "KEY_USER_ID";

    public NotificationDao(Context context) {

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void notificationEnabled (Boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isEnabled", isEnabled);

        editor.apply();
    }

    public Boolean isEnabled() {
        return sharedPreferences.getBoolean("isEnabled", false);
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }
}
