package paul.barthuel.go4lunch.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import paul.barthuel.go4lunch.R;

public class ChatActivity extends AppCompatActivity {

    private static final String KEY_WORKMATE_ID = "KEY_WORKMATE_ID";

    public static Intent navigate(Context context, String workmateId) {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_WORKMATE_ID, workmateId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        String workmateId = getIntent().getStringExtra(KEY_WORKMATE_ID);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chat_container, ChatFragment.newInstance(workmateId))
                    .commitNow();
        }
    }

}
