package paul.barthuel.go4lunch.data.firestore.restaurant;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import paul.barthuel.go4lunch.data.firestore.restaurant.dto.Uid;

public class RestaurantRepository {

    private static final String RESTAURANT_COLLECTION = "restaurants";
    private static final String USERS_TO_RESTAURANT_COLLECTION = "usersToRestaurant";

    // --- COLLECTION REFERENCE ---

    private CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance()
                .collection(LocalDate.now().toString() + RESTAURANT_COLLECTION);
    }

    private Query getUserstoRestaurantCollection() {
        return FirebaseFirestore.getInstance()
                .collectionGroup(USERS_TO_RESTAURANT_COLLECTION);
    }

    // --- CREATE ---

    public Task<Void> addUserToRestaurant(String placeId, String uid) {
        Uid userId = new Uid(uid);
        return getRestaurantsCollection()
                .document(placeId)
                .collection(USERS_TO_RESTAURANT_COLLECTION)
                .document(uid)
                .set(userId);
    }

    // --- GET ---

    public Task<DocumentSnapshot> getRestaurant(String placeId) {
        return getRestaurantsCollection().document(placeId).get();
    }

    public LiveData<Integer> getRestaurantAttendies(String placeId) {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        getRestaurantsCollection().document(placeId).collection("usersToRestaurant").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    liveData.postValue(queryDocumentSnapshots.getDocuments().size());
                }

            }
        });
        return liveData;
    }

    // --- DELETE ---

    public void deleteUserToRestaurant(String uid, OnDeletedUserCallback onDeletedUserCallback) {
        getUserstoRestaurantCollection().whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<Task<Void>> deleteTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Log.d("courgette", "onComplete: " + queryDocumentSnapshot.toString());
                        deleteTasks.add(queryDocumentSnapshot.getReference().delete());
                    }
                    Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Task<?>>> task) {
                            onDeletedUserCallback.onUserDeleted();
                        }
                    });
                }
            }
        });
    }

    public interface OnDeletedUserCallback {
        void onUserDeleted();
    }
}
