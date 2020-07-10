package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

import static org.mockito.ArgumentMatchers.any;

public class RestaurantDetailViewModelTest {


    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<NearbyResponse> nearbyReponseLiveData;
    private MutableLiveData<Detail> detailLiveData;
    private MutableLiveData<Location> locationLiveData;

    @Mock
    ActualLocationRepository actualLocationRepository;

    @Mock
    NearbyRepository nearbyRepository;

    @Mock
    PlaceDetailRepository placeDetailRepository;

    @Mock
    UriBuilder uriBuilder;

    private ListViewViewModel listViewViewModel;

    @Before
    public void setup() {
        actualLocationRepository = Mockito.mock(ActualLocationRepository.class);
        nearbyRepository = Mockito.mock(NearbyRepository.class);
        placeDetailRepository = Mockito.mock(PlaceDetailRepository.class);
        uriBuilder = Mockito.mock(UriBuilder.class);

        nearbyReponseLiveData = new MutableLiveData<>();
        detailLiveData = new MutableLiveData<>();
        locationLiveData = new MutableLiveData<>();

        Mockito.doReturn(locationLiveData).when(actualLocationRepository).getLocationLiveData();
        Mockito.doReturn(nearbyReponseLiveData).when(nearbyRepository).getNearbyForLocation(any());
        Mockito.doReturn(detailLiveData).when(placeDetailRepository).getDetailForRestaurantId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");

        //listViewViewModel = new ListViewViewModel(actualLocationRepository, nearbyRepository, placeDetailRepository, uriBuilder);
    }

    @Test
    public void shouldMapCorrectlyOneNearbywithOneDetail() throws InterruptedException {
        //Given


        // When


        // Then

    }
}
