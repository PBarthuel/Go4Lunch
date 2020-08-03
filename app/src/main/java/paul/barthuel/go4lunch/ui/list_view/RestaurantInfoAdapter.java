package paul.barthuel.go4lunch.ui.list_view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import paul.barthuel.go4lunch.R;
import paul.barthuel.go4lunch.ui.custom_view.CustomRatingBar;

public class RestaurantInfoAdapter extends ListAdapter<RestaurantInfo, RestaurantInfoAdapter.ViewHolder> {

    private Listener listener;

    public RestaurantInfoAdapter(Listener listener) {
        super(new RestaurantInfoDiffCallBack());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.restaurant_description_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        viewHolder.bind(getItem(index), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewName;
        private final TextView mTextViewAddress;
        private final TextView mTextViewOpeningHours;
        private final TextView mTextViewDistance;
        private final TextView mTextViewAttendies;
        private final View mRoot;
        private final ImageView mImageViewThumbnail;
        private final CustomRatingBar mCustomRatingBar;



        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewName = itemView.findViewById(R.id.restaurant_description_item_tv_name);
            mTextViewAddress = itemView.findViewById(R.id.restaurant_description_item_address);
            mTextViewOpeningHours = itemView.findViewById(R.id.restaurant_description_item_schedule);
            mTextViewDistance = itemView.findViewById(R.id.restaurant_description_item_distance);
            mTextViewAttendies = itemView.findViewById(R.id.restaurant_description_item_attendies);
            mRoot = itemView.findViewById(R.id.restaurant_description_item_cl);
            mImageViewThumbnail = itemView.findViewById(R.id.restaurant_description_item_iv_thumbnail);
            mCustomRatingBar = itemView.findViewById(R.id.restaurant_description_item_crb_rating);
        }

        private void bind(final RestaurantInfo restaurantInfo, final Listener listener) {

            mTextViewName.setText(restaurantInfo.getName());
            mTextViewAddress.setText(restaurantInfo.getAddress());
            mTextViewOpeningHours.setText(restaurantInfo.getOpeningHours());
            mTextViewDistance.setText(restaurantInfo.getDistance());
            if (restaurantInfo.isAttendiesVisible()) {
                mTextViewAttendies.setText(restaurantInfo.getAttendies());
                mTextViewAttendies.setVisibility(View.VISIBLE);
            }else {
                mTextViewAttendies.setVisibility(View.INVISIBLE);
            }
            mCustomRatingBar.setStars(restaurantInfo.getRating());
            Log.d("courgette", "bind: name " + restaurantInfo.getName() + "rating " + restaurantInfo.getRating());
            Glide.with(mImageViewThumbnail).load(restaurantInfo.getImage()).into(mImageViewThumbnail);
            mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRestaurantInfoClick(restaurantInfo);
                }
            });
        }
    }

    public interface Listener {
        void onRestaurantInfoClick(RestaurantInfo restaurantInfo);
    }
}


