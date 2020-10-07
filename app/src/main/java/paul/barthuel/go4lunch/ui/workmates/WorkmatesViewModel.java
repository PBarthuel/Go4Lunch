package paul.barthuel.go4lunch.ui.workmates;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;

public class WorkmatesViewModel extends ViewModel {

    private final MediatorLiveData<List<WorkmatesInfo>> mediatorLiveDataWorkmates = new MediatorLiveData<>();
    private final FirebaseAuth auth;

    public WorkmatesViewModel(UserRepository userRepository, FirebaseAuth auth) {

        this.auth = auth;
        LiveData<List<User>> usersLiveData = userRepository.getAllUsers();
        LiveData<List<TodayUser>> todayUsersLiveData = userRepository.getAllTodayUsers();

        mediatorLiveDataWorkmates.addSource(usersLiveData, users -> mediatorLiveDataWorkmates.setValue(map(users,
                todayUsersLiveData.getValue())));
        mediatorLiveDataWorkmates.addSource(todayUsersLiveData, todayUsers -> mediatorLiveDataWorkmates.setValue(map(usersLiveData.getValue(),
                todayUsers)));
    }

    private List<WorkmatesInfo> map(@Nullable List<User> users,
                                    @Nullable List<TodayUser> todayUsers) {

        List<WorkmatesInfo> workmatesInfos = new ArrayList<>();

        if (users == null || todayUsers == null) {
            return workmatesInfos;
        }

        for (User user : users) {
            if (auth.getCurrentUser() != null) {
                if (!user.getUid().equals(auth.getCurrentUser().getUid())) {
                    String restaurantName;
                    String placeId;
                    TodayUser matchingTodayUser = null;
                    for (TodayUser todayUser : todayUsers) {
                        if (todayUser.getUserId().equals(user.getUid())) {
                            matchingTodayUser = todayUser;
                            break;
                        }
                    }
                    if (matchingTodayUser != null) {
                        restaurantName = "(" + matchingTodayUser.getRestaurantName() + ")";
                        placeId = matchingTodayUser.getPlaceId();
                    } else {
                        restaurantName = "haven't decided yet";
                        placeId = null;
                    }
                    String name = user.getUsername();
                    String image = user.getUrlPicture();
                    String id = user.getUid();
                    workmatesInfos.add(new WorkmatesInfo(name, image, id, placeId, restaurantName));
                }
            }
        }

        return workmatesInfos;
    }

    LiveData<List<WorkmatesInfo>> getUiModelsLiveData() {
        return mediatorLiveDataWorkmates;
    }
}