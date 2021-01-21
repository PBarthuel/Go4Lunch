package paul.barthuel.go4lunch.ui.restaurant_detail

import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository
import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid
import paul.barthuel.go4lunch.data.firestore.user.UserRepository
import paul.barthuel.go4lunch.data.firestore.user.dto.User
import paul.barthuel.go4lunch.data.model.detail.Detail
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository
import paul.barthuel.go4lunch.ui.list_view.UriBuilder
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo
import java.util.*

class RestaurantDetailViewModel(private val placeDetailRepository: PlaceDetailRepository,
                                private val mAuth: FirebaseAuth,
                                private val restaurantRepository: RestaurantRepository,
                                private val userRepository: UserRepository,
                                private val uriBuilder: UriBuilder) : ViewModel() {
    private val liveDataRestaurantDetailInfo = MediatorLiveData<RestaurantDetailInfo>()
    private val mediatorLiveDataWorkmatesInfo = MediatorLiveData<List<WorkmatesInfo>>()
    private val mediatorLiveDataUsers = MediatorLiveData<MutableMap<String, User>?>()
    private var id: String? = null
    private var restaurantName: String? = null
    private val alreadyFetchUids: MutableList<String> = ArrayList()
    fun init(id: String, restaurantName: String?) {
        Log.d("courgette",
                "init() called with: id = [$id]")
        this.id = id
        this.restaurantName = restaurantName
        mediatorLiveDataUsers.value = HashMap()
        val liveDataAttendiesUid = restaurantRepository.getUidsFromRestaurant(id)
        if (mAuth.currentUser != null) {
            val isUserGoing = restaurantRepository.isUserGoingToRestaurant(mAuth.currentUser!!.uid,
                    id)
            val detail = placeDetailRepository.getDetailForRestaurantId(id)
            liveDataRestaurantDetailInfo.addSource(detail) { detail1: Detail? ->
                combineDetailsAndUserGoing(
                        detail1,
                        isUserGoing.value)
            }
            liveDataRestaurantDetailInfo.addSource(isUserGoing) { isUserGoing1: Boolean? ->
                combineDetailsAndUserGoing(
                        detail.value,
                        isUserGoing1)
            }
            mediatorLiveDataWorkmatesInfo.addSource(mediatorLiveDataUsers) { users: Map<String, User>? ->
                combineUids(
                        users,
                        liveDataAttendiesUid.value)
            }
            mediatorLiveDataWorkmatesInfo.addSource(liveDataAttendiesUid) { uids: List<Uid>? ->
                combineUids(
                        mediatorLiveDataUsers.value,
                        uids)
            }
        }
    }

    private fun combineDetailsAndUserGoing(detail: Detail?,
                                           isUserGoing: Boolean?) {
        if (detail == null) {
            return
        }
        val resolvedIsUserGoing: Boolean = isUserGoing ?: false
        val name = detail.resultDetail.name
        val address = detail.resultDetail.formattedAddress
        val uri: String
        val photoReference = detail.resultDetail.photos[0].photoReference
        uri = uriBuilder.buildUri("https",
                "maps.googleapis.com",
                "maps/api/place/photo",
                Pair("key", "AIzaSyDf9lQFMPnggxP8jYVT8NvGxmSQjuhNrNs"),
                Pair("photoreference", photoReference),
                Pair("maxwidth", "1080"))
        val id = detail.resultDetail.placeId
        val phoneNumber = detail.resultDetail.formattedPhoneNumber
        val url = detail.resultDetail.url
        liveDataRestaurantDetailInfo.value = RestaurantDetailInfo(name,
                address,
                uri,
                id,
                phoneNumber,
                url,
                resolvedIsUserGoing)
    }

    private fun combineUids(users: Map<String, User>?,
                            uids: List<Uid>?) {
        if (users == null || uids == null) {
            return
        }
        val workmatesInfos: MutableList<WorkmatesInfo> = ArrayList()
        for (uid in uids) {
            val user = users[uid.uid]
            if (user == null) {
                if (!alreadyFetchUids.contains(uid.uid)) {
                    alreadyFetchUids.add(uid.uid)
                    mediatorLiveDataUsers.addSource(userRepository.getUser(uid.uid)) { user1: User ->
                        val map = mediatorLiveDataUsers.value!!
                        map[user1.uid] = user1
                        mediatorLiveDataUsers.postValue(map)
                    }
                }
            } else {
                workmatesInfos.add(mapUser(user))
            }
        }
        mediatorLiveDataWorkmatesInfo.value = workmatesInfos
    }

    private fun mapUser(user: User): WorkmatesInfo {
        return WorkmatesInfo(user.username,
                user.urlPicture,
                user.uid,
                null,
                null)
    }

    val liveDataResultDetail: LiveData<RestaurantDetailInfo>
        get() = liveDataRestaurantDetailInfo

    val liveDataWormatesInfos: LiveData<List<WorkmatesInfo>>
        get() = mediatorLiveDataWorkmatesInfo

    fun likeRestaurant() {
        restaurantRepository.addLikedRestaurant(restaurantName, id)
    }

    fun goToRestaurant() {
        if (mAuth.currentUser != null && mAuth.currentUser!!.photoUrl != null) {
            userRepository.addPlaceIdAndRestaurantNameToTodayUser(mAuth.currentUser!!.uid, id, restaurantName)
            restaurantRepository.deleteUserToRestaurant(mAuth.currentUser!!.uid
            ) {
                restaurantRepository.addUserToRestaurant(id,
                        restaurantName,
                        mAuth.currentUser!!.uid,
                        mAuth.currentUser!!.displayName)
            }
        }
    }

}