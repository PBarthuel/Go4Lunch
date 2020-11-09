package paul.barthuel.go4lunch.ui.search_restaurant;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;

public class SearchRestaurantViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;

    private final MediatorLiveData<List<Prediction>> predictionsMediatorLiveData = new MediatorLiveData<>();

    private final MutableLiveData<String> selectedQueryLiveData = new MutableLiveData<>();

    private final MediatorLiveData<List<String>> uiModelsMediatorLiveData = new MediatorLiveData<>();

    public MediatorLiveData<List<String>> getUiModelsMediatorLiveData() {
        return uiModelsMediatorLiveData;
    }

    public SearchRestaurantViewModel(final AutocompleteRepository autocompleteRepository,
                                     final ActualLocationRepository actualLocationRepository,
                                     final UserRepository userRepository,
                                     final UserSearchRepository userSearchRepository) {

        this.autocompleteRepository = autocompleteRepository;
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;

        LiveData<String> userSearchQueryLiveData = userSearchRepository.getUserSearchQueryLiveData();
        LiveData<Location> locationLiveData = actualLocationRepository.getLocationLiveData();

        predictionsMediatorLiveData.addSource(locationLiveData, location -> combineForPredictions(
                location,
                userSearchQueryLiveData.getValue()));

        uiModelsMediatorLiveData.addSource(userSearchQueryLiveData, userSearchQuery -> combineForUserSearchQuery(
                userSearchQuery,
                predictionsMediatorLiveData.getValue(),
                selectedQueryLiveData.getValue()));
        uiModelsMediatorLiveData.addSource(selectedQueryLiveData, selectedQuery -> combineForUserSearchQuery(
                userSearchQueryLiveData.getValue(),
                predictionsMediatorLiveData.getValue(),
                selectedQuery));
        uiModelsMediatorLiveData.addSource(predictionsMediatorLiveData, predictions -> combineForUserSearchQuery(
                userSearchQueryLiveData.getValue(),
                predictions,
                selectedQueryLiveData.getValue()));
    }

    private void combineForPredictions(Location location, String userSearchQuery) {

        if (location != null && userSearchQuery != null) {
            predictionsMediatorLiveData.addSource(autocompleteRepository.getAutocompleteForLocation(userSearchQuery, location), autocomplete -> predictionsMediatorLiveData.setValue(autocomplete.getPredictions()));
        }

    }

    private void combineForUserSearchQuery(@Nullable String userSearchQuery,
                                           @Nullable List<Prediction> predictions,
                                           @Nullable String selectedQuery) {
        if (userSearchQuery == null || predictions == null) {
            return;
        }

        if (userSearchQuery.equals(selectedQuery) || userSearchQuery.isEmpty()) {
            uiModelsMediatorLiveData.setValue(new ArrayList<>());
            Log.d("courgette", "combineForUserSearchQuery() called with: userSearchQuery = [" + userSearchQuery + "], selectedQuery = [" + selectedQuery + "]");
        } else {
            List<String> autocompleteResults = new ArrayList<>();
            for (Prediction prediction : predictions) {
                autocompleteResults.add(prediction.getStructuredFormatting().getMainText());
            }
            uiModelsMediatorLiveData.setValue(autocompleteResults);
        }
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
    }

    public void onAutocompleteSelected(String selectedText) {
        selectedQueryLiveData.setValue(selectedText);
    }
}

