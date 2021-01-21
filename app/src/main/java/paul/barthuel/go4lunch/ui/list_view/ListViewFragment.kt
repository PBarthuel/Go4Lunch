package paul.barthuel.go4lunch.ui.list_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.injections.ViewModelFactory.Companion.instance
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity

class ListViewFragment : Fragment(), RestaurantInfoAdapter.Listener {
    private var mViewModel: ListViewViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this, instance).get(ListViewViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_view_fragment, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_restaurant_recycler_view)
        val adapter = RestaurantInfoAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewModel!!.uiModelsLiveData.observe(viewLifecycleOwner, Observer<List<RestaurantInfo?>> { list: List<RestaurantInfo?> -> adapter.submitList(list) })
        return view
    }

    override fun onRestaurantInfoClick(restaurantInfo: RestaurantInfo?) {
        startActivity(RestaurantDetailActivity.navigate(requireContext(), restaurantInfo!!.id, restaurantInfo.name))
    }

    companion object {
        fun newInstance(): ListViewFragment {
            val args = Bundle()
            val fragment = ListViewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}