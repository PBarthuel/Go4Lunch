package paul.barthuel.go4lunch.ui.workmates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import paul.barthuel.go4lunch.R

class WorkmatesAdapter(private val listener: Listener) : ListAdapter<WorkmatesInfo, WorkmatesAdapter.ViewHolder>(WorkmatesInfoDiffCallBack()) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.workmates_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) {
        viewHolder.bind(getItem(index), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextViewName = itemView.findViewById<TextView>(R.id.workmates_item_tv_username)
        private val mTextViewRestaurantName = itemView.findViewById<TextView>(R.id.workmates_item_tv_restaurant_name)
        private val mImageViewThumbnail = itemView.findViewById<ImageView>(R.id.workmates_item_iv_thumbnail)
        fun bind(workmatesInfo: WorkmatesInfo?,
                         listener: Listener) {
            mTextViewName.text = workmatesInfo!!.name
            Glide.with(mImageViewThumbnail)
                    .load(workmatesInfo.image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageViewThumbnail)
            mImageViewThumbnail.setOnClickListener { listener.onWorkmateInfoClick(workmatesInfo) }
            mTextViewRestaurantName.text = workmatesInfo.restaurantName
            mTextViewRestaurantName.setOnClickListener { listener.onRestaurantClick(workmatesInfo.placeId, workmatesInfo.restaurantName) }
        }

    }

    interface Listener {
        fun onWorkmateInfoClick(workmatesInfo: WorkmatesInfo?)
        fun onRestaurantClick(id: String?, restaurantName: String?)
    }

}