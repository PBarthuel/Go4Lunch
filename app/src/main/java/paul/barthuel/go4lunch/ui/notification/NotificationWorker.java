package paul.barthuel.go4lunch.ui.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class NotificationWorker extends Worker {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private PlaceDetailRepository placeDetailRepository;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

        userRepository = new UserRepository();
        placeDetailRepository = new PlaceDetailRepository();
        restaurantRepository =  new RestaurantRepository();

        if (userRepository.getTodayUser(FirebaseAuth.getInstance().getCurrentUser().getUid()) != null) {
            TodayUser todayUser = userRepository.getTodayUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue();
            if (todayUser != null) {
                Detail detail = placeDetailRepository.getDetailForRestaurantId(todayUser.getPlaceId()).getValue();
                List<Uid> uids = restaurantRepository.getUidsFromRestaurant(todayUser.getPlaceId()).getValue();
                StringBuilder allWorkmatesNames = new StringBuilder();
                if (uids != null) {
                    for (Uid uid : uids) {
                        allWorkmatesNames.append(uid.getWorkmateName());
                    }
                }
                if (detail != null) {
                    notificationHelper.displayNotification(detail.getResultDetail().getName() +
                            " " +
                            detail.getResultDetail().getFormattedAddress() +
                            " " +
                            allWorkmatesNames);
                }
            }
        }

        Log.i("call", "succes");
        return Result.success();

    }
}
