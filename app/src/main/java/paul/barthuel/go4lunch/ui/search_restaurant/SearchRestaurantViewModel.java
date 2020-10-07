package paul.barthuel.go4lunch.ui.search_restaurant;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;

public class SearchRestaurantViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private final ActualLocationRepository actualLocationRepository;
    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;

    private final MutableLiveData<String> userSearchQueryLiveData = new MutableLiveData<>();

    private final MediatorLiveData<List<String>> predictionMediatorLiveData = new MediatorLiveData<>();

    public MediatorLiveData<List<String>> getPredictionMediatorLiveData() {
        return predictionMediatorLiveData;
    }

    public SearchRestaurantViewModel(final AutocompleteRepository autocompleteRepository,
                                     final ActualLocationRepository actualLocationRepository,
                                     final UserRepository userRepository,
                                     final UserSearchRepository userSearchRepository) {

        this.actualLocationRepository = actualLocationRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;

        LiveData<Location> locationLiveData = actualLocationRepository.getLocationLiveData();

        predictionMediatorLiveData.addSource(locationLiveData, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                combineForUserSearchQuery(userSearchQueryLiveData.getValue(),
                        location);
            }
        });
        predictionMediatorLiveData.addSource(userSearchQueryLiveData, new Observer<String>() {
            @Override
            public void onChanged(String userSearchQuery) {
                combineForUserSearchQuery(userSearchQuery,
                        locationLiveData.getValue());
            }
        });
    }

    private void combineForUserSearchQuery(@Nullable String userSearchQuery, @Nullable Location location) {
        if(userSearchQuery == null || location == null) {
            return;
        }

        predictionMediatorLiveData.addSource(autocompleteRepository.getAutocompleteForLocation(userSearchQuery, location),
                new Observer<Autocomplete>() {
            @Override
            public void onChanged(Autocomplete autocomplete) {
                List<String> autocompleteResults =  new ArrayList<>();

                if(autocomplete != null && autocomplete.getPredictions() != null) {
                    for (Prediction prediction : autocomplete.getPredictions()) {
                        autocompleteResults.add(prediction.getStructuredFormatting().getMainText());
                    }
                }
                predictionMediatorLiveData.setValue(autocompleteResults);
            }
        });
    }

    public LiveData<TodayUser> getCurrentTodayUserLiveData() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return userRepository.getTodayUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else {
            return null;
        }
    }

    public void onSearchQueryChange(String newText) {
        userSearchRepository.updateSearchQuery(newText);
        userSearchQueryLiveData.setValue(newText);
    }
}

