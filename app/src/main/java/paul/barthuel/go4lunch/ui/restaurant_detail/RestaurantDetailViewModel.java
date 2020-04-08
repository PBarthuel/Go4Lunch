package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.net.Uri;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class RestaurantDetailViewModel extends ViewModel {

    private LiveData<RestaurantDetailInfo> liveDataResultDetail;
    private String id;
    private PlaceDetailRepository placeDetailRepository;

    public void init(String id) {

        Log.d("courgette",
                "init() called with: id = [" + id + "]");
        this.id = id;

        liveDataResultDetail = Transformations.map(
                placeDetailRepository.getDetailForRestaurantId(id),
                new Function<Detail, RestaurantDetailInfo>() {
                    @Override
                    public RestaurantDetailInfo apply(Detail detail) {
                        return map(detail.getResultDetail());
                    }
                }
        );
    }

    // TODO: Implement the ViewModel
    public RestaurantDetailViewModel(PlaceDetailRepository placeDetailRepository) {

        this.placeDetailRepository = placeDetailRepository;

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
}
