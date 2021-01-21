package paul.barthuel.go4lunch.data.retrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import paul.barthuel.go4lunch.data.model.detail.Detail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceDetailRepository {
    fun getDetailForRestaurantId(id: String?): LiveData<Detail> {
        val liveData = MutableLiveData<Detail>()
        RetrofitService.instance?.googlePlacesAPI?.getDetailSearch(
                id,
                "address_components,adr_address,formatted_address,icon,name,photo,url,vicinity,formatted_phone_number,opening_hours,rating,place_id",
                "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                ?.enqueue(object : Callback<Detail?> {
                    override fun onResponse(call: Call<Detail?>, response: Response<Detail?>) {
                        if (response.body() != null) {
                            liveData.postValue(response.body())
                        }
                    }

                    override fun onFailure(call: Call<Detail?>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        return liveData
    }
}