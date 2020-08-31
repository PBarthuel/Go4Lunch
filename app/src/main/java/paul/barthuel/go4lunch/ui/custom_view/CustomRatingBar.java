package paul.barthuel.go4lunch.ui.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import paul.barthuel.go4lunch.R;

public class CustomRatingBar extends LinearLayout {
    public CustomRatingBar(Context context) {
        this(context, null);
    }

    public CustomRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setStars(double numberOfStars) {
        removeAllViews();
        for (int i = 0; i < numberOfStars; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.ic_baseline_star_border_24);
            addView(imageView);
        }
    }
}
