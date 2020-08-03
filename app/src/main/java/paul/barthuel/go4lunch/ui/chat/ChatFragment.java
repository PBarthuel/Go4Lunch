package paul.barthuel.go4lunch.ui.chat;

import android.os.Bundle;
import android.view.FrameMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.data.firestore.chat.dto.Message;
import paul.barthuel.go4lunch.injections.ViewModelFactory;
import paul.barthuel.go4lunch.ui.list_view.ListViewViewModel;
import paul.barthuel.go4lunch.ui.list_view.RestaurantInfo;
import paul.barthuel.go4lunch.ui.list_view.RestaurantInfoAdapter;

public class ChatFragment extends Fragment {

    private ChatViewModel mViewModel;
    private static final String KEY_WORKMATE_ID = "KEY_WORKMATE_ID";

    public static ChatFragment newInstance(String workmateId) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_WORKMATE_ID, workmateId);

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ChatViewModel.class);
        mViewModel.init(getArguments().getString(KEY_WORKMATE_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        ChatAdapter chatAdapter = new ChatAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.chat_rv);
        EditText editText = view.findViewById(R.id.chat_et);
        Button button = view.findViewById(R.id.chat_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        mViewModel.getUiModelsLiveData().observe(getViewLifecycleOwner(), new Observer<List<UiMessage>>() {
            @Override
            public void onChanged(List<UiMessage> uiMessages) {
                chatAdapter.setNewData(uiMessages);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.sendMessage(editText.getText().toString());
            }
        });

        return view;
    }

}
