package paul.barthuel.go4lunch.data.retrofit

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyRepository {

    fun getNearbyForLocation(location: Location): LiveData<NearbyResponse> {
        val liveData = MutableLiveData<NearbyResponse>()
        RetrofitService.instance?.googlePlacesAPI?.getNearbySearch(
                location.latitude.toString() + "," + location.longitude,
                500,
                "restaurant",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                ?.enqueue(object : Callback<NearbyResponse?> {
                    override fun onResponse(call: Call<NearbyResponse?>, response: Response<NearbyResponse?>) {
                        if (response.body() != null) {
                            liveData.postValue(response.body())
                        }
                    }

                    override fun onFailure(call: Call<NearbyResponse?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        return liveData
    }



    fun getNearby(location: Location): Observable<NearbyResponse> {
        return RetrofitService.instance!!.googlePlacesAPI.getNearbySearchRx(
                location.latitude.toString() + "," + location.longitude,
                500,
                "restaurant",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
    }
}