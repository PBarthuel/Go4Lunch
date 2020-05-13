package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import org.threeten.bp.LocalDate;

import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class RestaurantDetailViewModel extends ViewModel {

    private LiveData<RestaurantDetailInfo> liveDataResultDetail;
    private String id;
    private PlaceDetailRepository placeDetailRepository;
    private FirebaseAuth mAuth;
    private RestaurantRepository mRestaurantRepository;
    private UserRepository mUserRepository;


    public void init(String id) {

        Log.d("courgette",
                "init() called with: id = [" + id + "]");
        this.id = id;

        liveDataResultDetail = Transformations.map(
                placeDetailRepository.getDetailForRestaurantId(id),
                detail -> map(detail.getResultDetail())
        );
    }

    public RestaurantDetailViewModel(PlaceDetailRepository placeDetailRepository,
                                     FirebaseAuth firebaseAuth,
                                     RestaurantRepository restaurantRepository,
                                     UserRepository userRepository) {

        this.placeDetailRepository = placeDetailRepository;
        this.mAuth = firebaseAuth;
        this.mRestaurantRepository = restaurantRepository;
        this.mUserRepository = userRepository;
    }

    private RestaurantDetailInfo map(ResultDetail result) {

        String name = result.getName();

        String address = result.getFormattedAddress();

        String photoReference = result.getPhotos().get(0).getPhotoReference();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .path("maps/api/place/photo")
                .appendQueryParameter("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs")
                .appendQueryParameter("photoreference", photoReference)
                .appendQueryParameter("maxwidth", "1080")
                .build();

        String id = result.getPlaceId();

        String phoneNumber = result.getFormattedPhoneNumber();

        String url = result.getUrl();

        return new RestaurantDetailInfo(name,
                address,
                uri.toString(),
                id,
                phoneNumber,
                url);
    }

    public LiveData<RestaurantDetailInfo> getLiveDataResultDetail() {
        return liveDataResultDetail;
    }

    public void goToRestaurant() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            mRestaurantRepository.deleteUserToRestaurant(mAuth.getCurrentUser().getUid());
            mRestaurantRepository.addUserToRestaurant(id,
                    mAuth.getCurrentUser().getUid());

        }
    }
}
