package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyAPIRepository;

public class LocalisationViewModel extends ViewModel {

    private ActualLocationRepository actualLocationRepository;
    private LiveData<List<LunchMarker>> liveDataLunchMarker;

    LiveData<List<LunchMarker>> getUiModelsLiveData() {
        return liveDataLunchMarker;
    }

    private boolean isMapReady;
    private boolean hasLocationPermissions;

    public LocalisationViewModel(final ActualLocationRepository repository,
                                 final NearbyAPIRepository nearbyRepository) {

        actualLocationRepository = repository;

        liveDataLunchMarker = Transformations.map(
                Transformations.switchMap(
                        repository.getLocationLiveData(),
                        new Function<Location, LiveData<NearbyResponse>>() {
                            @Override
                            public LiveData<NearbyResponse> apply(Location location) {
                                return nearbyRepository.getNearbyForLocation(location);
                            }
                        }), new Function<NearbyResponse, List<LunchMarker>>() {
                    @Override
                    public List<LunchMarker> apply(NearbyResponse nearbyResponse) {
                        return map(nearbyResponse.getResults());
                    }
                });
    }

    private List<LunchMarker> map(
            List<Result> results) {

        List<LunchMarker> lunchMarkers = new ArrayList<>();

        for (Result result : results) {

            double latitude = result.getGeometry().getLocation().getLat();
            double longitude = result.getGeometry().getLocation().getLng();
            String name = result.getName();

            lunchMarkers.add(new LunchMarker(latitude, longitude, name));
        }
        return lunchMarkers;
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
        enableGps();
    }
}