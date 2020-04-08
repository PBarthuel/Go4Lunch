package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

public class RestaurantDetailViewModel extends ViewModel {

    private LiveData<List<ResultDetail>> liveDataResultDetail;
    private String id;

    public void init(String id) {
        Log.d("courgette",
                "init() called with: id = [" + id + "]");
        this.id = id;
    }
    // TODO: Implement the ViewModel
    public RestaurantDetailViewModel (PlaceDetailRepository placeDetailRepository) {

    }
}
