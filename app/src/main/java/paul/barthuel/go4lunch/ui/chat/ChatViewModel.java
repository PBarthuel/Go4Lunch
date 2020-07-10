package paul.barthuel.go4lunch.ui.chat;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository;
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    private LiveData<List<UiMessage>> liveDataMessages = new MediatorLiveData<>();

    LiveData<List<UiMessage>> getUiModelsLiveData() {
        return liveDataMessages;
    }

    public ChatViewModel(final ChatRepository chatRepository) {

        this.chatRepository = chatRepository;

        liveDataMessages = Transformations.map(chatRepository.getChatForUsers(FirebaseAuth.getInstance().getCurrentUser().getUid(), "courgette"),
                new Function<List<Message>, List<UiMessage>>() {
                    @Override
                    public List<UiMessage> apply(List<Message> messages) {

                        List<UiMessage> uiMessages = new ArrayList<>(messages.size());
                        for (Message message : messages) {
                            String formattedDate;
                            if (LocalDate.now().equals(message.getDate().toLocalDate())) {
                                formattedDate = message.getDate().format(DateTimeFormatter.ofPattern("'à 'HH:mm"));
                            } else {
                                formattedDate = message.getDate().format(DateTimeFormatter.ofPattern("'le 'dd/MM' à 'HH:mm"));
                            }
                            uiMessages.add(new UiMessage(message.getText(),
                                    formattedDate,
                                    message.getSenderName(),
                                    message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())));
                        }
                        return uiMessages;
                    }
                });
    }
}
