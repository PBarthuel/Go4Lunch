package paul.barthuel.go4lunch.injections;

import android.text.style.UpdateLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.ListViewViewModel;
import paul.barthuel.go4lunch.ui.map_view.LocalisationViewModel;
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    @NonNull
    private final ActualLocationRepository actualLocationRepository;
    @NonNull
    private NearbyRepository nearbyRepository;
    @NonNull
    private PlaceDetailRepository placeDetailRepository;

    private ViewModelFactory(
            @NonNull ActualLocationRepository actualLocationRepository,
            @NonNull NearbyRepository nearbyRepository,
            @NonNull PlaceDetailRepository placeDetailRepository
    ) {
        this.actualLocationRepository = actualLocationRepository;
        this.nearbyRepository = nearbyRepository;
        this.placeDetailRepository = placeDetailRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new ActualLocationRepository(),
                            new NearbyRepository(),
                            new PlaceDetailRepository()
                    );
                }
            }
        }

        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocalisationViewModel.class)) {
            return (T) new LocalisationViewModel(
                    actualLocationRepository,
                    nearbyRepository
            );
        }else if (modelClass.isAssignableFrom(ListViewViewModel.class)) {
            return (T) new ListViewViewModel(
                    actualLocationRepository,
                    nearbyRepository,
                    placeDetailRepository);
        }else if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(
                    placeDetailRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
