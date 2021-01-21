package paul.barthuel.go4lunch.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message
import java.util.*

class ChatViewModel(private val chatRepository: ChatRepository,
                    private val clock: Clock,
                    private val auth: FirebaseAuth) : ViewModel() {
    private val liveDataMessages = MediatorLiveData<List<UiMessage>>()
    private var workmateId: String? = null
    val uiModelsLiveData: LiveData<List<UiMessage>>
        get() = liveDataMessages

    fun init(workmateId: String) {
        Log.d("courgette",
                "init() called with: wormateId = [$workmateId]")
        this.workmateId = workmateId
        if (auth.currentUser != null) {
            liveDataMessages.addSource(chatRepository.getChatForUsers(auth.currentUser!!.uid, workmateId)
            ) { messages: List<Message> ->
                val uiMessages: MutableList<UiMessage> = ArrayList(messages.size)
                for (message in messages) {
                    var formattedDate: String
                    val messageDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.epoch), ZoneId.of("Europe/Paris"))
                    formattedDate = if (LocalDate.now(clock) == messageDate.toLocalDate()) {
                        messageDate.format(DateTimeFormatter.ofPattern("'à 'HH:mm"))
                    } else {
                        messageDate.format(DateTimeFormatter.ofPattern("'le 'dd/MM' à 'HH:mm"))
                    }
                    uiMessages.add(UiMessage(message.text,
                            formattedDate,
                            message.senderName, message.senderId == auth.currentUser!!.uid))
                }
                liveDataMessages.setValue(uiMessages)
            }
        }
    }

    fun sendMessage(message: String?) {
        if (auth.currentUser != null) {
            chatRepository.createChatMessage(auth.currentUser!!.uid,
                    workmateId,
                    auth.currentUser!!.displayName,
                    ZonedDateTime.now(clock).toInstant().toEpochMilli(),
                    message)
        }
    }

}