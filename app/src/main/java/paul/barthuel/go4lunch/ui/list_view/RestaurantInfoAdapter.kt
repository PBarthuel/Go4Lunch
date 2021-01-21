package paul.barthuel.go4lunch.ui.list_view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.ui.custom_view.CustomRatingBar

class RestaurantInfoAdapter(private val listener: Listener) : ListAdapter<RestaurantInfo, RestaurantInfoAdapter.ViewHolder>(RestaurantInfoDiffCallBack()) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.restaurant_description_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) {
        viewHolder.bind(getItem(index), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextViewName = itemView.findViewById<TextView>(R.id.restaurant_description_item_tv_name)
        private val mTextViewAddress = itemView.findViewById<TextView>(R.id.restaurant_description_item_address)
        private val mTextViewOpeningHours = itemView.findViewById<TextView>(R.id.restaurant_description_item_schedule)
        private val mTextViewDistance = itemView.findViewById<TextView>(R.id.restaurant_description_item_distance)
        private val mTextViewAttendies = itemView.findViewById<TextView>(R.id.restaurant_description_item_attendies)
        private val mRoot = itemView.findViewById<View>(R.id.restaurant_description_item_cl)
        private val mImageViewThumbnail = itemView.findViewById<ImageView>(R.id.restaurant_description_item_iv_thumbnail)
        private val mCustomRatingBar = itemView.findViewById<CustomRatingBar>(R.id.restaurant_description_item_crb_rating)
        private val mBackground = itemView.findViewById<View>(R.id.restaurant_description_item_background)
        fun bind(restaurantInfo: RestaurantInfo, listener: Listener) {
            mTextViewName.text = restaurantInfo.name
            mTextViewAddress.text = restaurantInfo.address
            mTextViewOpeningHours.text = restaurantInfo.openingHours
            mTextViewDistance.text = restaurantInfo.distance
            if (restaurantInfo.isAttendiesVisible) {
                mTextViewAttendies.text = restaurantInfo.attendies
                mTextViewAttendies.visibility = View.VISIBLE
            } else {
                mTextViewAttendies.visibility = View.INVISIBLE
            }
            mCustomRatingBar.setStars(restaurantInfo.rating)
            Log.d("courgette", "bind: name " + restaurantInfo.name + "rating " + restaurantInfo.rating)
            Glide.with(mImageViewThumbnail).load(restaurantInfo.image).into(mImageViewThumbnail)
            mRoot.setOnClickListener { listener.onRestaurantInfoClick(restaurantInfo) }
            mBackground.setBackgroundResource(restaurantInfo.backgroundColor)
        }

    }

    interface Listener {
        fun onRestaurantInfoClick(restaurantInfo: RestaurantInfo?)
    }

}