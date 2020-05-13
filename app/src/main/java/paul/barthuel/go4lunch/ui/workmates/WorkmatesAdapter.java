package paul.barthuel.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import paul.barthuel.go4lunch.R;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder>  {

    private List<WorkmatesInfo> mList;

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.restaurant_description_item, viewGroup, false);

        return new WorkmatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder viewHolder, int index) {

        viewHolder.bind(mList.get(index));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setNewData(List<WorkmatesInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextViewName;
        private final ImageView mImageViewThumbnail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewName = itemView.findViewById(R.id.restaurant_description_item_tv_name);
            mImageViewThumbnail = itemView.findViewById(R.id.restaurant_description_item_iv_thumbnail);
        }

        private void bind(final WorkmatesInfo workmatesInfo) {

            mTextViewName.setText(workmatesInfo.getName());
            Glide.with(mImageViewThumbnail).load(workmatesInfo.getImage()).into(mImageViewThumbnail);
        }
    }
}
