package paul.barthuel.go4lunch.ui.restaurant_detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailFragment.Companion.newInstance

class RestaurantDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_detail_activity)
        val id = intent.getStringExtra(KEY_ID)
        val restaurantName = intent.getStringExtra(KEY_RESTAURANT_NAME)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.restaurant_detail_container, newInstance(id, restaurantName))
                    .commitNow()
        }
    }

    companion object {
        private const val KEY_ID = "KEY_ID"
        private const val KEY_RESTAURANT_NAME = "KEY_RESTAURANT_NAME"
        fun navigate(context: Context?, id: String?, restaurantName: String?): Intent {
            val intent = Intent(context, RestaurantDetailActivity::class.java)
            intent.putExtra(KEY_ID, id)
            intent.putExtra(KEY_RESTAURANT_NAME, restaurantName)
            return intent
        }
    }
}