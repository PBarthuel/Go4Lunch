package paul.barthuel.go4lunch.ui.map_view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.MapView;

public class LocalisationViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<MapView> mMapView;

    public LocalisationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        mMapView = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}