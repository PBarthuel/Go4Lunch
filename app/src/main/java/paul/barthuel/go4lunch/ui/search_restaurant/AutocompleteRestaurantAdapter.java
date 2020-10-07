package paul.barthuel.go4lunch.ui.search_restaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import paul.barthuel.go4lunch.R;

public class AutocompleteRestaurantAdapter extends ListAdapter<String, AutocompleteRestaurantAdapter.ViewHolder> {

    public AutocompleteRestaurantAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equalsIgnoreCase(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equalsIgnoreCase(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_autocomplete_row,
                parent,
                false));
    }

    //TODO Supporter le click sur les nom de restaurants et aussi changer la tete de la recyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.restaurant_autocomplete_row_tv);
        }
    }
}
