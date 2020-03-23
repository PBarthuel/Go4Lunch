package paul.barthuel.go4lunch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import paul.barthuel.go4lunch.ui.list_view.RestaurantInfo;

public class RestaurantInfoAdapter extends RecyclerView.Adapter<RestaurantInfoAdapter.ViewHolder> {

    private List<RestaurantInfo> mList;
    private Listener listener;

    public RestaurantInfoAdapter(Listener listener) {
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

        viewHolder.bind(mList.get(index), listener);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setNewData(List<RestaurantInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewName;
        private final TextView mTextViewAddress;
        private final TextView mTextViewOpeningHours;
        private final TextView mTextViewDistance;
        //private final ImageView mImageViewThumbnail;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewName = itemView.findViewById(R.id.restaurant_description_item_tv_name);
            mTextViewAddress = itemView.findViewById(R.id.restaurant_description_item_address);
            mTextViewOpeningHours = itemView.findViewById(R.id.restaurant_description_item_schedule);
            mTextViewDistance = itemView.findViewById(R.id.restaurant_description_item_distance);
            //mImageViewThumbnail = itemView.findViewById(R.id.restaurant_description_item_iv_thumbnail);
            //TODO utiliser glide
        }

        private void bind(final RestaurantInfo restaurantInfo, final Listener listener) {

            mTextViewName.setText(restaurantInfo.getName());
            mTextViewAddress.setText(restaurantInfo.getAddress());
            mTextViewOpeningHours.setText(restaurantInfo.getOpeningHours());
            mTextViewDistance.setText(restaurantInfo.getDistance());

        }
    }

    public interface Listener {
        void onRestaurantInfoClick(RestaurantInfo restaurantInfo);
        //TODO Lié ça avec la nouvelle activité qui affiche les details
        //TODO dans l'activité de detail juste envoyé l'id du restau
    }
}


