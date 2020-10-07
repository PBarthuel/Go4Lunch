package paul.barthuel.go4lunch.ui.map_view;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.model.nearby.Geometry;
import paul.barthuel.go4lunch.data.model.nearby.NearbyLocation;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Photo;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class LocalisationViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<NearbyResponse> nearbyResponseLiveData;

    @Mock
    ActualLocationRepository actualLocationRepository;

    @Mock
    NearbyRepository nearbyRepository;

    private LocalisationViewModel localisationViewModel;

    @Before
    public void setup() {
        actualLocationRepository = Mockito.mock(ActualLocationRepository.class);
        nearbyRepository = Mockito.mock(NearbyRepository.class);

        locationLiveData = new MutableLiveData<>();
        nearbyResponseLiveData = new MutableLiveData<>();

        Mockito.doReturn(locationLiveData).when(actualLocationRepository).getLocationLiveData();
        Mockito.doReturn(nearbyResponseLiveData).when(nearbyRepository).getNearbyForLocation(any());

        localisationViewModel = new LocalisationViewModel(actualLocationRepository, nearbyRepository);
    }

    @Test
    public void shouldMapCorrectlyLunchMarker() throws InterruptedException {
        //Given
        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        locationLiveData.setValue(location);

        nearbyResponseLiveData.setValue(getNearbyResponses());

        //When
        List<LunchMarker> lunchMarker = LiveDataTestUtil.getOrAwaitValue(localisationViewModel.getUiModelsLiveData(), 1);

        //Then
        assertEquals(2, lunchMarker.size());
    }

    private NearbyResponse getNearbyResponses() {
        Result result = getOneNearby();

        Result result2 = new Result();
        result2.setName("CourgetteBar");
        result2.setPlaceId("ChIJI5HJsx9u5kcRJl41efCbOAw");
        result2.setVicinity("27 Rue Saint-Martin, Paris");
        result2.setRating(3.50);

        Geometry geometry2 = getGeometry();
        result2.setGeometry(geometry2);

        List<Photo> photos2 = getPhoto();
        result2.setPhotos(photos2);


        List<Result> results = new ArrayList<>();
        results.add(result);
        results.add(result2);

        NearbyResponse nearbyResponse = new NearbyResponse();
        nearbyResponse.setResults(results);

        return nearbyResponse;
    }

    private Result getOneNearby() {
        Result result = new Result();
        result.setName("Benoit Paris");
        result.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        result.setVicinity("20 Rue Saint-Martin, Paris");
        result.setRating(3.00);

        Geometry geometry = getGeometry();
        result.setGeometry(geometry);

        List<Photo> photos = getPhoto();
        result.setPhotos(photos);
        return result;
    }

    private Geometry getGeometry() {
        NearbyLocation nearbyLocation = new NearbyLocation();

        nearbyLocation.setLat(48.85838);
        nearbyLocation.setLng(2.350088);

        Geometry geometry = new Geometry();
        geometry.setLocation(nearbyLocation);

        return geometry;
    }

    private List<Photo> getPhoto() {
        Photo photo = new Photo();
        photo.setPhotoReference("CmRaAAAASEJVUvMmuz3Ieqd7cGd2sFyhjxbGt7QjB2ZBw0ynqjtXE_a_sVsqhPP66HpebT4Tdao0eWxjzuLn2uA8In9rn7aCVpbhytOnBY-_Ic2C0O_HSG9zpi7xexOFtwGX8OGtEhCjkSZJQxwVaStqvhMIuZnaGhRbYVPkt5I4ZOKuib4njolRQRpoVQ");

        List<Photo> photos = new ArrayList<>();
        photos.add(0, photo);

        return photos;
    }
}
