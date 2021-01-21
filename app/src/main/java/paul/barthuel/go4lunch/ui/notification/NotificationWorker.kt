package paul.barthuel.go4lunch.ui.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository
import paul.barthuel.go4lunch.data.firestore.user.UserRepository
import paul.barthuel.go4lunch.data.retrofit.RetrofitService
import java.io.IOException

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        val userRepository = UserRepository()
        //PlaceDetailRepository placeDetailRepository = new PlaceDetailRepository();
        val restaurantRepository = RestaurantRepository()
        if (FirebaseAuth.getInstance().currentUser != null) {
            if (userRepository.getTodayUser(FirebaseAuth.getInstance().currentUser!!.uid) != null) {
                val todayUser = userRepository
                        .getTodayUserSync(FirebaseAuth
                                .getInstance()
                                .currentUser
                                ?.uid)
                if (todayUser != null) {
                    val service = RetrofitService.instance?.googlePlacesAPI
                    try {
                        val detail = service?.getDetailSearch(todayUser.placeId,
                                "formatted_address,name",
                                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")?.execute()
                        val detailSync = detail?.body()
                        val uids = restaurantRepository.getUidsSync(todayUser.placeId)
                        val allWorkmatesNames = StringBuilder()
                        if (uids != null) {
                            for (uid in uids) {
                                if (uid.uid != todayUser.userId) {
                                    allWorkmatesNames.append(uid.workmateName)
                                }
                            }
                        }
                        if (detailSync != null) {
                            notificationHelper.displayNotification(detailSync.resultDetail.name +
                                    " " +
                                    detailSync.resultDetail.formattedAddress +
                                    " " +
                                    allWorkmatesNames)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.i("call", "fail")
                        return Result.failure()
                    }
                }
            }
        }
        Log.i("call", "succes")
        return Result.success()
    }
}