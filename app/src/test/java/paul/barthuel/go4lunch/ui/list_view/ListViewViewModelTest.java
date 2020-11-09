package paul.barthuel.go4lunch.ui.list_view;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.threeten.bp.Clock;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.model.detail.Close;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.Open;
import paul.barthuel.go4lunch.data.model.detail.OpeningHours;
import paul.barthuel.go4lunch.data.model.detail.Period;
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
    private MutableLiveData<String> userSearchLiveData;

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

    @Mock
    UserSearchRepository userSearchRepository;

    private ListViewViewModel listViewViewModel;

    @Before
    public void setup() {
        actualLocationRepository = Mockito.mock(ActualLocationRepository.class);
        nearbyRepository = Mockito.mock(NearbyRepository.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        placeDetailRepository = Mockito.mock(PlaceDetailRepository.class);
        uriBuilder = Mockito.mock(UriBuilder.class);
        userSearchRepository = Mockito.mock(UserSearchRepository.class);

        nearbyReponseLiveData = new MutableLiveData<>();
        detailLiveData = new MutableLiveData<>();
        detailLiveData2 = new MutableLiveData<>();
        locationLiveData = new MutableLiveData<>();
        attendiesLiveData = new MutableLiveData<>();
        attendies2LiveData = new MutableLiveData<>();
        userSearchLiveData = new MutableLiveData<>();

        Mockito.doReturn(locationLiveData).when(actualLocationRepository).getLocationLiveData();
        Mockito.doReturn(nearbyReponseLiveData).when(nearbyRepository).getNearbyForLocation(any());
        Mockito.doReturn(detailLiveData).when(placeDetailRepository).getDetailForRestaurantId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn(detailLiveData2).when(placeDetailRepository).getDetailForRestaurantId("ChIJI5HJsx9u5kcRJl41efCbOAw");
        Mockito.doReturn(attendiesLiveData).when(restaurantRepository).getRestaurantAttendies("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn(attendies2LiveData).when(restaurantRepository).getRestaurantAttendies("ChIJI5HJsx9u5kcRJl41efCbOAw");
        Mockito.doReturn(userSearchLiveData).when(userSearchRepository).getUserSearchQueryLiveData();
        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());
        Clock clock = Clock.fixed(ZonedDateTime.of(1995,
                12,
                19,
                20,
                15,
                0,
                0,
                ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        listViewViewModel = new ListViewViewModel(actualLocationRepository,
                nearbyRepository,
                placeDetailRepository,
                restaurantRepository,
                uriBuilder,
                userSearchRepository,
                clock);
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

        userSearchLiveData.setValue("Benoit Paris");

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
        assertEquals("open until 22:00", restaurantInfos.get(0).getOpeningHours());
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

        userSearchLiveData.setValue("Benoit Paris");

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

        userSearchLiveData.setValue("Benoit Paris");

        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());

        //When
        List<RestaurantInfo> restaurantInfos = LiveDataTestUtil.getOrAwaitValue(listViewViewModel.getUiModelsLiveData(), 1);

        //Then
        assertTrue(restaurantInfos.isEmpty());
    }

    private Detail getRestaurantDetail() {
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        resultDetail.setFormattedAddress("20 Rue Saint-Martin, 75004 Paris, France");
        resultDetail.setName("Benoit Paris");
        resultDetail.setFormattedPhoneNumber("01 42 72 25 76");
        resultDetail.setUrl("https://maps.google.com/?cid=14478655399410179573");
        resultDetail.setOpeningHours(getOpeningHours());

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
        resultDetail.setOpeningHours(getOpeningHours2());

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

    private OpeningHours getOpeningHours() {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(true);
        openingHours.setPeriods(getPeriods());
        return openingHours;
    }

    private OpeningHours getOpeningHours2() {
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(false);
        openingHours.setPeriods(getPeriods2());
        return openingHours;
    }

    private List<Period> getPeriods() {
        List<Period> periods = new ArrayList<>();
        periods.add(getPeriod1());
        periods.add(getPeriod2());
        periods.add(getPeriod3());
        periods.add(getPeriod4());
        periods.add(getPeriod5());
        periods.add(getPeriod6());
        periods.add(getPeriod7());
        return periods;
    }

    private List<Period> getPeriods2() {
        List<Period> periods = new ArrayList<>();
        periods.add(getPeriod1b());
        periods.add(getPeriod2b());
        periods.add(getPeriod3b());
        periods.add(getPeriod4b());
        periods.add(getPeriod5b());
        periods.add(getPeriod6b());
        periods.add(getPeriod7b());
        return periods;
    }

    private Period getPeriod1() {
        Period period = new Period();
        period.setOpen(getOpen1());
        period.setClose(getClose1());
        return period;
    }

    private Open getOpen1() {
        Open open = new Open();
        open.setDay(0);
        open.setTime("1100");
        return open;
    }

    private Close getClose1() {
        Close close = new Close();
        close.setDay(0);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod2() {
        Period period = new Period();
        period.setOpen(getOpen2());
        period.setClose(getClose2());
        return period;
    }

    private Open getOpen2() {
        Open open = new Open();
        open.setDay(1);
        open.setTime("1100");
        return open;
    }

    private Close getClose2() {
        Close close = new Close();
        close.setDay(1);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod3() {
        Period period = new Period();
        period.setOpen(getOpen3());
        period.setClose(getClose3());
        return period;
    }

    private Open getOpen3() {
        Open open = new Open();
        open.setDay(2);
        open.setTime("1100");
        return open;
    }

    private Close getClose3() {
        Close close = new Close();
        close.setDay(2);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod4() {
        Period period = new Period();
        period.setOpen(getOpen4());
        period.setClose(getClose4());
        return period;
    }

    private Open getOpen4() {
        Open open = new Open();
        open.setDay(3);
        open.setTime("1100");
        return open;
    }

    private Close getClose4() {
        Close close = new Close();
        close.setDay(3);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod5() {
        Period period = new Period();
        period.setOpen(getOpen5());
        period.setClose(getClose5());
        return period;
    }

    private Open getOpen5() {
        Open open = new Open();
        open.setDay(4);
        open.setTime("1100");
        return open;
    }

    private Close getClose5() {
        Close close = new Close();
        close.setDay(4);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod6() {
        Period period = new Period();
        period.setOpen(getOpen6());
        period.setClose(getClose6());
        return period;
    }

    private Open getOpen6() {
        Open open = new Open();
        open.setDay(5);
        open.setTime("1100");
        return open;
    }

    private Close getClose6() {
        Close close = new Close();
        close.setDay(5);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod7() {
        Period period = new Period();
        period.setOpen(getOpen7());
        period.setClose(getClose7());
        return period;
    }

    private Open getOpen7() {
        Open open = new Open();
        open.setDay(6);
        open.setTime("1100");
        return open;
    }

    private Close getClose7() {
        Close close = new Close();
        close.setDay(6);
        close.setTime("2200");
        return close;
    }

    private Period getPeriod1b() {
        Period period = new Period();
        period.setOpen(getOpen1b());
        period.setClose(getClose1b());
        return period;
    }

    private Open getOpen1b() {
        Open open = new Open();
        open.setDay(0);
        open.setTime("1100");
        return open;
    }

    private Close getClose1b() {
        Close close = new Close();
        close.setDay(0);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod2b() {
        Period period = new Period();
        period.setOpen(getOpen2b());
        period.setClose(getClose2b());
        return period;
    }

    private Open getOpen2b() {
        Open open = new Open();
        open.setDay(1);
        open.setTime("1100");
        return open;
    }

    private Close getClose2b() {
        Close close = new Close();
        close.setDay(1);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod3b() {
        Period period = new Period();
        period.setOpen(getOpen3b());
        period.setClose(getClose3b());
        return period;
    }

    private Open getOpen3b() {
        Open open = new Open();
        open.setDay(2);
        open.setTime("1100");
        return open;
    }

    private Close getClose3b() {
        Close close = new Close();
        close.setDay(2);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod4b() {
        Period period = new Period();
        period.setOpen(getOpen4b());
        period.setClose(getClose4b());
        return period;
    }

    private Open getOpen4b() {
        Open open = new Open();
        open.setDay(3);
        open.setTime("1100");
        return open;
    }

    private Close getClose4b() {
        Close close = new Close();
        close.setDay(3);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod5b() {
        Period period = new Period();
        period.setOpen(getOpen5b());
        period.setClose(getClose5b());
        return period;
    }

    private Open getOpen5b() {
        Open open = new Open();
        open.setDay(4);
        open.setTime("1100");
        return open;
    }

    private Close getClose5b() {
        Close close = new Close();
        close.setDay(4);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod6b() {
        Period period = new Period();
        period.setOpen(getOpen6b());
        period.setClose(getClose6b());
        return period;
    }

    private Open getOpen6b() {
        Open open = new Open();
        open.setDay(5);
        open.setTime("1100");
        return open;
    }

    private Close getClose6b() {
        Close close = new Close();
        close.setDay(5);
        close.setTime("1800");
        return close;
    }

    private Period getPeriod7b() {
        Period period = new Period();
        period.setOpen(getOpen7b());
        period.setClose(getClose7b());
        return period;
    }

    private Open getOpen7b() {
        Open open = new Open();
        open.setDay(6);
        open.setTime("1100");
        return open;
    }

    private Close getClose7b() {
        Close close = new Close();
        close.setDay(6);
        close.setTime("1800");
        return close;
    }
}