package paul.barthuel.go4lunch.ui.chat;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;

import java.util.List;

import paul.barthuel.go4lunch.LiveDataTestUtil;
import paul.barthuel.go4lunch.data.firestore.chat.ChatRepository;
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;

public class ChatViewModelTest {

    // Check documentation but basically, with this rule : livedata.postValue() is the same as livedata.setValue()
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<Message> messagesLiveData;

    @Mock
    ChatRepository chatRepository;

    private ChatViewModel chatViewModel;

    @Before
    public void setup() {
        chatRepository = Mockito.mock(ChatRepository.class);

        messagesLiveData = new MutableLiveData<>();

        Mockito.doReturn(messagesLiveData).when(chatRepository).getChatForUsers("Inu2tJ6JZMb1sBvbOlFLk0zGlx53", "P8orHCZjywakkaE1LzkV5r7bYnH3");

        chatViewModel = new ChatViewModel(chatRepository);
    }

    /*@Test
    public void shouldShowTheDateLikeHHmmIfTheDayStoredIsToday() throws InterruptedException {
        //Given
        Message message = getMessage();
        messagesLiveData.setValue(message);

        //When
        List<UiMessage> messages = LiveDataTestUtil.getOrAwaitValue(chatViewModel.getUiModelsLiveData());

        //Then
        Assert.assertEquals("'à '18:27", messages.get(0).getDate());
    }*/

    public Message getMessage() {
        return new Message("courgette",
                "Inu2tJ6JZMb1sBvbOlFLk0zGlx53",
                "P8orHCZjywakkaE1LzkV5r7bYnH3",
                null);
    }
}
