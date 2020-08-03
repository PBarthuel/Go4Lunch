package paul.barthuel.go4lunch.ui.workmates;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import paul.barthuel.go4lunch.ActualLocationRepository;
import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.model.nearby.NearbyResponse;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.ListViewViewModel;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class WorkmatesViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {


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


        // When


        // Then

    }

    @Test
    public void shouldMapCorrectlyNoUser() throws InterruptedException {
        //Given


        // When


        // Then

    }

    public void getUsers() {

    }
}
