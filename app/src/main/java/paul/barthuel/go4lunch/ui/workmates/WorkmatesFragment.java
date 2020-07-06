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

public class WorkmatesFragment extends Fragment {

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

        final WorkmatesAdapter adapter = new WorkmatesAdapter();
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
}