package paul.barthuel.go4lunch.ui.workmates

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
import paul.barthuel.go4lunch.injections.ViewModelFactory
import paul.barthuel.go4lunch.ui.chat.ChatActivity
import paul.barthuel.go4lunch.ui.restaurant_detail.RestaurantDetailActivity

class WorkmatesFragment : Fragment(), WorkmatesAdapter.Listener {
    private var mViewModel: WorkmatesViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this, ViewModelFactory.instance).get(WorkmatesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_view_fragment, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.list_restaurant_recycler_view)
        val adapter = WorkmatesAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mViewModel!!.uiModelsLiveData.observe(viewLifecycleOwner, Observer { list: List<WorkmatesInfo?> -> adapter.submitList(list) })
        return view
    }

    override fun onWorkmateInfoClick(workmatesInfo: WorkmatesInfo?) {
        startActivity(ChatActivity.navigate(requireContext(), workmatesInfo!!.id))
    }

    override fun onRestaurantClick(id: String?, restaurantName: String?) {
        if (restaurantName != "haven't decided yet") {
            startActivity(RestaurantDetailActivity.navigate(requireContext(), id, restaurantName))
        }
    }

    companion object {
        fun newInstance(): WorkmatesFragment {
            val args = Bundle()
            val fragment = WorkmatesFragment()
            fragment.arguments = args
            return fragment
        }
    }
}