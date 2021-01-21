package paul.barthuel.go4lunch.ui.search_restaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import paul.barthuel.go4lunch.R

class AutocompleteRestaurantAdapter(private val onAutocompleteTextListener: OnAutocompleteTextListener) : ListAdapter<String, AutocompleteRestaurantAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem, ignoreCase = true)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem, ignoreCase = true)
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.restaurant_autocomplete_row,
                parent,
                false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = getItem(position)
        holder.textView.text = text
        holder.textView.setOnClickListener { onAutocompleteTextListener.onAutocompleteTextSelected(text) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.restaurant_autocomplete_row_tv)
    }
}