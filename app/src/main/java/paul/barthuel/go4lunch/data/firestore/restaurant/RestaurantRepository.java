package paul.barthuel.go4lunch.data.firestore.restaurant;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;

public class RestaurantRepository {

    private static final String RESTAURANT_COLLECTION = "restaurants";
    private static final String USERS_TO_RESTAURANT_COLLECTION = "usersToRestaurant";
    private static final String LIKED_RESTAURANT_COLLECTION = "likedRestaurants";
    private static final String KEY_RESTAURANT_NAME = "restaurantName";

    // --- COLLECTION REFERENCE ---

    private CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance()
                .collection(LocalDate.now().toString() + RESTAURANT_COLLECTION);
    }

    private Query getUserstoRestaurantCollection() {
        return FirebaseFirestore.getInstance()
                .collectionGroup(USERS_TO_RESTAURANT_COLLECTION);
    }

    private CollectionReference getLikedRestaurantCollection() {
        return FirebaseFirestore.getInstance()
                .collection(LIKED_RESTAURANT_COLLECTION);
    }

    // --- CREATE ---

    public void addUserToRestaurant(String placeId, String restaurantName, String uid, String userName) {
        Uid userId = new Uid(uid, userName);
        getRestaurantsCollection()
                .document(placeId)
                .collection(USERS_TO_RESTAURANT_COLLECTION)
                .document(uid)
                .set(userId);
        Map<String, String> restaurantNameMap = new HashMap<>();
        restaurantNameMap.put(KEY_RESTAURANT_NAME, restaurantName);
        getRestaurantsCollection()
                .document(placeId)
                .set(restaurantNameMap);
    }

    public void addLikedRestaurant(String restaurantName, String placeId) {
        Map<String, String> restaurantNameMap = new HashMap<>();
        restaurantNameMap.put(KEY_RESTAURANT_NAME, restaurantName);
        getLikedRestaurantCollection().document(placeId).set(restaurantNameMap);
    }

    // --- GET ---

    public LiveData<Integer> getRestaurantAttendies(String placeId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        getRestaurantsCollection().document(placeId).collection("usersToRestaurant").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                liveData.postValue(queryDocumentSnapshots.getDocuments().size());
            }

        });
        return liveData;
    }

    public LiveData<List<Uid>> getUidsFromRestaurant(String placeId) {
        MutableLiveData<List<Uid>> liveData = new MutableLiveData<>();
        getRestaurantsCollection().document(placeId).collection("usersToRestaurant").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                List<Uid> uids = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Uid uid = document.toObject(Uid.class);
                    uids.add(uid);
                }
                liveData.postValue(uids);
            }
        });
        return liveData;
    }

    @Nullable
    public List<Uid> getUidsSync(String placeId) {
        try {
            return Tasks.await(getRestaurantsCollection()
                    .document(placeId)
                    .collection("usersToRestaurant")
                    .get())
                    .toObjects(Uid.class);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<Boolean> isUserGoingToRestaurant(String userId, String placeId) {
        MutableLiveData<Boolean> result = new MediatorLiveData<>();
        getRestaurantsCollection()
                .document(placeId)
                .collection("usersToRestaurant")
                .whereEqualTo("uid", userId)
                .addSnapshotListener((value, error) -> result.postValue(value != null && value.getDocuments().size() > 0));
        return result;
    }

    // --- DELETE ---

    public void deleteUserToRestaurant(String uid, OnDeletedUserCallback onDeletedUserCallback) {
        getUserstoRestaurantCollection().whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> deleteTasks = new ArrayList<>();
                        if(task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Log.d("courgette", "onComplete: " + queryDocumentSnapshot.toString());
                                deleteTasks.add(queryDocumentSnapshot.getReference().delete());
                            }
                        }
                        Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(task1 -> onDeletedUserCallback.onUserDeleted());
                    }
                });
    }

    public interface OnDeletedUserCallback {
        void onUserDeleted();
    }
}
