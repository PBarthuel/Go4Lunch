package paul.barthuel.go4lunch.injections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import org.threeten.bp.Clock
import paul.barthuel.go4lunch.MainViewModel
import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository
import paul.barthuel.go4lunch.data.firestore.user.UserRepository
import paul.barthuel.go4lunch.data.local.ActualLocationRepository
import paul.barthuel.go4lunch.data.local.UserSearchRepository
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository
import paul.barthuel.go4lunch.ui.chat.ChatViewModel
import paul.barthuel.go4lunch.ui.list_view.ListViewViewModel
import paul.barthuel.go4lunch.ui.list_view.UriBuilder
import paul.barthuel.go4lunch.ui.map_view.LocalisationViewModel
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailViewModel
import paul.barthuel.go4lunch.ui.search_restaurant.SearchRestaurantViewModel
import paul.barthuel.go4lunch.ui.workmates.WorkmatesViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
        private val actualLocationRepository: ActualLocationRepository,
        private val nearbyRepository: NearbyRepository,
        private val placeDetailRepository: PlaceDetailRepository,
        private val userRepository: UserRepository,
        private val restaurantRepository: RestaurantRepository,
        private val chatRepository: ChatRepository,
        private val autocompleteRepository: AutocompleteRepository,
        private val userSearchRepository: UserSearchRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LocalisationViewModel::class.java) -> {
                return LocalisationViewModel(
                        actualLocationRepository,
                        nearbyRepository,
                        userSearchRepository) as T
            }
            modelClass.isAssignableFrom(ListViewViewModel::class.java) -> {
                return ListViewViewModel(
                        actualLocationRepository,
                        nearbyRepository,
                        placeDetailRepository,
                        restaurantRepository,
                        UriBuilder(),
                        userSearchRepository,
                        Clock.systemDefaultZone()) as T
            }
            modelClass.isAssignableFrom(RestaurantDetailViewModel::class.java) -> {
                return RestaurantDetailViewModel(
                        placeDetailRepository,
                        FirebaseAuth.getInstance(),
                        restaurantRepository,
                        userRepository,
                        UriBuilder()) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                return MainViewModel(FirebaseAuth.getInstance(),
                        userRepository) as T
            }
            modelClass.isAssignableFrom(WorkmatesViewModel::class.java) -> {
                return WorkmatesViewModel(userRepository,
                        FirebaseAuth.getInstance()) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                return ChatViewModel(chatRepository,
                        Clock.systemDefaultZone(),
                        FirebaseAuth.getInstance()) as T
            }
            modelClass.isAssignableFrom(SearchRestaurantViewModel::class.java) -> {
                return SearchRestaurantViewModel(autocompleteRepository,
                        actualLocationRepository,
                        userRepository,
                        userSearchRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        // Dependency injection
        val instance = ViewModelFactory(
                ActualLocationRepository(),
                NearbyRepository(),
                PlaceDetailRepository(),
                UserRepository(),
                RestaurantRepository(),
                ChatRepository(),
                AutocompleteRepository(),
                UserSearchRepository.getInstance()
        )
    }
}