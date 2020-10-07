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
import paul.barthuel.go4lunch.data.retrofit.GooglePlacesAPI;
import paul.barthuel.go4lunch.data.retrofit.RetrofitService;
import retrofit2.Response;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

        UserRepository userRepository = new UserRepository();
        //PlaceDetailRepository placeDetailRepository = new PlaceDetailRepository();
        RestaurantRepository restaurantRepository = new RestaurantRepository();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (userRepository.getTodayUser(FirebaseAuth.getInstance().getCurrentUser().getUid()) != null) {
                TodayUser todayUser = userRepository
                        .getTodayUserSync(FirebaseAuth
                                .getInstance()
                                .getCurrentUser()
                                .getUid());
                if (todayUser != null) {
                    GooglePlacesAPI service  = RetrofitService.getInstance().getGooglePlacesAPI();
                    try {
                        Response<Detail> detail = service.getDetailSearch(todayUser.getPlaceId(),
                                "formatted_address,name",
                                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs").execute();
                        Detail detailSync = detail.body();

                        List<Uid> uids = restaurantRepository.getUidsSync(todayUser.getPlaceId());
                        StringBuilder allWorkmatesNames = new StringBuilder();
                        if (uids != null) {
                            for (Uid uid : uids) {
                                if(!uid.getUid().equals(todayUser.getUserId())) {
                                    allWorkmatesNames.append(uid.getWorkmateName());
                                }
                            }
                        }

                        if (detailSync != null) {
                            notificationHelper.displayNotification(detailSync.getResultDetail().getName() +
                                    " " +
                                    detailSync.getResultDetail().getFormattedAddress() +
                                    " " +
                                    allWorkmatesNames);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("call", "fail");
                        return Result.failure();
                    }
                }
            }
        }

        Log.i("call", "succes");
        return Result.success();
    }
}
