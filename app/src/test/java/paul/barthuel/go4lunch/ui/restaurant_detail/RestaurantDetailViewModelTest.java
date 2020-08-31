package paul.barthuel.go4lunch.ui.restaurant_detail;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;

import static org.junit.Assert.assertEquals;


public class RestaurantDetailViewModelTest {


    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseAuth firebaseAuth;

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
        firebaseAuth = Mockito.mock(FirebaseAuth.class);
        uriBuilder = Mockito.mock(UriBuilder.class);

        restaurantDetailViewModel = new RestaurantDetailViewModel(placeDetailRepository, firebaseAuth, restaurantRepository, userRepository, uriBuilder);
    }

    @Test
    public void shouldMapCorrectlyOneDetail() throws InterruptedException {
        //Given
            Detail detail = getRestaurantDetail();

        // When
            RestaurantDetailInfo restaurantDetailInfo = new RestaurantDetailInfo(detail.getResultDetail().getName(),
                    detail.getResultDetail().getFormattedAddress(),
                    detail.getResultDetail().getUrl(),
                    detail.getResultDetail().getPlaceId(),
                    detail.getResultDetail().getFormattedPhoneNumber(),
                    detail.getResultDetail().getUrl());

        // Then
            assertEquals("20 Rue Saint-Martin, 75004 Paris, France", restaurantDetailInfo.getAddress());
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
}
