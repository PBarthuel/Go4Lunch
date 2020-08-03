package paul.barthuel.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import paul.barthuel.go4lunch.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UiMessage> uiMessages;

    public void setNewData(List<UiMessage> uiMessages) {
        this.uiMessages = uiMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ViewHolderType.RIGHT.ordinal()) {
            return new RightViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.is_sender_item, viewGroup, false));
        } else {
            return new LeftViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.is_receiver_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int index) {
        //TODO a faire
        if (getItemViewType(index) == ViewHolderType.LEFT.ordinal()) {
            ((LeftViewHolder) viewHolder).bind(uiMessages.get(index));
        } else {
            ((RightViewHolder) viewHolder).bind(uiMessages.get(index));
        }
    }

    @Override
    public int getItemCount() {
        return uiMessages == null ? 0 : uiMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (uiMessages.get(position).isSender()) {
            return ViewHolderType.RIGHT.ordinal();
        } else {
            return ViewHolderType.LEFT.ordinal();
        }
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final TextView mTextViewDate;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.left_message_tv);
            mTextViewDate = itemView.findViewById(R.id.left_date_tv);
        }

        private void bind(final UiMessage uiMessage) {
            mTextView.setText(uiMessage.getMessage());
            mTextViewDate.setText(uiMessage.getDate());
        }
    }

    static class RightViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final TextView mTextViewDate;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.right_message_tv);
            mTextViewDate = itemView.findViewById(R.id.right_date_tv);
        }

        private void bind(final UiMessage uiMessage) {
            mTextView.setText(uiMessage.getMessage());
            mTextViewDate.setText(uiMessage.getDate());
        }
    }
}
