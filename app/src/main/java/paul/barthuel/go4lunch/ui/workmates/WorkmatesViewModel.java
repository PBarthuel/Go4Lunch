package paul.barthuel.go4lunch.ui.workmates;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;
import paul.barthuel.go4lunch.data.model.nearby.Result;

public class WorkmatesViewModel extends ViewModel {

    private MediatorLiveData<List<WorkmatesInfo>> mediatorLiveDataWorkmates;
    private UserRepository mUserRepository;

    public WorkmatesViewModel(UserRepository userRepository) {

        this.mUserRepository = userRepository;
    }

    private WorkmatesInfo map(User user) {

        String name = user.getUsername();
        String image = user.getUrlPicture();

        return new WorkmatesInfo(name, image);
    }

    LiveData<List<WorkmatesInfo>> getUiModelsLiveData() {
        return mediatorLiveDataWorkmates;
    }

}