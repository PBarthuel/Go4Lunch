package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;

public class LocalisationViewModel extends ViewModel {

    private final ActualLocationRepository actualLocationRepository;
    private final UserSearchRepository userSearchRepository;

    private final MediatorLiveData<List<LunchMarker>> liveDataLunchMarker = new MediatorLiveData<>();

    private final LiveData<NearbyResponse> liveDataNearby;

    LiveData<List<LunchMarker>> getUiModelsLiveData() {
        return liveDataLunchMarker;
    }

    private boolean isMapReady;
    private boolean hasLocationPermissions;

    public LocalisationViewModel(final ActualLocationRepository actualLocationRepository,
                                 final NearbyRepository nearbyRepository,
                                 final UserSearchRepository userSearchRepository) {

        this.actualLocationRepository = actualLocationRepository;
        this.userSearchRepository = userSearchRepository;

        LiveData<Location> locationLiveData = actualLocationRepository.getLocationLiveData();
        LiveData<String> userSearchQueryLiveData = userSearchRepository.getUserSearchQueryLiveData();

        liveDataNearby = Transformations.switchMap(
                locationLiveData,
                nearbyRepository::getNearbyForLocation);

        liveDataLunchMarker.addSource(liveDataNearby, new Observer<NearbyResponse>() {
            @Override
            public void onChanged(NearbyResponse nearbyResponse) {
                map(nearbyResponse, userSearchQueryLiveData.getValue());
            }
        });
        liveDataLunchMarker.addSource(userSearchQueryLiveData, new Observer<String>() {
            @Override
            public void onChanged(String userSearchQuery) {
                map(liveDataNearby.getValue(), userSearchQuery);
            }
        });
    }

    private void map(
            @Nullable NearbyResponse nearbyResponse,
            @Nullable String userSearchQuery) {

        List<LunchMarker> lunchMarkers = new ArrayList<>();

        if (nearbyResponse != null && nearbyResponse.getResults() != null) {
            for (Result result : nearbyResponse.getResults()) {

                double latitude = result.getGeometry().getLocation().getLat();
                double longitude = result.getGeometry().getLocation().getLng();
                String name = result.getName();

                int backGroundColor;

                //TODO mettre les bonnes couleur pour les marker
                if (userSearchQuery != null && name.startsWith(userSearchQuery)) {
                    backGroundColor = R.color.selected_background_color;
                }else {
                    backGroundColor = android.R.color.white;
                }

                lunchMarkers.add(new LunchMarker(latitude, longitude, name, backGroundColor));
            }
        }
        liveDataLunchMarker.setValue(lunchMarkers);
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