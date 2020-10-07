package paul.barthuel.go4lunch.ui.notification;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.ui.notification.data.NotificationDao;

public class NotificationActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String ID = "id";

    private PeriodicWorkRequest saveRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        final NotificationDao notificationDao = new NotificationDao(getApplicationContext());

        final Switch switchNotification = findViewById(R.id.switch_notification_activity);

        final Boolean notificationEnabled = notificationDao.isEnabled();

        if (notificationEnabled != null && notificationEnabled) {
            switchNotification.setChecked(true);
        } else {
            switchNotification.setChecked(false);
        }

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                notificationDao.notificationEnabled(true);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    notificationDao.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                startAlarm();
            } else {
                cancelAlarm();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void startAlarm() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        saveRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this)
                .enqueue(saveRequest);

        saveId();
    }

    private void cancelAlarm() {
        NotificationDao notificationDao = new NotificationDao(getApplicationContext());
        notificationDao.notificationEnabled(false);
        if(loadId() != null && !loadId().isEmpty()) {
            WorkManager.getInstance(this).cancelWorkById(UUID.fromString(loadId()));
        }
    }

    @SuppressLint("RestrictedApi")
    public void saveId() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ID, saveRequest.getStringId());

        editor.apply();
    }

    public String loadId() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        return sharedPreferences.getString(ID, "");
    }
}
