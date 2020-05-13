package paul.barthuel.go4lunch.data.firestore.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

    // --- GET ---

    public  Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    users.add(queryDocumentSnapshot.toObject(User.class));
                }
                mutableLiveData.postValue(users);
            }
        });
        return mutableLiveData;
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

