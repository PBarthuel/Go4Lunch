package paul.barthuel.go4lunch.data.retrofit;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutocompleteRepository {

    public LiveData<Autocomplete> getAutocompleteForLocation(String userInput, Location location) {
        final MutableLiveData<Autocomplete> liveData = new MutableLiveData<>();
        RetrofitService.getInstance().getGooglePlacesAPI().getAutocompleteSearch(userInput,
                location.getLatitude() + "," + location.getLongitude(),
                500,
                "strictbounds",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                .enqueue(new Callback<Autocomplete>() {
                    @Override
                    public void onResponse(Call<Autocomplete> call, Response<Autocomplete> response) {
                        if (response.body() != null) {
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Autocomplete> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

        return liveData;
    }
}
