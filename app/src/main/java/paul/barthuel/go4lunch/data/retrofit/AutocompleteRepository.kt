package paul.barthuel.go4lunch.data.retrofit

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutocompleteRepository {
    fun getAutocompleteForLocation(userInput: String, location: Location): LiveData<Autocomplete> {
        val liveData = MutableLiveData<Autocomplete>()
        RetrofitService.instance?.googlePlacesAPI?.getAutocompleteSearch(userInput,
                location.latitude.toString() + "," + location.longitude,
                500,
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                ?.enqueue(object : Callback<Autocomplete?> {
                    override fun onResponse(call: Call<Autocomplete?>, response: Response<Autocomplete?>) {
                        if (response.body() != null) {
                            liveData.postValue(response.body())
                        }
                    }

                    override fun onFailure(call: Call<Autocomplete?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        return liveData
    }
}