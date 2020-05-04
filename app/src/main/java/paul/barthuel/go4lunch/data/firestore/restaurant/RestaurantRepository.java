package paul.barthuel.go4lunch.data.firestore.restaurant;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.threeten.bp.LocalDate;

import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Restaurant;

public class RestaurantRepository {

    private static final String RESTAURANT_COLLECTION = "restaurants";
    private static final String USERS_COLLECTION = "users";

    // --- COLLECTION REFERENCE ---

    private CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(RESTAURANT_COLLECTION);
    }

    // --- CREATE ---

    public Task<Void> createRestaurant(String placeId, LocalDate date) {
        Restaurant restaurantToCreate = new Restaurant(placeId, date.toString());
        return getRestaurantsCollection().document(placeId).set(restaurantToCreate);
    }

    public Task<Void> addUserToRestaurant(String placeId, String uid) {
            return getRestaurantsCollection()
                    .document(placeId)
                    .update(USERS_COLLECTION, FieldValue.arrayUnion(uid));
    }

    // --- GET ---

    public Task<DocumentSnapshot> getRestaurant(String placeId) {
        return getRestaurantsCollection().document(placeId).get();
    }

    // --- DELETE ---

    //TODO utiliser WhereTo
    public Task<Void> deleteUserToRestaurant(String uid) {
        /*String userId = getRestaurantsCollection()
                .document()
                .collection(USERS_COLLECTION)
                .whereEqualTo(USERS_COLLECTION, uid).get().getResult().toString();
        return getRestaurantsCollection()
                .document()
                .update(USERS_COLLECTION, FieldValue.arrayRemove(userId));*/
        return null;
    }
}
