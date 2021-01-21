package paul.barthuel.go4lunch.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import paul.barthuel.go4lunch.R
import paul.barthuel.go4lunch.injections.ViewModelFactory.Companion.instance

class ChatFragment : Fragment() {
    private var mViewModel: ChatViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this, instance).get(ChatViewModel::class.java)
        if (arguments != null) {
            arguments!!.getString(KEY_WORKMATE_ID)?.let { mViewModel!!.init(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val chatAdapter = ChatAdapter()
        val recyclerView: RecyclerView = view.findViewById(R.id.chat_rv)
        val editText = view.findViewById<EditText>(R.id.chat_et)
        val button = view.findViewById<Button>(R.id.chat_btn)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = chatAdapter
        mViewModel!!.uiModelsLiveData.observe(viewLifecycleOwner, Observer { uiMessages: List<UiMessage>? -> chatAdapter.setNewData(uiMessages) })
        button.setOnClickListener { mViewModel!!.sendMessage(editText.text.toString()) }
        return view
    }

    companion object {
        private const val KEY_WORKMATE_ID = "KEY_WORKMATE_ID"
        fun newInstance(workmateId: String?): ChatFragment {
            val bundle = Bundle()
            bundle.putString(KEY_WORKMATE_ID, workmateId)
            val fragment = ChatFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}