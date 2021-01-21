package paul.barthuel.go4lunch.ui.restaurant_detail;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.injections.ViewModelFactory;
import paul.barthuel.go4lunch.ui.chat.ChatActivity;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesAdapter;
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo;

public class RestaurantDetailFragment extends Fragment implements WorkmatesAdapter.Listener {

    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_RESTAURANT_NAME = "KEY_RESTAURANT_NAME";
    private RestaurantDetailViewModel mViewModel;
    private TextView textViewCall;

    public static RestaurantDetailFragment newInstance(String id, String restaurantName) {

        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_RESTAURANT_NAME, restaurantName);

        RestaurantDetailFragment fragment = new RestaurantDetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, ViewModelFactory.Companion.getInstance()).get(RestaurantDetailViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_detail_fragment, container, false);

        ImageView imageView = view.findViewById(R.id.restaurant_detail_iv);
        TextView textViewTitle = view.findViewById(R.id.content_scrolling_restaurant_detail_title_tv);
        TextView textViewAddress = view.findViewById(R.id.content_scrolling_restaurant_detail_address_tv);
        TextView textViewWebSite = view.findViewById(R.id.content_scrolling_restaurant_detail_website_iv);
        textViewCall = view.findViewById(R.id.content_scrolling_restaurant_detail_call_iv);
        TextView textViewLike = view.findViewById(R.id.content_scrolling_restaurant_detail_like_iv);
        RecyclerView recyclerView = view.findViewById(R.id.content_scrolling_restaurant_detail_rv);
        FloatingActionButton floatingActionButton = view.findViewById(R.id.restaurant_detail_fab);

        final WorkmatesAdapter adapter = new WorkmatesAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if(getArguments() != null) {
            mViewModel.init(getArguments().getString(KEY_ID), getArguments().getString(KEY_RESTAURANT_NAME));
        }
        mViewModel.getLiveDataResultDetail().observe(getViewLifecycleOwner(), restaurantDetailInfo -> {
            Glide.with(imageView).load(restaurantDetailInfo.getImage()).into(imageView);
            textViewTitle.setText(restaurantDetailInfo.getName());
            textViewAddress.setText(restaurantDetailInfo.getAddress());
            textViewWebSite.setOnClickListener(view1 -> onWebSiteClick(restaurantDetailInfo.getUrl()));
            textViewCall.setTag(restaurantDetailInfo.getPhoneNumber());
            textViewCall.setOnClickListener(view1 -> makePhoneCall());
            textViewLike.setOnClickListener(view1 -> mViewModel.likeRestaurant());
            if(restaurantDetailInfo.getIsUserGoing()) {
                floatingActionButton.setImageResource(R.drawable.ic_baseline_check_circle_24);
            }else {
                floatingActionButton.setImageDrawable(null);
            }
            floatingActionButton.setOnClickListener(v -> mViewModel.goToRestaurant());
        });
        mViewModel.getLiveDataWormatesInfos().observe(getViewLifecycleOwner(), adapter::submitList);
        return view;
    }

    private void makePhoneCall() {
        String phoneNumber = textViewCall.getTag().toString();
        if (phoneNumber.trim().length() > 0) {
            String dial = "tel:" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
            PackageManager manager = requireContext().getPackageManager();
            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
            if (infos.size() > 0) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "No phone application", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "no phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void onWebSiteClick(String url) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }

    @Override
    public void onWorkmateInfoClick(WorkmatesInfo workmatesInfo) {
        startActivity(ChatActivity.navigate(requireContext(), workmatesInfo.getId()));
    }

    @Override
    public void onRestaurantClick(String id, String restaurantName) {
        startActivity(RestaurantDetailActivity.navigate(requireContext(), id, restaurantName));
    }

}
