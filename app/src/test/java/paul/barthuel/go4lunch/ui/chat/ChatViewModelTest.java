package paul.barthuel.go4lunch.ui.chat;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository;
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<List<Message>> messagesLiveData;

    @Mock
    ChatRepository chatRepository;

    @Mock
    FirebaseAuth auth;

    private ChatViewModel chatViewModel;

    @Before
    public void setup() {
        chatRepository = Mockito.mock(ChatRepository.class);
        auth = Mockito.mock(FirebaseAuth.class);

        messagesLiveData = new MutableLiveData<>();
        FirebaseUser user = Mockito.mock(FirebaseUser.class);

        Mockito.doReturn(messagesLiveData).when(chatRepository).getChatForUsers("uid", "workmateUid");

        Mockito.doReturn("userName").when(user).getDisplayName();
        Mockito.doReturn("uid").when(user).getUid();
        Mockito.doReturn(user).when(auth).getCurrentUser();

        chatViewModel = new ChatViewModel(chatRepository, Clock.fixed(
                getCurrentTime(),
                ZoneOffset.UTC),
                auth);
    }

    @Test
    public void shouldShowTheDateLikeHHmmIfTheDayStoredIsToday() throws InterruptedException {
        //Given
        messagesLiveData.setValue(getMessages(getCurrentTime().getEpochSecond() * 1000));

        chatViewModel.init("workmateUid");

        //When
        List<UiMessage> messages = LiveDataTestUtil.getOrAwaitValue(chatViewModel.getUiModelsLiveData(), 1);

        //Then
        Assert.assertEquals("à 21:19", messages.get(0).getDate());
    }

    @Test
    public void shouldShowTheDateLikeDDMMHHmmIfTheDayStoredIsanotherDay() throws InterruptedException {
        //Given
        messagesLiveData.setValue(getMessages(getCurrentTime2().getEpochSecond() * 1000));

        chatViewModel.init("workmateUid");

        //When
        List<UiMessage> messages = LiveDataTestUtil.getOrAwaitValue(chatViewModel.getUiModelsLiveData(), 1);

        //Then
        Assert.assertEquals("le 19/10 à 22:19", messages.get(0).getDate());
    }

    private Instant getCurrentTime() {
        return ZonedDateTime.of(1995,
                12,
                19,
                20,
                19,
                0,
                0,
                ZoneOffset.UTC).toInstant();
    }

    private Instant getCurrentTime2() {
        return ZonedDateTime.of(2010,
                10,
                19,
                20,
                19,
                0,
                0,
                ZoneOffset.UTC).toInstant();
    }

    public List<Message> getMessages(Long firstMessageEpoch) {
        List<Message> messages = new ArrayList<>();
        messages.add(getMessage(firstMessageEpoch));
        return messages;
    }

    public Message getMessage(Long epoch) {
        return new Message("courgette",
                "workmateUid",
                "workmate",
                epoch);
    }
}
