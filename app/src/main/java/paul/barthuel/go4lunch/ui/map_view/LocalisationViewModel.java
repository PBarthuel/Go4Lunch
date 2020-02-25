package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import paul.barthuel.go4lunch.ActualLocationRepository;

public class LocalisationViewModel extends ViewModel {

    private ActualLocationRepository actualLocationRepository;
    private MediatorLiveData<LunchMarker> mediatorLiveDataLunchMarker = new MediatorLiveData<>();

    LiveData<LunchMarker> getUiModelsLiveData() {
        return mediatorLiveDataLunchMarker;
    }

    private boolean isMapReady;
    private boolean hasLocationPermissions;

    public LocalisationViewModel(ActualLocationRepository repository) {

        actualLocationRepository = repository;

        mediatorLiveDataLunchMarker.addSource(repository.getLocationLiveData(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mediatorLiveDataLunchMarker.setValue(new LunchMarker(location.getLatitude(), location.getLongitude()));
            }
        });
    }

    private void enableGps() {
        if (isMapReady && hasLocationPermissions) {
            actualLocationRepository.initLocation();
        }
    }

    public void onMapReady() {
        isMapReady = true;

        enableGps();
    }

    public void hasPermissions(boolean hasLocationPermissions) {
        this.hasLocationPermissions = hasLocationPermissions;
    }
}