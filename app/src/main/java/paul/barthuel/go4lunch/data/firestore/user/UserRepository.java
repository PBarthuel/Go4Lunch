package paul.barthuel.go4lunch.data.firestore.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import paul.barthuel.go4lunch.data.firestore.user.dto.TodayUser;
import paul.barthuel.go4lunch.data.firestore.user.dto.User;


public class UserRepository {

    private static final String USER_COLLECTION = "Users";
    private static final String TODAY_USER_COLLECTION = "TodayUsers";

    // --- COLLECTION REFERENCE ---

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(USER_COLLECTION);
    }

    private CollectionReference getTodayUsersCollection() {
        return FirebaseFirestore.getInstance()
                .collection(LocalDate.now().toString() + TODAY_USER_COLLECTION);
    }

    // --- CREATE ---

    public  Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return getUsersCollection().document(uid).set(userToCreate);
    }

    public Task<Void> addPlaceIdAndRestaurantNameToTodayUser(String uid, String placeId, String restaurantName) {
        TodayUser todayUser = new TodayUser(uid, placeId, restaurantName);
        return getTodayUsersCollection()
                .document(uid)
                .set(todayUser);
    }

    // --- GET ---

    public  LiveData<User> getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        getUsersCollection().document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null) {
                    mutableLiveData.postValue(documentSnapshot.toObject(User.class));
                }
            }
        });
        return mutableLiveData;
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

    public LiveData<List<TodayUser>> getAllTodayUsers() {
        MutableLiveData<List<TodayUser>> mutableLiveData = new MutableLiveData<>();
        getTodayUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<TodayUser> todayUsers = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    todayUsers.add(queryDocumentSnapshot.toObject(TodayUser.class));
                }
                mutableLiveData.postValue(todayUsers);
            }
        });
        return mutableLiveData;
    }

    public LiveData<User> getTodayUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        getTodayUsersCollection().document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null) {
                    mutableLiveData.postValue(documentSnapshot.toObject(User.class));
                }
            }
        });
        return mutableLiveData;
    }

    // --- UPDATE ---

    public  Task<Void> updateUsername(String username, String uid) {
        return getUsersCollection().document(uid).update("username", username);
    }

    // --- DELETE ---

    public Task<Void> deleteUser(String uid) {
        return getUsersCollection().document(uid).delete();
    }

    public Task<Void> deleteTodayUser(String uid) {
        return getTodayUsersCollection().document(uid).delete();
    }

}

