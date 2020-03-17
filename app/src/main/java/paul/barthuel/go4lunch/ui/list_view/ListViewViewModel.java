package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyAPIRepository;

public class ListViewViewModel extends ViewModel {

    private ActualLocationRepository actualLocationRepository;
    private LiveData<List<RestaurantInfo>> liveDataRestaurantInfo;

    public ListViewViewModel(final ActualLocationRepository repository,
                             final NearbyAPIRepository nearbyRepository) {

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

            restaurantInfo.add();
        }
        return restaurantInfo;
    }
}