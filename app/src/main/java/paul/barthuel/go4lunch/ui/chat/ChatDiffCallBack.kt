package paul.barthuel.go4lunch.ui.chat

import androidx.recyclerview.widget.DiffUtil

class ChatDiffCallBack : DiffUtil.ItemCallback<UiMessage>() {
    override fun areItemsTheSame(oldItem: UiMessage, newItem: UiMessage): Boolean {
        return oldItem.message == newItem.message
    }

    override fun areContentsTheSame(oldItem: UiMessage, newItem: UiMessage): Boolean {
        return oldItem.message == newItem.message && oldItem.date == newItem.date && oldItem.senderName == newItem.senderName
    }
}