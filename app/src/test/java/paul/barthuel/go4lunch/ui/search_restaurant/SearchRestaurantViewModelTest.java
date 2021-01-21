package paul.barthuel.go4lunch.ui.search_restaurant;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;
import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.autocomplet.Autocomplete;
import paul.barthuel.go4lunch.data.model.autocomplet.Prediction;
import paul.barthuel.go4lunch.data.model.autocomplet.StructuredFormatting;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;

import static org.junit.Assert.*;

public class SearchRestaurantViewModelTest {
    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    MutableLiveData<Location> locationLiveData;
    MutableLiveData<String> userSearchQueryLiveData;
    MutableLiveData<List<User>> usersLiveData;
    MutableLiveData<Autocomplete> autocompleteLiveData;

    @Mock
    AutocompleteRepository autocompleteRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserSearchRepository userSearchRepository;

    @Mock
    ActualLocationRepository actualLocationRepository;

    private SearchRestaurantViewModel searchRestaurantViewModel;

    @Before
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        actualLocationRepository = Mockito.mock(ActualLocationRepository.class);
        autocompleteRepository = Mockito.mock(AutocompleteRepository.class);
        userSearchRepository = Mockito.mock(UserSearchRepository.class);

        locationLiveData = new MutableLiveData<>();
        userSearchQueryLiveData =  new MutableLiveData<>();
        usersLiveData = new MutableLiveData<>();
        autocompleteLiveData = new MutableLiveData<>();

        Mockito.doReturn(locationLiveData).when(actualLocationRepository).getLocationLiveData();
        Mockito.doReturn(userSearchQueryLiveData).when(userSearchRepository).getUserSearchQueryLiveData();
        Mockito.doReturn(usersLiveData).when(userRepository).getTodayUser("todayuser");
        Mockito.doReturn(autocompleteLiveData).when(autocompleteRepository).getAutocompleteForLocation(any(), any());

        searchRestaurantViewModel = new SearchRestaurantViewModel(autocompleteRepository,
                actualLocationRepository,
                userRepository,
                userSearchRepository);

        }

    @Test
    public void shouldMapCorrectlyLunchMarker() throws InterruptedException {
        //Given
        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        locationLiveData.setValue(location);

        userSearchQueryLiveData.setValue("Benoit Pa");

        List<User> users = new ArrayList<>();
        users.add(new User("14H2Qd18àe14é",
                "courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg"));
        usersLiveData.setValue(users);

        autocompleteLiveData.setValue(getAutocomplete());

        //When
        List<String> predictions = LiveDataTestUtil.getOrAwaitValue(searchRestaurantViewModel.uiModelsMediatorLiveData, 1);

        //Then
        assertEquals("Benoit Paris", predictions.get(0));
    }

    @Test
    public void shouldMapCorrectlyLunchMarker2() throws InterruptedException {
        //Given
        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        locationLiveData.setValue(location);

        userSearchQueryLiveData.setValue("");

        List<User> users = new ArrayList<>();
        users.add(new User("14H2Qd18àe14é",
                "courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg"));
        usersLiveData.setValue(users);

        autocompleteLiveData.setValue(getAutocomplete());

        //When
        List<String> predictions = LiveDataTestUtil.getOrAwaitValue(searchRestaurantViewModel.uiModelsMediatorLiveData, 1);

        //Then
    }

    private Autocomplete getAutocomplete() {
        Autocomplete autocomplete = new Autocomplete();

        autocomplete.setPredictions(getPredictions());
        return autocomplete;
    }

    private List<Prediction> getPredictions() {
        List<Prediction> predictions = new ArrayList<>();

        Prediction prediction1 = new Prediction();
        StructuredFormatting structuredFormatting1 = new StructuredFormatting();
        structuredFormatting1.setMainText("Benoit Paris");
        prediction1.setStructuredFormatting(structuredFormatting1);

        predictions.add(0, prediction1);

        return predictions;
    }

}
