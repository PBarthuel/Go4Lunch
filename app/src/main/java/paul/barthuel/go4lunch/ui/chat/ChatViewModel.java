package paul.barthuel.go4lunch.ui.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository;
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final Clock clock;
    private final FirebaseAuth auth;

    private final MediatorLiveData<List<UiMessage>> liveDataMessages = new MediatorLiveData<>();
    private String workmateId;

    LiveData<List<UiMessage>> getUiModelsLiveData() {
        return liveDataMessages;
    }

    public void init(String workmateId) {

        Log.d("courgette",
                "init() called with: wormateId = [" + workmateId + "]");
        this.workmateId = workmateId;

        if (auth.getCurrentUser() != null) {
            liveDataMessages.addSource(chatRepository.getChatForUsers(auth.getCurrentUser().getUid(), workmateId),
                    messages -> {
                        List<UiMessage> uiMessages = new ArrayList<>(messages.size());
                        for (Message message : messages) {
                            String formattedDate;
                            LocalDateTime messageDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getEpoch()), ZoneId.of("Europe/Paris"));
                            if (LocalDate.now(clock).equals(messageDate.toLocalDate())) {
                                formattedDate = messageDate.format(DateTimeFormatter.ofPattern("'à 'HH:mm"));
                            } else {
                                formattedDate = messageDate.format(DateTimeFormatter.ofPattern("'le 'dd/MM' à 'HH:mm"));
                            }
                            uiMessages.add(new UiMessage(message.getText(),
                                    formattedDate,
                                    message.getSenderName(),
                                    message.getSenderId().equals(auth.getCurrentUser().getUid())));
                        }
                        liveDataMessages.setValue(uiMessages);
                    });
        }
    }

    public ChatViewModel(final ChatRepository chatRepository,
                         final Clock clock,
                         final FirebaseAuth auth) {

        this.chatRepository = chatRepository;
        this.clock = clock;
        this.auth = auth;
    }

    public void sendMessage(String message) {
        if (auth.getCurrentUser() != null) {
            chatRepository.createChatMessage(auth.getCurrentUser().getUid(),
                    workmateId,
                    auth.getCurrentUser().getDisplayName(),
                    ZonedDateTime.now(clock).toInstant().toEpochMilli(),
                    message);
        }
    }
}
