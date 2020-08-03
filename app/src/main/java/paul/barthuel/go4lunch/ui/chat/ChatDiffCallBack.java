package paul.barthuel.go4lunch.ui.chat;

import androidx.annotation.NonNull;

public class ChatDiffCallBack extends androidx.recyclerview.widget.DiffUtil.ItemCallback<UiMessage> {

@Override
public boolean areItemsTheSame(@NonNull UiMessage oldItem, @NonNull UiMessage newItem) {
        return oldItem.getMessage().equals(newItem.getMessage());
        }

@Override
public boolean areContentsTheSame(@NonNull UiMessage oldItem, @NonNull UiMessage newItem) {
        return oldItem.getMessage().equals(newItem.getMessage())
                && oldItem.getDate().equals(newItem.getDate())
                && oldItem.getSenderName().equals(newItem.getSenderName());
        }
}
