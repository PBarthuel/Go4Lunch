package paul.barthuel.go4lunch;

import android.location.Location;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;
import paul.barthuel.go4lunch.ui.list_view.RestaurantInfo;

public class SearchRestaurantViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private ActualLocationRepository actualLocationRepository;

    LiveData<Autocomplete> autocompleteLiveData;

    MediatorLiveData<List<Prediction>> predictionMediatorLiveData = new MediatorLiveData<>();

    LiveData<List<Prediction>> getUiModelsLiveData() {
        return predictionMediatorLiveData;
    }

public SearchRestaurantViewModel(final AutocompleteRepository autocompleteRepository,
                                 final ActualLocationRepository actualLocationRepository) {

        this.actualLocationRepository = actualLocationRepository;
        this.autocompleteRepository = autocompleteRepository;

        /*autocompleteLiveData = Transformations.switchMap(actualLocationRepository.getLocationLiveData(), new Function<Location, LiveData<Autocomplete>>() {
            @Override
            public LiveData<Autocomplete> apply(Location input) {
                return autocompleteRepository.getAutocompleteForLocation();
            }
        });*/
    }

}
