package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

import paul.barthuel.go4lunch.ActualLocationRepository;

public class LocalisationViewModel extends ViewModel {

    private MediatorLiveData<LunchMarker> mediatorLiveData = new MediatorLiveData<>();

    LiveData<LunchMarker> getUiModelsLiveData() {
        return mediatorLiveData;
    }

    public LocalisationViewModel(ActualLocationRepository repository) {

        mediatorLiveData.addSource(repository.getLocationLiveData(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mediatorLiveData.setValue(new LunchMarker(location.getLatitude(), location.getLongitude()));
            }
        });
    }
}