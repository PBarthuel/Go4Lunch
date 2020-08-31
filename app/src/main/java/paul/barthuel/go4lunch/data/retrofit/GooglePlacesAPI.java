package paul.barthuel.go4lunch.data.retrofit;

import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/json")
    Call<NearbyResponse> getNearbySearch(@Query("location") String location,
                                         @Query("radius") int radius,
                                         @Query("type") String type,
                                         @Query("key") String key);

    @GET("maps/api/place/details/json")
    Call<Detail> getDetailSearch(@Query("place_id") String id,
                                 @Query("fields") String fields,
                                 @Query("key") String key);

    @GET("maps/api/place/autocomplete/json")
    Call<Autocomplete> getAutocompleteSearch(@Query("input") String userInput,
                                             @Query("location") String location,
                                             @Query("radius") int radius,
                                             String strictbounds,
                                             @Query("key") String key);
}
