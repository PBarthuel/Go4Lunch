package paul.barthuel.go4lunch.data.retrofit;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyRepository {

    public LiveData<NearbyResponse> getNearbyForLocation(Location location) {
        final MutableLiveData<NearbyResponse> liveData = new MutableLiveData<>();
        RetrofitService.getInstance().getGooglePlacesAPI().getNearbySearch(
                location.getLatitude() + "," + location.getLongitude(),
                500,
                "restaurant",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                .enqueue(new Callback<NearbyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NearbyResponse> call, @NonNull Response<NearbyResponse> response) {
                        if (response.body() != null) {
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NearbyResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });

        return liveData;
    }
}
