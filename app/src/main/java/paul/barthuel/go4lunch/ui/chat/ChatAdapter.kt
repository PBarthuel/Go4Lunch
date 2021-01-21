package paul.barthuel.go4lunch.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paul.barthuel.go4lunch.R

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var uiMessages: List<UiMessage>? = null
    fun setNewData(uiMessages: List<UiMessage>?) {
        this.uiMessages = uiMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewHolderType.RIGHT.ordinal) {
            RightViewHolder(LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.is_sender_item, viewGroup, false))
        } else {
            LeftViewHolder(LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.is_receiver_item, viewGroup, false))
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, index: Int) {
        if (getItemViewType(index) == ViewHolderType.LEFT.ordinal) {
            (viewHolder as LeftViewHolder).bind(uiMessages!![index])
        } else {
            (viewHolder as RightViewHolder).bind(uiMessages!![index])
        }
    }

    override fun getItemCount(): Int {
        return if (uiMessages == null) 0 else uiMessages!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (uiMessages!![position].isSender) {
            ViewHolderType.RIGHT.ordinal
        } else {
            ViewHolderType.LEFT.ordinal
        }
    }

    internal class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView = itemView.findViewById<TextView>(R.id.left_message_tv)
        private val mTextViewDate = itemView.findViewById<TextView>(R.id.left_date_tv)
        fun bind(uiMessage: UiMessage) {
            mTextView.text = uiMessage.message
            mTextViewDate.text = uiMessage.date
        }

    }

    internal class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView = itemView.findViewById<TextView>(R.id.right_message_tv)
        private val mTextViewDate = itemView.findViewById<TextView>(R.id.right_date_tv)
        fun bind(uiMessage: UiMessage) {
            mTextView.text = uiMessage.message
            mTextViewDate.text = uiMessage.date
        }

    }
}