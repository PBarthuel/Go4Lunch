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
import paul.barthuel.go4lunch.data.firestore.user.dto.User;

import static org.junit.Assert.*;

public class WorkmatesViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    MutableLiveData<List<User>> usersLiveData;

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

        Mockito.doReturn(usersLiveData).when(userRepository).getAllUsers();

        workmatesViewModel = new WorkmatesViewModel(userRepository, auth);
    }

    @Test
    public void shouldMapCorrectlyOneUser() throws InterruptedException {
        //Given


        // When


        // Then

    }

    @Test
    public void shouldMapCorrectlyTwoUser() throws InterruptedException {
        //Given
        List<User> users = getUsers();
        usersLiveData.setValue(users);
        Mockito.doReturn(getDefaultUser()).when(auth).getCurrentUser();
        // When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);

        // Then
        assertEquals("courgette", workmatesInfos.get(0).getName());
        assertEquals("jambon", workmatesInfos.get(1).getName());
    }

    private FirebaseUser getDefaultUser() {
        FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);
        Mockito.doReturn("id1").when(firebaseUser).getUid();
        return firebaseUser;
    }

    @Test
    public void shouldMapCorrectlyNoUser() throws InterruptedException {
        //Given
        List<User> users =  new ArrayList<>();
        usersLiveData.setValue(users);
        // When
        List<WorkmatesInfo> workmatesInfos = LiveDataTestUtil.getOrAwaitValue(workmatesViewModel.getUiModelsLiveData(), 1);
        // Then
        assertTrue(workmatesInfos.isEmpty());
    }

    private List<WorkmatesInfo> getWormates() {
        WorkmatesInfo workmatesInfo = new WorkmatesInfo("courgette",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/AAAAAAAAAAA/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg",
                "id1");
        WorkmatesInfo workmatesInfo2 = new WorkmatesInfo("jambon",
                "https://lh3.googleusercontent.com/-7um8S-y7QQY/AAAAAAAAAAI/BBBBBBBBBBBB/ACHi3re4GaBg2ddibtBGvS7QNBelGD-9zg/s96-c/photo.jpg",
                "58P1DA'(57DRG");

        List<WorkmatesInfo> workmatesInfos = new ArrayList<>();
        workmatesInfos.add(workmatesInfo);
        workmatesInfos.add(workmatesInfo2);
        return workmatesInfos;
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
}
