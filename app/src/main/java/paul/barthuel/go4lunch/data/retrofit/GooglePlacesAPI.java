package paul.barthuel.go4lunch.data.retrofit;

import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlacesAPI {

    @GET("maps/api/place/nearbysearch/")
    Call<NearbyResponse> nearbySearch (@Query("location") String location,
                                       @Query("radius") String radius);

}
