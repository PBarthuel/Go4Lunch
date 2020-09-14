package paul.barthuel.go4lunch.ui.list_view;

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

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.model.nearby.Geometry;
import paul.barthuel.go4lunch.data.model.nearby.NearbyLocation;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.model.nearby.Photo;
import paul.barthuel.go4lunch.data.model.nearby.Result;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ListViewViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<NearbyResponse> nearbyReponseLiveData;
    private MutableLiveData<Detail> detailLiveData;
    private MutableLiveData<Detail> detailLiveData2;
    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<Integer> attendiesLiveData;
    private MutableLiveData<Integer> attendies2LiveData;

    @Mock
    ActualLocationRepository actualLocationRepository;

    @Mock
    NearbyRepository nearbyRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    PlaceDetailRepository placeDetailRepository;

    @Mock
    UriBuilder uriBuilder;

    private ListViewViewModel listViewViewModel;

    @Before
    public void setup() {
        actualLocationRepository = Mockito.mock(ActualLocationRepository.class);
        nearbyRepository = Mockito.mock(NearbyRepository.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        placeDetailRepository = Mockito.mock(PlaceDetailRepository.class);
        uriBuilder = Mockito.mock(UriBuilder.class);

        nearbyReponseLiveData = new MutableLiveData<>();
        detailLiveData = new MutableLiveData<>();
        detailLiveData2 = new MutableLiveData<>();
        locationLiveData = new MutableLiveData<>();
        attendiesLiveData = new MutableLiveData<>();
        attendies2LiveData = new MutableLiveData<>();

        Mockito.doReturn(locationLiveData).when(actualLocationRepository).getLocationLiveData();
        Mockito.doReturn(nearbyReponseLiveData).when(nearbyRepository).getNearbyForLocation(any());
        Mockito.doReturn(detailLiveData).when(placeDetailRepository).getDetailForRestaurantId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn(detailLiveData2).when(placeDetailRepository).getDetailForRestaurantId("ChIJI5HJsx9u5kcRJl41efCbOAw");
        Mockito.doReturn(attendiesLiveData).when(restaurantRepository).getRestaurantAttendies("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn(attendies2LiveData).when(restaurantRepository).getRestaurantAttendies("ChIJI5HJsx9u5kcRJl41efCbOAw");
        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());

        listViewViewModel = new ListViewViewModel(actualLocationRepository,
                nearbyRepository,
                placeDetailRepository,
                restaurantRepository,
                uriBuilder);
    }

    @Test
    public void shouldMapCorrectlyOneNearbywithOneDetail() throws InterruptedException {
        //Given
        List<Result> results = new ArrayList<>();
        results.add(getOneNearby());
        NearbyResponse nearbyResponse = new NearbyResponse();
        nearbyResponse.setResults(results);
        nearbyReponseLiveData.setValue(nearbyResponse);

        Detail detail = getRestaurantDetail();
        detailLiveData.setValue(detail);

        Integer attendies = 1;
        attendiesLiveData.setValue(attendies);

        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        locationLiveData.setValue(location);

        //When
        List<RestaurantInfo> restaurantInfos = LiveDataTestUtil.getOrAwaitValue(listViewViewModel.getUiModelsLiveData(), 1);

        //Then
        assertEquals(1, restaurantInfos.size());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", restaurantInfos.get(0).getId());
        assertEquals("Benoit Paris", restaurantInfos.get(0).getName());
        assertEquals("courgette", restaurantInfos.get(0).getImage());
        assertEquals("20 Rue Saint-Martin, Paris", restaurantInfos.get(0).getAddress());
        assertEquals(new Double(2.00), restaurantInfos.get(0).getRating());
        assertEquals("5437222m", restaurantInfos.get(0).getDistance());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", restaurantInfos.get(0).getId());
    }

    @Test
    public void shouldMapCorrectlyWithTwoDetailAndTwoNearby() throws InterruptedException {
        //Given
        Detail detail = getRestaurantDetail();
        detailLiveData.setValue(detail);

        Detail detail2 = getRestaurantDetail2();
        detailLiveData2.setValue(detail2);

        Integer attendies = 1;
        attendiesLiveData.setValue(attendies);

        Integer attendies2 = 5;
        attendies2LiveData.setValue(attendies2);

        Location location = new Location("");
        location.setLatitude(48.85838489);
        location.setLongitude(2.350088);
        locationLiveData.setValue(location);

        NearbyResponse nearbyResponse = getNearbyResponses();
        nearbyReponseLiveData.setValue(nearbyResponse);

        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());

        //When
        LiveDataTestUtil.getOrAwaitValue(listViewViewModel.getUiModelsLiveData(), 1);
        List<RestaurantInfo> restaurantInfos = LiveDataTestUtil.getOrAwaitValue(listViewViewModel.getUiModelsLiveData(), 1);

        //Then
        assertEquals(2, restaurantInfos.size());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", restaurantInfos.get(0).getId());
        assertEquals("Benoit Paris", restaurantInfos.get(0).getName());
        assertEquals("courgette", restaurantInfos.get(0).getImage());
        assertEquals("20 Rue Saint-Martin, Paris", restaurantInfos.get(0).getAddress());
        assertEquals(new Double(2.00), restaurantInfos.get(0).getRating());
        assertEquals("5437222m", restaurantInfos.get(0).getDistance());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", restaurantInfos.get(0).getId());

        assertEquals("ChIJI5HJsx9u5kcRJl41efCbOAw", restaurantInfos.get(1).getId());
        assertEquals("CourgetteBar", restaurantInfos.get(1).getName());
        assertEquals("courgette", restaurantInfos.get(1).getImage());
        assertEquals("27 Rue Saint-Martin, Paris", restaurantInfos.get(1).getAddress());
        assertEquals(new Double(2.00), restaurantInfos.get(1).getRating());
        assertEquals("5437222m", restaurantInfos.get(1).getDistance());
    }

    @Test
    public void shouldMapCorrectlyWithNoResult() throws InterruptedException {
        //Given
        NearbyResponse nearbyResponse = new NearbyResponse();
        nearbyReponseLiveData.setValue(nearbyResponse);

        Detail detail = new Detail();
        detailLiveData.setValue(detail);

        Location location = new Location("");
        location.setLatitude(48.685673);
        location.setLongitude(1.961863);
        locationLiveData.setValue(location);

        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());

        //When
        List<RestaurantInfo> restaurantInfos = LiveDataTestUtil.getOrAwaitValue(listViewViewModel.getUiModelsLiveData(), 1);

        //Then
        assertTrue(restaurantInfos.isEmpty());
    }

    private Detail getRestaurantDetail() {
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setFormattedAddress("20 Rue Saint-Martin, 75004 Paris, France");
        resultDetail.setName("Benoit Paris");
        resultDetail.setFormattedPhoneNumber("01 42 72 25 76");
        resultDetail.setUrl("https://maps.google.com/?cid=14478655399410179573");

        Detail detail = new Detail();
        detail.setResultDetail(resultDetail);
        return detail;
    }

    private Detail getRestaurantDetail2() {
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setFormattedAddress("27 Rue Saint-Martin, 75004 Paris, France");
        resultDetail.setName("CourgetteBar");
        resultDetail.setFormattedPhoneNumber("01 45 78 95 65");
        resultDetail.setUrl("https://maps.google.com/?cid=14478655397810179573");

        Detail detail = new Detail();
        detail.setResultDetail(resultDetail);
        return detail;
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