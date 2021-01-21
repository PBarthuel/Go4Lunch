package paul.barthuel.go4lunch.ui.workmates

import androidx.recyclerview.widget.DiffUtil

internal class WorkmatesInfoDiffCallBack : DiffUtil.ItemCallback<WorkmatesInfo>() {
    override fun areItemsTheSame(oldItem: WorkmatesInfo, newItem: WorkmatesInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: WorkmatesInfo, newItem: WorkmatesInfo): Boolean {
        return oldItem.name == newItem.name && oldItem.image == newItem.image
    }
}