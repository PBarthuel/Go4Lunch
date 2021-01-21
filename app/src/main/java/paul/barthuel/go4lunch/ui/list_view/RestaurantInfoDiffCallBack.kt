package paul.barthuel.go4lunch.ui.list_view

import androidx.recyclerview.widget.DiffUtil

internal class RestaurantInfoDiffCallBack : DiffUtil.ItemCallback<RestaurantInfo>() {
    override fun areItemsTheSame(oldItem: RestaurantInfo, newItem: RestaurantInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RestaurantInfo, newItem: RestaurantInfo): Boolean {
        return oldItem.name == newItem.name
                && oldItem.address == newItem.address
                && oldItem.distance == newItem.distance
                && oldItem.attendies == newItem.attendies
                && oldItem.openingHours == newItem.openingHours
                && oldItem.backgroundColor == newItem.backgroundColor
    }
}