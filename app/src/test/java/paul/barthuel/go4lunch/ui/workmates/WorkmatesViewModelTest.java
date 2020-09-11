package paul.barthuel.go4lunch.ui.workmates;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;

import static org.junit.Assert.*;

public class WorkmatesViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    MutableLiveData<List<User>> usersLiveData;
    MutableLiveData<List<TodayUser>> todayUsersLiveData;

    @Mock
    UserRepository userRepository;

    @Mock
    FirebaseAuth auth;

    private WorkmatesViewModel workmatesViewModel;

    @Before
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        auth = Mockito.mock(FirebaseAuth.class);

        usersLiveData = new MutableLiveData<>();
        todayUsersLiveData = new MutableLiveData<>();

        Mockito.doReturn(usersLiveData).when(userRepository).getAllUsers();
        Mockito.doReturn(todayUsersLiveData).when(userRepository).getAllTodayUsers();

        workmatesViewModel = new WorkmatesViewModel(userRepository, auth);
    }

    @Test
    public void shouldMapCorrectlyTOneUserWhoHasChooseARestaurantToday() throws InterruptedException {
        //Given
        Mockito.doReturn(getDefaultUser()).when(auth).getCurrentUser();
        List<User> users = new ArrayList<>();
        users.add(new User("14H2Qd18àe14é",
                "courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg"));
        usersLiveData.setValue(users);

        List<TodayUser> todayUsers = getTodayUsers();
        todayUsersLiveData.setValue(todayUsers);

        //When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);

        //Then
        assertEquals(1, workmatesInfos.size());
        assertEquals("courgette", workmatesInfos.get(0).getName());
        assertEquals("14H2Qd18àe14é", workmatesInfos.get(0).getId());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", workmatesInfos.get(0).getPlaceId());
        assertEquals("(Benoit Paris)", workmatesInfos.get(0).getRestaurantName());
        assertEquals("https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg", workmatesInfos.get(0).getImage());
    }

    @Test
    public void shouldMapCorrectlyOneUserwhoHasntChooseARestaurantToday() throws InterruptedException {
        //Given
        Mockito.doReturn(getDefaultUser()).when(auth).getCurrentUser();
        List<User> users = new ArrayList<>();
        users.add(new User("136s4d64dz69d4s",
                "carotte",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/CCCCCCCCCCC/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg"));
        usersLiveData.setValue(users);

        List<TodayUser> todayUsers = getTodayUsers();
        todayUsersLiveData.setValue(todayUsers);

        //When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);

        //Then
        assertEquals(1, workmatesInfos.size());
        assertEquals("carotte", workmatesInfos.get(0).getName());
        assertEquals("136s4d64dz69d4s", workmatesInfos.get(0).getId());
        assertNull(workmatesInfos.get(0).getPlaceId());
        assertEquals("haven't decided yet", workmatesInfos.get(0).getRestaurantName());
        assertEquals("https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/CCCCCCCCCCC/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg", workmatesInfos.get(0).getImage());
    }

    @Test
    public void shouldMapCorrectlyTwoUsersAndTwoTodayUsers() throws InterruptedException {
        //Given
        Mockito.doReturn(getDefaultUser()).when(auth).getCurrentUser();
        List<User> users = getUsers();
        usersLiveData.setValue(users);

        List<TodayUser> todayUsers = getTodayUsers();
        todayUsersLiveData.setValue(todayUsers);

        // When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);

        // Then
        assertEquals(2, workmatesInfos.size());
        assertEquals("courgette", workmatesInfos.get(0).getName());
        assertEquals("jambon", workmatesInfos.get(1).getName());
        assertEquals("14H2Qd18àe14é", workmatesInfos.get(0).getId());
        assertEquals("58P1DA'(57DRG", workmatesInfos.get(1).getId());
        assertEquals("https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg", workmatesInfos.get(0).getImage());
        assertEquals("https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/BBBBBBBBBBBB/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg", workmatesInfos.get(1).getImage());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", workmatesInfos.get(0).getPlaceId());
        assertEquals("ChIJQ0bNfR5u5kcR9Z0i41-E7sg", workmatesInfos.get(1).getPlaceId());
        assertEquals("(Benoit Paris)", workmatesInfos.get(0).getRestaurantName());
        assertEquals("(Benoit Paris)", workmatesInfos.get(1).getRestaurantName());
    }

    @Test
    public void shouldMapCorrectlyNoUserAndNoTodayUser() throws InterruptedException {
        //Given
        usersLiveData.setValue(null);

        todayUsersLiveData.setValue(null);
        // When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);
        // Then
        assertTrue(workmatesInfos.isEmpty());
    }

    @Test
    public void shouldMapCorrectlyOneUserWithNoTodayUser() throws InterruptedException {
        //Given
        List<User> users = new ArrayList<>();
        users.add(new User("14H2Qd18àe14é",
                "courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg"));
        usersLiveData.setValue(users);

        todayUsersLiveData.setValue(null);
        // When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);
        // Then
        assertTrue(workmatesInfos.isEmpty());
    }

    private FirebaseUser getDefaultUser() {
        FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);
        Mockito.doReturn("id1").when(firebaseUser).getUid();
        return firebaseUser;
    }

    private List<User> getUsers() {
        User user = new User("14H2Qd18àe14é",
                "courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg");
        User user2 = new User("58P1DA'(57DRG",
                "jambon",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/BBBBBBBBBBBB/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        return users;
    }

    private List<TodayUser> getTodayUsers() {
        TodayUser user = new TodayUser("14H2Qd18àe14é",
                "ChIJQ0bNfR5u5kcR9Z0i41-E7sg",
                "Benoit Paris");
        TodayUser user2 = new TodayUser("58P1DA'(57DRG",
                "ChIJQ0bNfR5u5kcR9Z0i41-E7sg",
                "Benoit Paris");

        List<TodayUser> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        return users;
    }
}
