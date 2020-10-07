package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;
import paul.barthuel.go4lunch.data.model.detail.Detail;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo;

public class RestaurantDetailViewModel extends ViewModel {

    private final MediatorLiveData<RestaurantDetailInfo> liveDataRestaurantDetailInfo = new MediatorLiveData<>();

    private final MediatorLiveData<List<WorkmatesInfo>> mediatorLiveDataWorkmatesInfo = new MediatorLiveData<>();

    private final MediatorLiveData<Map<String, User>> mediatorLiveDataUsers = new MediatorLiveData<>();

    private String id;
    private String restaurantName;
    private final List<String> alreadyFetchUids = new ArrayList<>();

    private final FirebaseAuth mAuth;

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
        if(mAuth.getCurrentUser() != null) {
            LiveData<Boolean> isUserGoing = restaurantRepository.isUserGoingToRestaurant(mAuth.getCurrentUser().getUid(),
                    id);


            LiveData<Detail> detail = placeDetailRepository.getDetailForRestaurantId(id);

            liveDataRestaurantDetailInfo.addSource(detail, detail1 -> combineDetailsAndUserGoing(detail1,
                    isUserGoing.getValue()));
            liveDataRestaurantDetailInfo.addSource(isUserGoing, isUserGoing1 -> combineDetailsAndUserGoing(detail.getValue(),
                    isUserGoing1));

            mediatorLiveDataWorkmatesInfo.addSource(mediatorLiveDataUsers, users -> combineUids(users,
                    liveDataAttendiesUid.getValue()));
            mediatorLiveDataWorkmatesInfo.addSource(liveDataAttendiesUid, uids -> combineUids(mediatorLiveDataUsers.getValue(),
                    uids));
        }
    }

    private void combineDetailsAndUserGoing(@Nullable Detail detail,
                                            @Nullable Boolean isUserGoing) {
        if(detail == null) {
            return;
        }

        boolean resolvedIsUserGoing;

        if(isUserGoing == null) {
            resolvedIsUserGoing = false;
        }else {
            resolvedIsUserGoing = isUserGoing;
        }

        String name = detail.getResultDetail().getName();

        String address = detail.getResultDetail().getFormattedAddress();

        String uri;

        String photoReference = detail.getResultDetail().getPhotos().get(0).getPhotoReference();
        uri = uriBuilder.buildUri("https",
                "maps.googleapis.com",
                "maps/api/place/photo",
                new Pair<>("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs"),
                new Pair<>("photoreference", photoReference),
                new Pair<>("maxwidth", "1080"));

        String id = detail.getResultDetail().getPlaceId();

        String phoneNumber = detail.getResultDetail().getFormattedPhoneNumber();

        String url = detail.getResultDetail().getUrl();

        liveDataRestaurantDetailInfo.setValue(new RestaurantDetailInfo(name,
                address,
                uri,
                id,
                phoneNumber,
                url,
                resolvedIsUserGoing));
    }

    private void combineUids(@Nullable Map<String, User> users, @Nullable List<Uid> uids) {
        if (users == null || uids == null) {
            return;
        }

        List<WorkmatesInfo> workmatesInfos = new ArrayList<>();

        for (Uid uid : uids) {
            User user = users.get(uid.getUid());
            if (user == null) {
                if (!alreadyFetchUids.contains(uid.getUid())) {
                    alreadyFetchUids.add(uid.getUid());
                    mediatorLiveDataUsers.addSource(userRepository.getUser(uid.getUid()), user1 -> {
                            Map<String, User> map = mediatorLiveDataUsers.getValue();
                            assert map != null;
                            map.put(user1.getUid(), user1);
                            mediatorLiveDataUsers.postValue(map);
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

    public LiveData<RestaurantDetailInfo> getLiveDataResultDetail() {
        return liveDataRestaurantDetailInfo;
    }

    public LiveData<List<WorkmatesInfo>> getLiveDataWormatesInfos() {
        return mediatorLiveDataWorkmatesInfo;
    }

    public void likeRestaurant() {
        restaurantRepository.addLikedRestaurant(restaurantName, id);
    }

    public void goToRestaurant() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            userRepository.addPlaceIdAndRestaurantNameToTodayUser(mAuth.getCurrentUser().getUid(), id, restaurantName);
            restaurantRepository.deleteUserToRestaurant(mAuth.getCurrentUser().getUid(),
                    () -> restaurantRepository.addUserToRestaurant(id,
                            restaurantName,
                            mAuth.getCurrentUser().getUid(),
                            mAuth.getCurrentUser().getDisplayName()));
        }
    }
}
