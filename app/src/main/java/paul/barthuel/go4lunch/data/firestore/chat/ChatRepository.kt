package paul.barthuel.go4lunch.data.firestore.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message
import java.util.*

class ChatRepository {
    // --- COLLECTION REFERENCE ---
    fun getChatForUsers(userId1: String, userId2: String): LiveData<List<Message>> {
        val firstKey: String
        val secondKey: String
        if (userId1 > userId2) {
            firstKey = userId1
            secondKey = userId2
        } else {
            firstKey = userId2
            secondKey = userId1
        }
        val mutableLiveData = MutableLiveData<List<Message>>()
        FirebaseFirestore
                .getInstance()
                .collection(CHAT_COLLECTION)
                .document(firstKey + "_" + secondKey)
                .collection(CHAT_COLLECTION).addSnapshotListener { queryDocumentSnapshots: QuerySnapshot?, _: FirebaseFirestoreException? ->
                    if (queryDocumentSnapshots != null) {
                        val messages: MutableList<Message> = ArrayList()
                        for (document in queryDocumentSnapshots.documents) {
                            val message = document.toObject(Message::class.java)
                            message?.let { messages.add(it) }
                        }
                        messages.sortWith(Comparator { o1: Message, o2: Message -> o1.epoch.compareTo(o2.epoch) })
                        mutableLiveData.postValue(messages)
                    }
                }
        return mutableLiveData
    }

    private val chatCollection: CollectionReference
        get() = FirebaseFirestore.getInstance()
                .collection(CHAT_COLLECTION)

    // --- CREATE ---
    fun createChatMessage(senderUserId: String, receiverUserId: String, senderName: String, epoch: Long, text: String) {
        val message = Message(text, senderUserId, senderName, epoch)
        val firstKey: String
        val secondKey: String
        if (senderUserId > receiverUserId) {
            firstKey = senderUserId
            secondKey = receiverUserId
        } else {
            firstKey = receiverUserId
            secondKey = senderUserId
        }
        chatCollection
                .document(firstKey + "_" + secondKey)
                .collection("chat")
                .add(message)
    }

    companion object {
        private const val CHAT_COLLECTION = "chat"
    }
}