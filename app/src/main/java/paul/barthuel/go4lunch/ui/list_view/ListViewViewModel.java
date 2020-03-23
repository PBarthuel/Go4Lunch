package paul.barthuel.go4lunch.ui.list_view;

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
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;

public class ListViewViewModel extends ViewModel {

    private ActualLocationRepository actualLocationRepository;
    private LiveData<List<RestaurantInfo>> liveDataRestaurantInfo;

    LiveData<List<RestaurantInfo>> getUiModelsLiveData() {
        return liveDataRestaurantInfo;
    }

    public ListViewViewModel(final ActualLocationRepository repository,
                             final NearbyRepository nearbyRepository) {

        actualLocationRepository = repository;

        liveDataRestaurantInfo = Transformations.map(
                Transformations.switchMap(
                        repository.getLocationLiveData(),
                        new Function<Location, LiveData<NearbyResponse>>() {
                            @Override
                            public LiveData<NearbyResponse> apply(Location location) {
                                return nearbyRepository.getNearbyForLocation(location);
                            }
                        }), new Function<NearbyResponse, List<RestaurantInfo>>() {
                    @Override
                    public List<RestaurantInfo> apply(NearbyResponse nearbyResponse) {
                        return map(nearbyResponse.getResults());
                    }
                });
    }

    private List<RestaurantInfo> map(
            List<Result> results) {

        List<RestaurantInfo> restaurantInfo = new ArrayList<>();

        for (Result result : results) {

            String name = result.getName();
            String address = result.getVicinity();
            String openingHours;
            if (result.getOpeningHours() == null) {
                openingHours = "unknown";
            } else {
                openingHours = result.getOpeningHours().toString();
            }
            String distance = "" + distance(result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng(),
                    actualLocationRepository.getLocationLiveData().getValue().getLatitude(),
                    actualLocationRepository.getLocationLiveData().getValue().getLongitude());//TODO REFAIRE AVEC MEDIATORLIVEDATA
            String rating = result.getReference();
            String image = result.getIcon();

            restaurantInfo.add(new RestaurantInfo(name,
                    address,
                    openingHours,
                    distance,
                    rating,
                    image));
        }
        return restaurantInfo;
    }

    private int distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344 * 1000;
            return (int) dist;
        }
    }
}