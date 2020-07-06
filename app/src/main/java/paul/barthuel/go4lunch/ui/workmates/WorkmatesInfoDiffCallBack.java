package paul.barthuel.go4lunch.ui.workmates;

import androidx.annotation.NonNull;

class WorkmatesInfoDiffCallBack extends androidx.recyclerview.widget.DiffUtil.ItemCallback<WorkmatesInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull WorkmatesInfo oldItem, @NonNull WorkmatesInfo newItem) {
        return oldItem.getName().equals(newItem.getName());
    }

    @Override
    public boolean areContentsTheSame(@NonNull WorkmatesInfo oldItem, @NonNull WorkmatesInfo newItem) {
        return oldItem.getName().equals(newItem.getName())
                && oldItem.getImage().equals(newItem.getImage());
    }
}
