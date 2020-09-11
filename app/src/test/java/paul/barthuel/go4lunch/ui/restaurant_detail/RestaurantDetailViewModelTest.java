package paul.barthuel.go4lunch.ui.restaurant_detail;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.Photo;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;


public class RestaurantDetailViewModelTest {


    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<Detail> detailLiveData;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<Uid>> uidLiveData;

    @Mock
    FirebaseAuth auth;

    @Mock
    PlaceDetailRepository placeDetailRepository;

    @Mock
    RestaurantRepository restaurantRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UriBuilder uriBuilder;

    private RestaurantDetailViewModel restaurantDetailViewModel;

    @Before
    public void setup() {
        placeDetailRepository = Mockito.mock(PlaceDetailRepository.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        auth = Mockito.mock(FirebaseAuth.class);
        uriBuilder = Mockito.mock(UriBuilder.class);

        detailLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();
        uidLiveData = new MutableLiveData<>();

        Mockito.doReturn(detailLiveData).when(placeDetailRepository).getDetailForRestaurantId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn(userLiveData).when(userRepository).getUser("Inu2tJ6JZMb1sBvbOlFLk0zGlx53");
        Mockito.doReturn(uidLiveData).when(restaurantRepository).getUidsFromRestaurant("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        Mockito.doReturn("courgette").when(uriBuilder).buildUri(any(), any(), any(), any());

        restaurantDetailViewModel = new RestaurantDetailViewModel(placeDetailRepository, auth, restaurantRepository, userRepository, uriBuilder);
    }

    @Test
    public void shouldMapCorrectlyOneDetail() throws InterruptedException {
        //Given
        Detail detail = getRestaurantDetail();
        detailLiveData.setValue(detail);

        restaurantDetailViewModel.init("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "Benoit Paris");

        // When
        RestaurantDetailInfo restaurantDetailInfo = LiveDataTestUtil.getOrAwaitValue(restaurantDetailViewModel.getLiveDataResultDetail(), 1);

        // Then
        assertEquals("20 Rue Saint-Martin, 75004 Paris, France", restaurantDetailInfo.getAddress());
        assertEquals("Benoit Paris", restaurantDetailInfo.getName());
        assertEquals("01 42 72 25 76", restaurantDetailInfo.getPhoneNumber());
        assertEquals("https://maps.google.com/?cid=14478655399410179573", restaurantDetailInfo.getUrl());
    }

    private Detail getRestaurantDetail() {
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setFormattedAddress("20 Rue Saint-Martin, 75004 Paris, France");
        resultDetail.setName("Benoit Paris");
        resultDetail.setFormattedPhoneNumber("01 42 72 25 76");
        resultDetail.setUrl("https://maps.google.com/?cid=14478655399410179573");
        resultDetail.setPhotos(getPhoto());

        Detail detail = new Detail();
        detail.setResultDetail(resultDetail);
        return detail;
    }

    private List<Photo> getPhoto() {
        Photo photo = new Photo();
        photo.setPhotoReference("CmRaAAAASEJVUvMmuz3Ieqd7cGd2sFyhjxbGt7QjB2ZBw0ynqjtXE_a_sVsqhPP66HpebT4Tdao0eWxjzuLn2uA8In9rn7aCVpbhytOnBY-_Ic2C0O_HSG9zpi7xexOFtwGX8OGtEhCjkSZJQxwVaStqvhMIuZnaGhRbYVPkt5I4ZOKuib4njolRQRpoVQ");

        List<Photo> photos = new ArrayList<>();
        photos.add(0, photo);

        return photos;
    }

    //TODO regarder ça aussi
    @Test
    public void shouldCombineCorrectlyUids() throws InterruptedException {
        //Given
        userLiveData.setValue(getUser());

        List<Uid> uids = getUids();
        uidLiveData.setValue(uids);

        restaurantDetailViewModel.init("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", "Benoit Paris");

        //When
        LiveDataTestUtil.getOrAwaitValue(restaurantDetailViewModel.getLiveDataWormatesInfos(), 1);
        List<WorkmatesInfo> workmatesInfo = LiveDataTestUtil.getOrAwaitValue(restaurantDetailViewModel.getLiveDataWormatesInfos(), 1);

        //Then
        assertEquals(1, workmatesInfo.size());
        assertEquals("courgette", workmatesInfo.get(0).getName());
        assertEquals("Inu2tJ6JZMb1sBvbOlFLk0zGlx53", workmatesInfo.get(0).getId());
        assertEquals("https://lh3.googleusercontent.com/a-/AAuE7mCgtveKhcPjmos2EBGwyTFas6Kcmpzgo96q08aT=s96-c", workmatesInfo.get(0).getImage());
    }

    private User getUser() {
        return new User("Inu2tJ6JZMb1sBvbOlFLk0zGlx53",
                "courgette",
                "https://lh3.googleusercontent.com/a-/AAuE7mCgtveKhcPjmos2EBGwyTFas6Kcmpzgo96q08aT=s96-c");
    }

    private List<Uid> getUids() {
        List<Uid> uids = new ArrayList<>();
        uids.add(new Uid("Inu2tJ6JZMb1sBvbOlFLk0zGlx53",
                "courgette"));
        return uids;
    }
}
