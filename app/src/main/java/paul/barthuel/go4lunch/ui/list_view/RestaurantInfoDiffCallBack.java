package paul.barthuel.go4lunch.ui.list_view;

import androidx.annotation.NonNull;

class RestaurantInfoDiffCallBack extends androidx.recyclerview.widget.DiffUtil.ItemCallback<RestaurantInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull RestaurantInfo oldItem, @NonNull RestaurantInfo newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull RestaurantInfo oldItem, @NonNull RestaurantInfo newItem) {
        return oldItem.getName().equals(newItem.getName())
                && oldItem.getAddress().equals(newItem.getAddress())
                && oldItem.getDistance().equals(newItem.getDistance())
                //&& oldItem.getOpeningHours().equals(newItem.getOpeningHours())
                && oldItem.getImage().equals(newItem.getImage());
    }
}
