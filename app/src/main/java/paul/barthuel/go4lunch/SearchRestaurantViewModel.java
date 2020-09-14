package paul.barthuel.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;

public class SearchRestaurantViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private ActualLocationRepository actualLocationRepository;
    private final UserRepository userRepository;

    LiveData<Autocomplete> autocompleteLiveData;

    MediatorLiveData<List<Prediction>> predictionMediatorLiveData = new MediatorLiveData<>();

    LiveData<List<Prediction>> getUiModelsLiveData() {
        return predictionMediatorLiveData;
    }

public SearchRestaurantViewModel(final AutocompleteRepository autocompleteRepository,
                                 final ActualLocationRepository actualLocationRepository,
                                 final UserRepository userRepository) {

        this.actualLocationRepository = actualLocationRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;

        /*autocompleteLiveData = Transformations.switchMap(actualLocationRepository.getLocationLiveData(), new Function<Location, LiveData<Autocomplete>>() {
            @Override
            public LiveData<Autocomplete> apply(Location input) {
                return autocompleteRepository.getAutocompleteForLocation();
            }
        });*/
}

    public LiveData<TodayUser> getCurrentTodayUserLiveData() {
        return userRepository.getTodayUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}

