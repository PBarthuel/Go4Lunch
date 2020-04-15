package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_detail_fragment, container, false);

        ImageView imageView = view.findViewById(R.id.restaurant_detail_iv);
        TextView textViewTitle = view.findViewById(R.id.content_scrolling_restaurant_detail_title_tv);
        TextView textViewAddress = view.findViewById(R.id.content_scrolling_restaurant_detail_address_tv);

        mViewModel.init(getArguments().getString(KEY_ID));
        mViewModel.getLiveDataResultDetail().observe(getViewLifecycleOwner(), new Observer<RestaurantDetailInfo>() {
            @Override
            public void onChanged(RestaurantDetailInfo restaurantDetailInfo) {
                Glide.with(imageView).load(restaurantDetailInfo.getImage()).into(imageView);
                textViewTitle.setText(restaurantDetailInfo.getName());
                textViewAddress.setText(restaurantDetailInfo.getAddress());
            }
        });

        return view;
    }
}
