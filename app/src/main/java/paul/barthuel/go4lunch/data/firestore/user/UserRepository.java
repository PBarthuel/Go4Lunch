package paul.barthuel.go4lunch.data.firestore.user;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import paul.barthuel.go4lunch.data.firestore.user.dto.User;


public class UserRepository {

    private static final String USER_COLLECTION = "users";
    private static final String PLACE_ID = "placeId";

    // --- COLLECTION REFERENCE ---

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(USER_COLLECTION);
    }

    // --- CREATE ---

    public  Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return getUsersCollection().document(uid).set(userToCreate);
    }

    public Task<Void> addRestaurantToUser(String uid, String placeId) {
        return getUsersCollection()
                .document(uid)
                .update(PLACE_ID, placeId);
    }

    // --- GET ---

    public  Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public  Task<Void> updateUsername(String username, String uid) {
        return getUsersCollection().document(uid).update("username", username);
    }

    // --- DELETE ---

    public  Task<Void> deleteUser(String uid) {
        return getUsersCollection().document(uid).delete();
    }

}

