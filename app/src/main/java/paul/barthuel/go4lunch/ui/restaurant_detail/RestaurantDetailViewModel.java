package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;
import paul.barthuel.go4lunch.data.model.detail.ResultDetail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo;

public class RestaurantDetailViewModel extends ViewModel {

    private LiveData<RestaurantDetailInfo> liveDataRestaurantDetailInfo;

    private MediatorLiveData<List<WorkmatesInfo>> mediatorLiveDataWorkmatesInfo = new MediatorLiveData<>();
    private MediatorLiveData<Map<String, User>> mediatorLiveDataUsers = new MediatorLiveData<>();

    private String id;
    private String restaurantName;
    private List<String> alreadyFetchUids = new ArrayList<>();

    private FirebaseAuth mAuth;

    private final UriBuilder uriBuilder;
    private final PlaceDetailRepository placeDetailRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public void init(String id, String restaurantName) {

        Log.d("courgette",
                "init() called with: id = [" + id + "]");
        this.id = id;
        this.restaurantName = restaurantName;

        mediatorLiveDataUsers.setValue(new HashMap<>());
        LiveData<List<Uid>> liveDataAttendiesUid = restaurantRepository.getUidsFromRestaurant(id);

        liveDataRestaurantDetailInfo = Transformations.map(
                placeDetailRepository.getDetailForRestaurantId(id),
                detail -> map(detail.getResultDetail())
        );

        mediatorLiveDataWorkmatesInfo.addSource(mediatorLiveDataUsers, new Observer<Map<String, User>>() {
            @Override
            public void onChanged(Map<String, User> users) {
                combineUids(users,
                        liveDataAttendiesUid.getValue());
            }
        });
        mediatorLiveDataWorkmatesInfo.addSource(liveDataAttendiesUid, new Observer<List<Uid>>() {
            @Override
            public void onChanged(List<Uid> uids) {
                combineUids(mediatorLiveDataUsers.getValue(),
                        uids);
            }
        });
    }

    private void combineUids(Map<String, User> users, List<Uid> uids) {
        if (users == null || uids == null) {
            return;
        }

        List<WorkmatesInfo> workmatesInfos = new ArrayList<>();

        for (Uid uid : uids) {
            User user = users.get(uid.getUid());
            if (user == null) {
                if (!alreadyFetchUids.contains(uid.getUid())) {
                    alreadyFetchUids.add(uid.getUid());
                    mediatorLiveDataUsers.addSource(userRepository.getUser(uid.getUid()), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                                Map<String, User> map = mediatorLiveDataUsers.getValue();
                                assert map != null;
                                map.put(user.getUid(), user);
                                mediatorLiveDataUsers.postValue(map);
                        }
                    });
                }
            }else {
                workmatesInfos.add(mapUser(user));
            }
        }
        mediatorLiveDataWorkmatesInfo.setValue(workmatesInfos);
    }

    private WorkmatesInfo mapUser(User user) {

        return new WorkmatesInfo(user.getUsername(),
                user.getUrlPicture(),
                user.getUid(),
                null,
                null);
    }

    public RestaurantDetailViewModel(final PlaceDetailRepository placeDetailRepository,
                                     final FirebaseAuth firebaseAuth,
                                     final RestaurantRepository restaurantRepository,
                                     final UserRepository userRepository,
                                     final UriBuilder uriBuilder) {

        this.placeDetailRepository = placeDetailRepository;
        this.mAuth = firebaseAuth;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.uriBuilder = uriBuilder;
    }


    private RestaurantDetailInfo map(ResultDetail result) {

        String name = result.getName();

        String address = result.getFormattedAddress();

        String uri = null;

        String photoReference = result.getPhotos().get(0).getPhotoReference();
        uri = uriBuilder.buildUri("https",
                "maps.googleapis.com",
                "maps/api/place/photo",
                new Pair<>("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs"),
                new Pair<>("photoreference", photoReference),
                new Pair<>("maxwidth", "1080"));

        String id = result.getPlaceId();

        String phoneNumber = result.getFormattedPhoneNumber();

        String url = result.getUrl();

        //TODO cree un boolean pour dire si l'utilisateur y va
        return new RestaurantDetailInfo(name,
                address,
                uri,
                id,
                phoneNumber,
                url);
    }

    public LiveData<RestaurantDetailInfo> getLiveDataResultDetail() {
        return liveDataRestaurantDetailInfo;
    }

    public LiveData<List<WorkmatesInfo>> getLiveDataWormatesInfos() {
        return mediatorLiveDataWorkmatesInfo;
    }

    public void goToRestaurant() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            userRepository.addPlaceIdAndRestaurantNameToTodayUser(mAuth.getCurrentUser().getUid(), id, restaurantName);
            restaurantRepository.deleteUserToRestaurant(mAuth.getCurrentUser().getUid(),
                    new RestaurantRepository.OnDeletedUserCallback() {
                        @Override
                        public void onUserDeleted() {
                            restaurantRepository.addUserToRestaurant(id,
                                    restaurantName,
                                    mAuth.getCurrentUser().getUid(),
                                    mAuth.getCurrentUser().getDisplayName());
                        }
                    });
        }
    }
}
