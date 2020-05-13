package paul.barthuel.go4lunch.ui.list_view;

import android.os.Bundle;
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
import paul.barthuel.go4lunch.injections.ViewModelFactory;
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity;

public class ListViewFragment extends Fragment implements RestaurantInfoAdapter.Listener {

    private ListViewViewModel mViewModel;

    public static ListViewFragment newInstance() {

        Bundle args = new Bundle();

        ListViewFragment fragment = new ListViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_restaurant_recycler_view);

        final RestaurantInfoAdapter adapter = new RestaurantInfoAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewModel.getUiModelsLiveData().observe(getViewLifecycleOwner(), new Observer<List<RestaurantInfo>>() {
            @Override
            public void onChanged(List<RestaurantInfo> restaurantInfos) {
                adapter.submitList(restaurantInfos);
            }
        });

        return view;
    }

    @Override
    public void onRestaurantInfoClick(RestaurantInfo restaurantInfo) {
        startActivity(RestaurantDetailActivity.navigate(requireContext(), restaurantInfo.getId()));
    }
}