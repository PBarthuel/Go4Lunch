package paul.barthuel.go4lunch.ui.workmates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import paul.barthuel.go4lunch.data.firestore.user.UserRepository
import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser
import paul.barthuel.go4lunch.data.firestore.user.dto.User
import java.util.*

class WorkmatesViewModel(userRepository: UserRepository, private val auth: FirebaseAuth) : ViewModel() {

    private val mediatorLiveDataWorkmates = MediatorLiveData<List<WorkmatesInfo>>()

    private fun map(users: List<User>?,
                    todayUsers: List<TodayUser>?): List<WorkmatesInfo> {
        val workmatesInfos: MutableList<WorkmatesInfo> = ArrayList()
        if (users == null || todayUsers == null) {
            return workmatesInfos
        }
        for (user in users) {
            if (auth.currentUser != null && user.uid != auth.currentUser!!.uid) {
                var restaurantName: String
                var placeId: String?
                var matchingTodayUser: TodayUser? = null
                for (todayUser in todayUsers) {
                    if (todayUser.userId == user.uid) {
                        matchingTodayUser = todayUser
                        break
                    }
                }
                when {
                    matchingTodayUser != null -> {
                        restaurantName = "(" + matchingTodayUser.restaurantName + ")"
                        placeId = matchingTodayUser.placeId
                    }
                    else -> {
                        restaurantName = "haven't decided yet"
                        placeId = null
                    }
                }
                val name = user.username
                val image = user.urlPicture
                val id = user.uid
                workmatesInfos.add(WorkmatesInfo(name, image, id, placeId, restaurantName))
            }
        }
        return workmatesInfos
    }

    val uiModelsLiveData: LiveData<List<WorkmatesInfo>>
        get() = mediatorLiveDataWorkmates

    init {
        val usersLiveData = userRepository.allUsers
        val todayUsersLiveData = userRepository.allTodayUsers
        mediatorLiveDataWorkmates.addSource(usersLiveData) { users: List<User>? ->
            mediatorLiveDataWorkmates.setValue(map(users,
                    todayUsersLiveData.value))
        }
        mediatorLiveDataWorkmates.addSource(todayUsersLiveData) { todayUsers: List<TodayUser>? ->
            mediatorLiveDataWorkmates.setValue(map(usersLiveData.value,
                    todayUsers))
        }
    }
}