package paul.barthuel.go4lunch.data.firestore.user;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
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

    public void createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        getUsersCollection().document(uid).set(userToCreate);
    }

    public void addPlaceIdAndRestaurantNameToTodayUser(String uid, String placeId, String restaurantName) {
        TodayUser todayUser = new TodayUser(uid, placeId, restaurantName);
        getTodayUsersCollection()
                .document(uid)
                .set(todayUser);
    }

    // --- GET ---

    public  LiveData<User> getUser(String uid) {
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        getUsersCollection().document(uid).addSnapshotListener((documentSnapshot, e) -> {
            if(documentSnapshot != null) {
                mutableLiveData.postValue(documentSnapshot.toObject(User.class));
            }
        });
        return mutableLiveData;
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData<>();
        getUsersCollection().get().addOnCompleteListener(task -> {
            List<User> users = new ArrayList<>();
            if(task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    users.add(queryDocumentSnapshot.toObject(User.class));
                }
            }
            mutableLiveData.postValue(users);
        });
        return mutableLiveData;
    }

    public LiveData<List<TodayUser>> getAllTodayUsers() {
        MutableLiveData<List<TodayUser>> mutableLiveData = new MutableLiveData<>();
        getTodayUsersCollection().get().addOnCompleteListener(task -> {
            List<TodayUser> todayUsers = new ArrayList<>();
            if(task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    todayUsers.add(queryDocumentSnapshot.toObject(TodayUser.class));
                }
            }
            mutableLiveData.postValue(todayUsers);
        });
        return mutableLiveData;
    }

    public LiveData<TodayUser> getTodayUser(String uid) {
        MutableLiveData<TodayUser> mutableLiveData = new MutableLiveData<>();
        getTodayUsersCollection().document(uid).addSnapshotListener((documentSnapshot, e) -> {
            if(documentSnapshot != null) {
                mutableLiveData.postValue(documentSnapshot.toObject(TodayUser.class));
            }
        });
        return mutableLiveData;
    }

    @Nullable
    public TodayUser getTodayUserSync(String uid) {
        try {
            return Tasks.await(getTodayUsersCollection().document(uid).get()).toObject(TodayUser.class);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
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

