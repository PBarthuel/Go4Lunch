package paul.barthuel.go4lunch.data.retrofit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import paul.barthuel.go4lunch.data.model.detail.Detail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailRepository {

    public LiveData<Detail> getDetailForRestaurantId(String id) {
        final MutableLiveData<Detail> liveData = new MutableLiveData<>();
        RetrofitService.getInstance().getGooglePlacesAPI().getDetailSearch(
                id,
                "address_components,adr_address,formatted_address,icon,name,photo,url,vicinity,formatted_phone_number,opening_hours,rating,place_id",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                .enqueue(new Callback<Detail>() {
                    @Override
                    public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {
                        if (response.body() != null) {
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });

        return liveData;
    }
}
