package paul.barthuel.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.injections.ViewModelFactory;
import paul.barthuel.go4lunch.ui.chat.ChatActivity;
import paul.barthuel.go4lunch.ui.list_view.RestaurantInfo;
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.Listener {

   private WorkmatesViewModel mViewModel;

    public static WorkmatesFragment newInstance() {

        Bundle args = new Bundle();

        WorkmatesFragment fragment = new WorkmatesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(WorkmatesViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_restaurant_recycler_view);

        final WorkmatesAdapter adapter = new WorkmatesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewModel.getUiModelsLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkmatesInfo>>() {
            @Override
            public void onChanged(List<WorkmatesInfo> workmatesInfos) {
                adapter.submitList(workmatesInfos);
            }
        });

        return view;
    }

    @Override
    public void onWorkmateInfoClick(WorkmatesInfo workmatesInfo) {
        startActivity(ChatActivity.navigate(requireContext(), workmatesInfo.getId()));
    }

    @Override
    public void onRestaurantClick(String id, String restaurantName) {
        if (!restaurantName.equals("haven't decided yet")) {
            startActivity(RestaurantDetailActivity.navigate(requireContext(), id, restaurantName));
        }
    }
}