package paul.barthuel.go4lunch.ui.restaurant_detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.injections.ViewModelFactory.Companion.instance
import paul.barthuel.go4lunch.ui.chat.ChatActivity.Companion.navigate
import paul.barthuel.go4lunch.ui.workmates.WorkmatesAdapter
import paul.barthuel.go4lunch.ui.workmates.WorkmatesInfo

class RestaurantDetailFragment : Fragment(), WorkmatesAdapter.Listener {
    private var mViewModel: RestaurantDetailViewModel? = null
    private lateinit var textViewCall: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this, instance).get(RestaurantDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.restaurant_detail_fragment, container, false)
        val imageView = view.findViewById<ImageView>(R.id.restaurant_detail_iv)
        val textViewTitle = view.findViewById<TextView>(R.id.content_scrolling_restaurant_detail_title_tv)
        val textViewAddress = view.findViewById<TextView>(R.id.content_scrolling_restaurant_detail_address_tv)
        val textViewWebSite = view.findViewById<TextView>(R.id.content_scrolling_restaurant_detail_website_iv)
        textViewCall = view.findViewById(R.id.content_scrolling_restaurant_detail_call_iv)
        val textViewLike = view.findViewById<TextView>(R.id.content_scrolling_restaurant_detail_like_iv)
        val recyclerView = view.findViewById<RecyclerView>(R.id.content_scrolling_restaurant_detail_rv)
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.restaurant_detail_fab)
        val adapter = WorkmatesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        if (arguments != null) {
            arguments!!.getString(KEY_ID)?.let {
                mViewModel!!.init(it, arguments!!.getString(KEY_RESTAURANT_NAME))
            }
        }
        mViewModel!!.liveDataResultDetail.observe(viewLifecycleOwner, Observer { restaurantDetailInfo: RestaurantDetailInfo ->
            Glide.with(imageView).load(restaurantDetailInfo.image).into(imageView)
            textViewTitle.text = restaurantDetailInfo.name
            textViewAddress.text = restaurantDetailInfo.address
            textViewWebSite.setOnClickListener { onWebSiteClick(restaurantDetailInfo.url) }
            textViewCall.tag = restaurantDetailInfo.phoneNumber
            textViewCall.setOnClickListener { makePhoneCall() }
            textViewLike.setOnClickListener { mViewModel!!.likeRestaurant() }
            if (restaurantDetailInfo.isUserGoing) {
                floatingActionButton.setImageResource(R.drawable.ic_baseline_check_circle_24)
            } else {
                floatingActionButton.setImageDrawable(null)
            }
            floatingActionButton.setOnClickListener { mViewModel!!.goToRestaurant() }
        })
        mViewModel!!.liveDataWormatesInfos.observe(viewLifecycleOwner, Observer { list: List<WorkmatesInfo?> -> adapter.submitList(list) })
        return view
    }

    private fun makePhoneCall() {
        val phoneNumber = textViewCall.tag.toString()
        when {
            phoneNumber.trim { it <= ' ' }.isNotEmpty() -> {
                val dial = "tel:$phoneNumber"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(dial))
                val manager = requireContext().packageManager
                val infos = manager.queryIntentActivities(intent, 0)
                when {
                    infos.size > 0 -> {
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "No phone application", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
                Toast.makeText(activity, "no phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onWebSiteClick(url: String?) {
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        val builder = CustomTabsIntent.Builder()
        // set toolbar color and/or setting custom actions before invoking build()
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        val customTabsIntent = builder.build()
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    override fun onWorkmateInfoClick(workmatesInfo: WorkmatesInfo?) {
        startActivity(navigate(requireContext(), workmatesInfo!!.id))
    }

    override fun onRestaurantClick(id: String?, restaurantName: String?) {
        startActivity(RestaurantDetailActivity.navigate(requireContext(), id, restaurantName))
    }

    companion object {
        private const val KEY_ID = "KEY_ID"
        private const val KEY_RESTAURANT_NAME = "KEY_RESTAURANT_NAME"

        @JvmStatic
        fun newInstance(id: String?, restaurantName: String?): RestaurantDetailFragment {
            val bundle = Bundle()
            bundle.putString(KEY_ID, id)
            bundle.putString(KEY_RESTAURANT_NAME, restaurantName)
            val fragment = RestaurantDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}