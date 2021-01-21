package paul.barthuel.go4lunch.data.retrofit

import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete
import paul.barthuel.go4lunch.data.model.detail.Detail
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {
    @GET("maps/api/place/nearbysearch/json")
    fun getNearbySearch(@Query("location") location: String?,
                        @Query("radius") radius: Int,
                        @Query("type") type: String?,
                        @Query("key") key: String?): Call<NearbyResponse>

    @GET("maps/api/place/details/json")
    fun getDetailSearch(@Query("place_id") id: String?,
                        @Query("fields") fields: String?,
                        @Query("key") key: String?): Call<Detail>

    @GET("maps/api/place/autocomplete/json")
    fun getAutocompleteSearch(@Query("input") userInput: String?,
                              @Query("location") location: String?,
                              @Query("radius") radius: Int?,
                              @Query("key") key: String?): Call<Autocomplete>
}