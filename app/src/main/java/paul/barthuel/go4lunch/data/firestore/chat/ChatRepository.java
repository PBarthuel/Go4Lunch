package paul.barthuel.go4lunch.data.firestore.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatRepository {

    private static final String CHAT_COLLECTION = "chat";

    // --- COLLECTION REFERENCE ---

    public LiveData<List<Message>> getChatForUsers(String userId1, String userId2) {
        String firstKey;
        String secondKey;
        if (userId1.compareTo(userId2) > 0) {
            firstKey = userId1;
            secondKey = userId2;
        } else {
            firstKey = userId2;
            secondKey = userId1;
        }
        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        FirebaseFirestore
                .getInstance()
                .collection(CHAT_COLLECTION)
                .document(firstKey + "_" + secondKey)
                .collection(CHAT_COLLECTION).addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        List<Message> messages = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Message message = document.toObject(Message.class);
                            messages.add(message);
                        }
                        Collections.sort(messages, (o1, o2) -> Long.compare(o1.getEpoch(), o2.getEpoch()));
                        mutableLiveData.postValue(messages);
                    }
                });
        return mutableLiveData;
    }

    private CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance()
                .collection(CHAT_COLLECTION);
    }

    // --- CREATE ---

    public void createChatMessage(String senderUserId, String receiverUserId, String senderName, Long epoch, String text) {
        Message message = new Message(text, senderUserId, senderName, epoch);
        String firstKey;
        String secondKey;
        if (senderUserId.compareTo(receiverUserId) > 0) {
            firstKey = senderUserId;
            secondKey = receiverUserId;
        } else {
            firstKey = receiverUserId;
            secondKey = senderUserId;
        }
        getChatCollection()
                .document(firstKey + "_" + secondKey)
                .collection("chat")
                .add(message);
    }
}
