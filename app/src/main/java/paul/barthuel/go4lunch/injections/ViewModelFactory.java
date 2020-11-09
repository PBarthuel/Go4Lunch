package paul.barthuel.go4lunch.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import org.threeten.bp.Clock;

import paul.barthuel.go4lunch.data.local.ActualLocationRepository;
import paul.barthuel.go4lunch.MainViewModel;
import paul.barthuel.go4lunch.ui.search_restaurant.SearchRestaurantViewModel;
import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository;
import paul.barthuel.go4lunch.data.firestore.restaurant.RestaurantRepository;
import paul.barthuel.go4lunch.data.firestore.user.UserRepository;
import paul.barthuel.go4lunch.data.local.UserSearchRepository;
import paul.barthuel.go4lunch.data.retrofit.AutocompleteRepository;
import paul.barthuel.go4lunch.data.retrofit.NearbyRepository;
import paul.barthuel.go4lunch.data.retrofit.PlaceDetailRepository;
import paul.barthuel.go4lunch.ui.chat.ChatViewModel;
import paul.barthuel.go4lunch.ui.list_view.ListViewViewModel;
import paul.barthuel.go4lunch.ui.list_view.UriBuilder;
import paul.barthuel.go4lunch.ui.map_view.LocalisationViewModel;
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailViewModel;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory sFactory;

    @NonNull
    private final ActualLocationRepository actualLocationRepository;
    @NonNull
    private final NearbyRepository nearbyRepository;
    @NonNull
    private final PlaceDetailRepository placeDetailRepository;
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final RestaurantRepository restaurantRepository;
    @NonNull
    private final ChatRepository chatRepository;
    @NonNull
    private final AutocompleteRepository autocompleteRepository;
    @NonNull
    private final UserSearchRepository userSearchRepository;

    private ViewModelFactory(
            @NonNull ActualLocationRepository actualLocationRepository,
            @NonNull NearbyRepository nearbyRepository,
            @NonNull PlaceDetailRepository placeDetailRepository,
            @NonNull UserRepository userRepository,
            @NonNull RestaurantRepository restaurantRepository,
            @NonNull ChatRepository chatRepository,
            @NonNull AutocompleteRepository autocompleteRepository,
            @NonNull UserSearchRepository userSearchRepository) {
        this.actualLocationRepository = actualLocationRepository;
        this.nearbyRepository = nearbyRepository;
        this.placeDetailRepository = placeDetailRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.chatRepository = chatRepository;
        this.autocompleteRepository = autocompleteRepository;
        this.userSearchRepository = userSearchRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (sFactory == null) {
                    sFactory = new ViewModelFactory(
                            new ActualLocationRepository(),
                            new NearbyRepository(),
                            new PlaceDetailRepository(),
                            new UserRepository(),
                            new RestaurantRepository(),
                            new ChatRepository(),
                            new AutocompleteRepository(),
                            UserSearchRepository.getInstance()
                    );
                }
            }
        }

        return sFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LocalisationViewModel.class)) {
            return (T) new LocalisationViewModel(
                    actualLocationRepository,
                    nearbyRepository,
                    userSearchRepository);
        }else if (modelClass.isAssignableFrom(ListViewViewModel.class)) {
            return (T) new ListViewViewModel(
                    actualLocationRepository,
                    nearbyRepository,
                    placeDetailRepository,
                    restaurantRepository,
                    new UriBuilder(),
                    userSearchRepository,
                    Clock.systemDefaultZone());
        }else if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(
                    placeDetailRepository,
                    FirebaseAuth.getInstance(),
                    restaurantRepository,
                    userRepository,
                    new UriBuilder());
        }else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T)new MainViewModel(FirebaseAuth.getInstance(),
                    userRepository);
        }else if (modelClass.isAssignableFrom(WorkmatesViewModel.class)) {
            return (T)new WorkmatesViewModel(userRepository,
                    FirebaseAuth.getInstance());
        }else if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T)new ChatViewModel(chatRepository,
                    Clock.systemDefaultZone(),
                    FirebaseAuth.getInstance());
        }else if (modelClass.isAssignableFrom(SearchRestaurantViewModel.class)) {
            return (T)new SearchRestaurantViewModel(autocompleteRepository,
                    actualLocationRepository,
                    userRepository,
                    userSearchRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
