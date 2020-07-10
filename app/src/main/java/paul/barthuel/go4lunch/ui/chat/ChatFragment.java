package paul.barthuel.go4lunch.ui.chat;

import android.os.Bundle;
import android.view.FrameMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public static ChatFragment newInstance() {

        Bundle args = new Bundle();

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ChatViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);



        return view;
    }

}
