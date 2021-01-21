package paul.barthuel.go4lunch.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import paul.barthuel.go4lunch.R

class CustomRatingBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    fun setStars(numberOfStars: Double) {
        removeAllViews()
        var i = 0
        while (i < numberOfStars) {
            val imageView = ImageView(context)
            imageView.setImageResource(R.drawable.ic_baseline_star_border_24)
            addView(imageView)
            i++
        }
    }

    init {
        orientation = HORIZONTAL
    }
}