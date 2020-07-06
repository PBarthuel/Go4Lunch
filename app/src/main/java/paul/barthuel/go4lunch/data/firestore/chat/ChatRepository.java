package paul.barthuel.go4lunch.data.firestore.chat;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatRepository {

    private static final String CHAT_COLLECTION = "chat";

    public LiveData<List<Message>> getChatForUsers(String userId1, String userId2) {
        List<Message> messages = new ArrayList<>();
        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        FirebaseFirestore
                .getInstance()
                .collection(CHAT_COLLECTION)
                .document(userId1 + "_" + userId2)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Message message = documentSnapshot.toObject(Message.class);
                        messages.add(message);
                        mutableLiveData.postValue(messages);
                    }
                });
                return mutableLiveData;
    }
}
