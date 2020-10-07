package paul.barthuel.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import paul.barthuel.go4lunch.R;

public class WorkmatesAdapter extends ListAdapter<WorkmatesInfo, WorkmatesAdapter.ViewHolder> {

    private final Listener listener;

    public WorkmatesAdapter(Listener listener) {
        super(new WorkmatesInfoDiffCallBack());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.workmates_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        viewHolder.bind(getItem(index), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewName;
        private final TextView mTextViewRestaurantName;
        private final ImageView mImageViewThumbnail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewName = itemView.findViewById(R.id.workmates_item_tv_username);
            mImageViewThumbnail = itemView.findViewById(R.id.workmates_item_iv_thumbnail);
            View mRoot = itemView.findViewById(R.id.workmates_item_cl);
            mTextViewRestaurantName = itemView.findViewById(R.id.workmates_item_tv_restaurant_name);
        }

        private void bind(final WorkmatesInfo workmatesInfo,
                          final Listener listener) {

            mTextViewName.setText(workmatesInfo.getName());
            Glide.with(mImageViewThumbnail)
                    .load(workmatesInfo.getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageViewThumbnail);
            mImageViewThumbnail.setOnClickListener(v -> listener.onWorkmateInfoClick(workmatesInfo));
            mTextViewRestaurantName.setText(workmatesInfo.getRestaurantName());
            mTextViewRestaurantName.setOnClickListener(v -> listener.onRestaurantClick(workmatesInfo.getPlaceId(), workmatesInfo.getRestaurantName()));
        }
    }

    public interface Listener {
        void onWorkmateInfoClick(WorkmatesInfo workmatesInfo);
        void onRestaurantClick(String id, String restaurantName);
    }
}
