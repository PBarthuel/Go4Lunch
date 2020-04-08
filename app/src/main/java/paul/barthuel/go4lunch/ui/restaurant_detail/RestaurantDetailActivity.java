package paul.barthuel.go4lunch.ui.restaurant_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import paul.barthuel.go4lunch.R;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String KEY_ID = "KEY_ID";

    public static Intent navigate(Context context, String id) {
        Intent intent = new Intent(context, RestaurantDetailActivity.class);
        intent.putExtra(KEY_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_activity);

        String id = getIntent().getStringExtra(KEY_ID);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RestaurantDetailFragment.newInstance(id))
                    .commitNow();
        }
    }
}
