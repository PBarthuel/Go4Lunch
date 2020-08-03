package paul.barthuel.go4lunch.ui.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;

public class WorkmatesViewModel extends ViewModel {

    private MediatorLiveData<List<WorkmatesInfo>> mediatorLiveDataWorkmates = new MediatorLiveData<>();
    private UserRepository mUserRepository;

    public WorkmatesViewModel(UserRepository userRepository) {

        this.mUserRepository = userRepository;

        mediatorLiveDataWorkmates.addSource(mUserRepository.getAllUsers(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mediatorLiveDataWorkmates.setValue(map(users));
            }
        });
    }

    private List<WorkmatesInfo> map(List<User> users) {

        List<WorkmatesInfo> workmatesInfos = new ArrayList<>();

        for (User user : users) {
            if (user.getUid() ==)
            String name = user.getUsername();
            String image = user.getUrlPicture();
            String id = user.getUid();
            workmatesInfos.add(new WorkmatesInfo(name, image, id));
        }

        return workmatesInfos;
    }

    LiveData<List<WorkmatesInfo>> getUiModelsLiveData() {
        return mediatorLiveDataWorkmates;
    }
}