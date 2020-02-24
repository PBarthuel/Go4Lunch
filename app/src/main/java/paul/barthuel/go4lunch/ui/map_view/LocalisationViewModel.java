package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import paul.barthuel.go4lunch.ActualLocationRepository;

public class LocalisationViewModel extends ViewModel {

    private ActualLocationRepository actualLocationRepository;
    private MediatorLiveData<LunchMarker> mediatorLiveDataLunchMarker = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> mediatorLiveDataShouldTriggerGps = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mutableLiveDataMapReady = new MutableLiveData<>();
    private MutableLiveData<Boolean> mutableLiveDataLocationPermissions = new MutableLiveData<>();

    LiveData<LunchMarker> getUiModelsLiveData() {
        return mediatorLiveDataLunchMarker;
    }

    public LocalisationViewModel(ActualLocationRepository repository) {

        actualLocationRepository = repository;

        mediatorLiveDataLunchMarker.addSource(repository.getLocationLiveData(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mediatorLiveDataLunchMarker.setValue(new LunchMarker(location.getLatitude(), location.getLongitude()));
            }
        });
        mediatorLiveDataShouldTriggerGps.addSource(mutableLiveDataMapReady, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isMapReady) {
                enableGps(isMapReady, mutableLiveDataLocationPermissions.getValue());
            }
        });
        mediatorLiveDataShouldTriggerGps.addSource(mutableLiveDataLocationPermissions, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hasPermissions) {
                enableGps(mutableLiveDataMapReady.getValue(), hasPermissions);
            }
        });
    }

    private void enableGps(Boolean isMapReady, Boolean locationPermissions) {
        if (isMapReady && locationPermissions) {
            actualLocationRepository.initLocation();
        }
    }

    public void onMapReady() {
        mutableLiveDataMapReady.setValue(true);
    }

    public void hasPermissions(boolean hasLocationPermissions) {
        mutableLiveDataLocationPermissions.setValue(hasLocationPermissions);
    }
}