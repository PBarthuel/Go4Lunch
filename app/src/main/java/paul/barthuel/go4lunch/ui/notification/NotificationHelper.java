package paul.barthuel.go4lunch.ui.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import paul.barthuel.go4lunch.R;

public class NotificationHelper {

    private static final String channelID = "channelID";
    private static final String channelName = "Channel Name";
    private final Context context;

    private NotificationManager mManager;

    NotificationHelper(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    void displayNotification(String message) {
        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setContentTitle("Time to lunch !")
                .setSmallIcon(R.drawable.ic_baseline_emoji_people_24)
                .setContentText(message)
                .build();
        getManager().notify(0, notification);
    }
}
