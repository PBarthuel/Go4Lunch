package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.injections.ViewModelFactory;

public class RestaurantDetailFragment extends Fragment {

    private static final String KEY_ID = "KEY_ID";
    private RestaurantDetailViewModel mViewModel;

    public static RestaurantDetailFragment newInstance(String id) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);

        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.restaurant_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);
        mViewModel.init(getArguments().getString(KEY_ID));
        mViewModel.getLiveDataResultDetail().observe(getViewLifecycleOwner(), new Observer<RestaurantDetailInfo>() {
            @Override
            public void onChanged(RestaurantDetailInfo restaurantDetailInfo) {

            }
        });
    }
}
